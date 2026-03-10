package com.agermolin.playlistmaker.player.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.library.domain.interactor.IFavoritesInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IGetCurrentPositionInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IGetPlayerStateInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IPauseTrackInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IPlayTrackInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IPreparePlayerInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IReleasePlayerInteractor
import com.agermolin.playlistmaker.player.domain.interactor.ISetOnCompletionListenerInteractor
import com.agermolin.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(
    application: Application,
    private val favoritesInteractor: IFavoritesInteractor,
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

    private var progressJob: Job? = null

    fun initTrack(track: Track) {
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.isTrackFavorite(track.trackId)
            val trackWithFavorite = track.copy(isFavorite = isFavorite)
            _screenState.value = PlayerScreenState(
                track = trackWithFavorite,
                isPrepared = false,
                isPlaying = false,
                currentPosition = 0,
                wasPrepared = false,
                error = null
            )

            if (trackWithFavorite.previewUrl.isNotEmpty()) {
                preparePlayer(trackWithFavorite.previewUrl)
            } else {
                _screenState.value = _screenState.value?.copy(error = "Preview not available")
            }
        }
    }

    private fun preparePlayer(previewUrl: String) {
        setOnCompletionListenerInteractor.execute {
            progressJob?.cancel()
            progressJob = null
            val st = _screenState.value ?: return@execute
            viewModelScope.launch {
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
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                if (getPlayerStateInteractor.execute() == PlayerState.PLAYING) {
                    val pos = getCurrentPositionInteractor.execute()
                    _screenState.value = _screenState.value?.copy(currentPosition = pos)
                }
                delay(Constants.RELOAD_PROGRESS)
            }
        }
    }

    private fun pause() {
        pauseTrackInteractor.execute()
        _screenState.value = _screenState.value?.copy(isPlaying = false)
        progressJob?.cancel()
        progressJob = null
    }

    fun onFavoriteClicked() {
        val st = _screenState.value ?: return
        val track = st.track ?: return
        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesInteractor.removeTrack(track)
            } else {
                favoritesInteractor.addTrack(track)
            }
            val updatedTrack = track.copy(isFavorite = !track.isFavorite)
            _screenState.postValue(st.copy(track = updatedTrack))
        }
    }

    fun onPause() {
        if (getPlayerStateInteractor.execute() == PlayerState.PLAYING) pause()
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        releasePlayerInteractor.execute()
    }
}

