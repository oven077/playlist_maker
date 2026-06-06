package com.agermolin.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.main.presentation.activity.MainActivity
import com.agermolin.playlistmaker.player.domain.model.PlayerState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerService : Service(), PlayerServiceController {

    companion object {
        private const val TAG = "PlayerService"
        private const val PLAYER_CHANNEL_ID = "player_channel_id"
        private const val PLAYER_NOTIFICATION_ID = 1001
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val binder = PlayerBinder()
    private val gson = Gson()

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var currentTrack: Track? = null
    private var notificationTrackInfo: String = ""
    private var isForegroundModeActive = false

    private val playbackState = MutableStateFlow(PlayerServiceState())

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initMediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder {
        handleBindIntent(intent)
        return binder
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopPlayback()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForegroundMode()
        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        serviceScope.cancel()
    }

    override fun play() {
        val player = mediaPlayer ?: return
        val state = playbackState.value.playerState
        if (state != PlayerState.PREPARED && state != PlayerState.PAUSED) return

        try {
            if ((player.duration > 0) && (player.currentPosition >= player.duration - 100)) {
                player.seekTo(0)
            }
            player.start()
            playbackState.value = playbackState.value.copy(
                playerState = PlayerState.PLAYING,
                currentPosition = player.currentPosition,
                isPrepared = false,
                wasPrepared = true,
                error = null,
            )
            startProgressUpdates()
        } catch (exception: IllegalStateException) {
            Log.e(TAG, "Unable to start playback", exception)
            playbackState.value = playbackState.value.copy(
                playerState = PlayerState.DEFAULT,
                currentPosition = 0,
                isPrepared = false,
                wasPrepared = false,
                error = "Failed to start playback",
            )
        }
    }

    override fun pause() {
        val player = mediaPlayer ?: return
        if (playbackState.value.playerState != PlayerState.PLAYING) return

        player.pause()
        stopProgressUpdates()
        stopForegroundMode()
        playbackState.value = playbackState.value.copy(
            playerState = PlayerState.PAUSED,
            currentPosition = player.currentPosition,
            isPrepared = false,
            wasPrepared = true,
            error = null,
        )
    }

    override fun stopPlayback() {
        stopProgressUpdates()
        stopForegroundMode()
        mediaPlayer?.reset()
        playbackState.value = PlayerServiceState()
        currentTrack = null
        notificationTrackInfo = ""
        initMediaPlayer()
    }

    override fun getPlayerState(): PlayerState = playbackState.value.playerState

    override fun startForegroundMode() {
        if (playbackState.value.playerState != PlayerState.PLAYING) return

        try {
            ServiceCompat.startForeground(
                this,
                PLAYER_NOTIFICATION_ID,
                createNotification(),
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
            isForegroundModeActive = true
        } catch (exception: Exception) {
            Log.e(TAG, "Unable to start foreground mode", exception)
        }
    }

    override fun stopForegroundMode() {
        if (!isForegroundModeActive) return
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        isForegroundModeActive = false
    }

    override fun getPlaybackStateFlow(): StateFlow<PlayerServiceState> = playbackState.asStateFlow()

    private fun handleBindIntent(intent: Intent?) {
        val trackJson = intent?.getStringExtra(Constants.TRACK).orEmpty()
        if (trackJson.isBlank()) return

        val track = runCatching { gson.fromJson(trackJson, Track::class.java) }.getOrNull() ?: return
        notificationTrackInfo = "${track.artistName} - ${track.trackName}"

        val currentTrackId = currentTrack?.trackId
        currentTrack = track

        if (currentTrackId == track.trackId && playbackState.value.playerState != PlayerState.DEFAULT) {
            return
        }

        prepareTrack(track)
    }

    private fun initMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                playbackState.value = playbackState.value.copy(
                    playerState = PlayerState.PREPARED,
                    currentPosition = 0,
                    isPrepared = true,
                    wasPrepared = true,
                    error = null,
                )
            }

            setOnCompletionListener { player ->
                stopProgressUpdates()
                stopForegroundMode()
                player.seekTo(0)
                playbackState.value = playbackState.value.copy(
                    playerState = PlayerState.PREPARED,
                    currentPosition = 0,
                    isPrepared = true,
                    wasPrepared = true,
                    error = null,
                )
            }

            setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                stopProgressUpdates()
                stopForegroundMode()
                playbackState.value = PlayerServiceState(error = "Playback error")
                true
            }
        }
    }

    private fun prepareTrack(track: Track) {
        val previewUrl = track.previewUrl
        if (previewUrl.isBlank()) {
            playbackState.value = PlayerServiceState(error = "Preview not available")
            return
        }

        val player = mediaPlayer ?: return

        playbackState.value = playbackState.value.copy(
            playerState = PlayerState.PREPARING,
            currentPosition = 0,
            isPrepared = false,
            wasPrepared = false,
            error = null,
        )

        try {
            player.reset()
            player.setDataSource(previewUrl)
            player.prepareAsync()
        } catch (exception: Exception) {
            Log.e(TAG, "Error preparing track", exception)
            playbackState.value = PlayerServiceState(error = "Failed to prepare player")
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = serviceScope.launch {
            while (isActive) {
                val player = mediaPlayer ?: break
                if (playbackState.value.playerState == PlayerState.PLAYING) {
                    playbackState.value = playbackState.value.copy(
                        currentPosition = player.currentPosition,
                    )
                }
                delay(Constants.RELOAD_PROGRESS)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, PLAYER_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(notificationTrackInfo)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            PLAYER_CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW,
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}
