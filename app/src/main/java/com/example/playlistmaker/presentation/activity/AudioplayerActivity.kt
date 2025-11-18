package com.example.playlistmaker.presentation.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var trackName: TextView
    private lateinit var trackDuration: TextView
    private lateinit var artistName: TextView
    private lateinit var albumIcon: ImageView
    private lateinit var collectionName: TextView
    private lateinit var collectionTitle: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var trackProgress: TextView
    private lateinit var playButton: FloatingActionButton

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var mainHandler: Handler? = null
    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                trackProgress.text = timeFormat.format(mediaPlayer.currentPosition.toLong())
                mainHandler?.postDelayed(this, Constants.RELOAD_PROGRESS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        mainHandler = Handler(Looper.getMainLooper())

        initToolbar()
        initViews()
        initTrackInfo()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.player_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackDuration = findViewById(R.id.trackTime)
        albumIcon = findViewById(R.id.track_icon)
        collectionName = findViewById(R.id.album_name)
        collectionTitle = findViewById(R.id.album)
        releaseDate = findViewById(R.id.release_date_data)
        primaryGenreName = findViewById(R.id.primary_genre_name)
        country = findViewById(R.id.country_data)
        trackProgress = findViewById(R.id.progress)
        playButton = findViewById(R.id.play_track)

        playButton.isEnabled = false
        playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun initTrackInfo() {
        val track = Gson().fromJson(intent.getStringExtra(Constants.TRACK), Track::class.java)

        Glide
            .with(albumIcon)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder_512)
            .centerCrop()
            .transform(
                RoundedCorners(
                    resources.getDimensionPixelSize(R.dimen.corner_radius_8)
                )
            )
            .into(albumIcon)

        trackName.text = track.trackName
        artistName.text = track.artistName
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        trackDuration.text = timeFormat.format(track.trackTimeMillis.toLong())
        trackProgress.text = Constants.CURRENT_TIME_ZERO

        val release = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(track.releaseDate)
        release?.let {
            val formatted = SimpleDateFormat("yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(it)
            releaseDate.text = formatted
        }

        if (track.collectionName.isNotEmpty()) {
            collectionName.text = track.collectionName
            collectionName.visibility = View.VISIBLE
            collectionTitle.visibility = View.VISIBLE
        } else {
            collectionName.visibility = View.GONE
            collectionTitle.visibility = View.GONE
        }

        if (track.previewUrl.isNotEmpty()) {
            preparePlayer(track.previewUrl)
        } else {
            playButton.isEnabled = false
            trackProgress.text = Constants.CURRENT_TIME_ZERO
        }
    }

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            playButton.isEnabled = true
            playButton.setImageResource(R.drawable.play)
        }

        mediaPlayer.setOnCompletionListener {
            mainHandler?.removeCallbacks(updateProgressRunnable)
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.play)
            trackProgress.text = Constants.CURRENT_TIME_ZERO
        }

        try {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            playButton.isEnabled = false
            trackProgress.text = Constants.CURRENT_TIME_ZERO
            return
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
            else -> Unit
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        mainHandler?.post(updateProgressRunnable)
    }

    private fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            playerState = STATE_PAUSED
        }
        playButton.setImageResource(R.drawable.play)
        mainHandler?.removeCallbacks(updateProgressRunnable)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler?.removeCallbacks(updateProgressRunnable)
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}

