package com.example.playlistmaker.player.presentation.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.core.di.Creator
import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.player.domain.interactor.GetCurrentPositionInteractor
import com.example.playlistmaker.player.domain.interactor.GetPlayerStateInteractor
import com.example.playlistmaker.player.domain.interactor.PauseTrackInteractor
import com.example.playlistmaker.player.domain.interactor.PlayTrackInteractor
import com.example.playlistmaker.player.domain.interactor.PreparePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.ReleasePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.SetOnCompletionListenerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "PlayerViewModel"
    }

    private val preparePlayerInteractor = Creator.providePreparePlayerInteractor(getApplication())
    private val playTrackInteractor = Creator.providePlayTrackInteractor(getApplication())
    private val pauseTrackInteractor = Creator.providePauseTrackInteractor(getApplication())
    private val getPlayerStateInteractor = Creator.provideGetPlayerStateInteractor(getApplication())
    private val getCurrentPositionInteractor = Creator.provideGetCurrentPositionInteractor(getApplication())
    private val setOnCompletionListenerInteractor = Creator.provideSetOnCompletionListenerInteractor(getApplication())
    private val releasePlayerInteractor = Creator.provideReleasePlayerInteractor(getApplication())

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
            
            // После завершения трека MediaPlayer находится в состоянии PREPARED
            // Сохраняем isPrepared = true и wasPrepared = true, чтобы кнопка оставалась активной
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
                // Если плеер готов, просто играем
                // Но если isPrepared = false (после завершения), обновляем состояние
                if (!st.isPrepared && st.wasPrepared) {
                    _screenState.value = st.copy(isPrepared = true)
                }
                play()
            }
            else -> {
                // Если трек закончился или в неожиданном состоянии, готовим заново
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

