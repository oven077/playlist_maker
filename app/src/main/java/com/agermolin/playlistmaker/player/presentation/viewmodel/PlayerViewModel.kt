package com.agermolin.playlistmaker.player.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.core.presentation.Event
import com.agermolin.playlistmaker.library.domain.interactor.IAddTrackToPlaylistInteractor
import com.agermolin.playlistmaker.library.domain.interactor.IFavoritesInteractor
import com.agermolin.playlistmaker.library.domain.interactor.IGetPlaylistsInteractor
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.player.domain.model.PlayerState
import com.agermolin.playlistmaker.player.service.PlayerServiceController
import com.agermolin.playlistmaker.player.service.PlayerServiceState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerViewModel(
    application: Application,
    private val favoritesInteractor: IFavoritesInteractor,
    private val getPlaylistsInteractor: IGetPlaylistsInteractor,
    private val addTrackToPlaylistInteractor: IAddTrackToPlaylistInteractor,
) : AndroidViewModel(application) {

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> = _screenState

    val playlists: LiveData<List<Playlist>> =
        getPlaylistsInteractor.observePlaylists().asLiveData()

    private val _addTrackToPlaylistEvent = MutableLiveData<Event<AddTrackToPlaylistUiEvent>>()
    val addTrackToPlaylistEvent: LiveData<Event<AddTrackToPlaylistUiEvent>> = _addTrackToPlaylistEvent

    private var serviceStateJob: Job? = null
    private var playerServiceController: PlayerServiceController? = null

    fun initTrack(track: Track) {
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.isTrackFavorite(track.trackId)
            val trackWithFavorite = track.copy(isFavorite = isFavorite)
            val currentState = _screenState.value ?: PlayerScreenState()
            _screenState.value = currentState.copy(
                track = trackWithFavorite,
            )
        }
    }

    fun attachService(controller: PlayerServiceController) {
        playerServiceController = controller
        serviceStateJob?.cancel()
        serviceStateJob = viewModelScope.launch {
            controller.getPlaybackStateFlow().collect { state ->
                applyPlaybackState(state)
            }
        }
    }

    fun detachService() {
        serviceStateJob?.cancel()
        serviceStateJob = null
        playerServiceController = null
    }

    fun togglePlayback() {
        val serviceController = playerServiceController ?: return
        val playerState = serviceController.getPlayerState()

        when (playerState) {
            PlayerState.PLAYING -> serviceController.pause()
            PlayerState.PAUSED,
            PlayerState.PREPARED -> serviceController.play()
            else -> Unit
        }
    }

    fun onUiStarted() {
        playerServiceController?.stopForegroundMode()
    }

    fun onUiStopped(canShowNotification: Boolean) {
        val serviceController = playerServiceController ?: return
        if (!canShowNotification) return
        if (serviceController.getPlayerState() == PlayerState.PLAYING) {
            serviceController.startForegroundMode()
        }
    }

    fun onScreenClosed() {
        playerServiceController?.stopForegroundMode()
        playerServiceController?.stopPlayback()
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

    fun onPlaylistPicked(playlist: Playlist) {
        val track = _screenState.value?.track ?: return
        if (track.trackId in playlist.trackIds) {
            _addTrackToPlaylistEvent.value = Event(AddTrackToPlaylistUiEvent.AlreadyInPlaylist(playlist.name))
            return
        }
        viewModelScope.launch {
            val ok = addTrackToPlaylistInteractor.addTrackToPlaylist(playlist.id, track)
            if (ok) {
                _addTrackToPlaylistEvent.postValue(Event(AddTrackToPlaylistUiEvent.Added(playlist.name)))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        serviceStateJob?.cancel()
    }

    private fun applyPlaybackState(state: PlayerServiceState) {
        val currentState = _screenState.value ?: PlayerScreenState()
        _screenState.value = currentState.copy(
            isPrepared = state.isPrepared,
            isPlaying = state.playerState == PlayerState.PLAYING,
            currentPosition = state.currentPosition,
            error = state.error,
            wasPrepared = state.wasPrepared,
        )
    }
}

sealed interface AddTrackToPlaylistUiEvent {
    data class Added(val playlistName: String) : AddTrackToPlaylistUiEvent
    data class AlreadyInPlaylist(val playlistName: String) : AddTrackToPlaylistUiEvent
}
