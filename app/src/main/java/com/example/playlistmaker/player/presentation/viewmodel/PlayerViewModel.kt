package com.example.playlistmaker.player.presentation.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.player.domain.interactor.IGetCurrentPositionInteractor
import com.example.playlistmaker.player.domain.interactor.IGetPlayerStateInteractor
import com.example.playlistmaker.player.domain.interactor.IPauseTrackInteractor
import com.example.playlistmaker.player.domain.interactor.IPlayTrackInteractor
import com.example.playlistmaker.player.domain.interactor.IPreparePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.IReleasePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.ISetOnCompletionListenerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerViewModel(
    application: Application,
    private val preparePlayerInteractor: IPreparePlayerInteractor,
    private val playTrackInteractor: IPlayTrackInteractor,
    private val pauseTrackInteractor: IPauseTrackInteractor,
    private val getPlayerStateInteractor: IGetPlayerStateInteractor,
    private val getCurrentPositionInteractor: IGetCurrentPositionInteractor,
    private val setOnCompletionListenerInteractor: ISetOnCompletionListenerInteractor,
    private val releasePlayerInteractor: IReleasePlayerInteractor
) : AndroidViewModel(application) {

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> = _screenState

    private val mainHandler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (getPlayerStateInteractor.execute() == PlayerState.PLAYING) {
                updateProgress()
                mainHandler.postDelayed(this, Constants.RELOAD_PROGRESS)
            }
        }
    }

    fun initTrack(track: Track) {
        _screenState.value = PlayerScreenState(
            track = track,
            isPrepared = false,
            isPlaying = false,
            currentPosition = 0,
            wasPrepared = false,
            error = null
        )

        if (track.previewUrl.isNotEmpty()) {
            preparePlayer(track.previewUrl)
        } else {
            _screenState.value = _screenState.value?.copy(error = "Preview not available")
        }
    }

    private fun preparePlayer(previewUrl: String) {
        setOnCompletionListenerInteractor.execute {
            mainHandler.removeCallbacks(updateProgressRunnable)
            val st = _screenState.value ?: return@execute
            
            mainHandler.post {
                _screenState.value = st.copy(
                    isPrepared = true,
                    isPlaying = false,
                    currentPosition = 0,
                    wasPrepared = true
                )
            }
        }

        preparePlayerInteractor.execute(
            previewUrl = previewUrl,
            onPrepared = {
                val st = _screenState.value ?: return@execute
                _screenState.value = st.copy(
                    isPrepared = true,
                    wasPrepared = true,
                    error = null
                )
            },
            onError = {
                val st = _screenState.value ?: return@execute
                _screenState.value = st.copy(isPrepared = false, error = "Failed to prepare player")
            }
        )
    }

    fun togglePlayback() {
        val playerState = getPlayerStateInteractor.execute()
        val st = _screenState.value ?: return

        when (playerState) {
            PlayerState.PLAYING -> {
                pause()
            }
            PlayerState.PAUSED,
            PlayerState.PREPARED -> {
                if (!st.isPrepared && st.wasPrepared) {
                    _screenState.value = st.copy(isPrepared = true)
                }
                play()
            }
            else -> {
                if (st.track?.previewUrl != null) {
                    preparePlayer(st.track.previewUrl)
                }
            }
        }
    }

    private fun play() {
        playTrackInteractor.execute()
        _screenState.value = _screenState.value?.copy(isPlaying = true)
        mainHandler.post(updateProgressRunnable)
    }

    private fun pause() {
        pauseTrackInteractor.execute()
        _screenState.value = _screenState.value?.copy(isPlaying = false)
        mainHandler.removeCallbacks(updateProgressRunnable)
    }

    private fun updateProgress() {
        val pos = getCurrentPositionInteractor.execute()
        _screenState.value = _screenState.value?.copy(currentPosition = pos)
    }

    fun onPause() {
        if (getPlayerStateInteractor.execute() == PlayerState.PLAYING) pause()
    }

    override fun onCleared() {
        super.onCleared()
        mainHandler.removeCallbacks(updateProgressRunnable)
        releasePlayerInteractor.execute()
    }
}

