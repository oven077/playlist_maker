package com.agermolin.playlistmaker.library.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.agermolin.playlistmaker.library.domain.interactor.IObservePlaylistDetailInteractor
import com.agermolin.playlistmaker.library.domain.interactor.IRemoveTrackFromPlaylistInteractor
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistId: Long,
    observePlaylistDetailInteractor: IObservePlaylistDetailInteractor,
    private val removeTrackFromPlaylistInteractor: IRemoveTrackFromPlaylistInteractor,
) : ViewModel() {

    val screenState = observePlaylistDetailInteractor.observePlaylistDetail(playlistId).asLiveData()

    fun removeTrack(trackId: Long) {
        viewModelScope.launch {
            removeTrackFromPlaylistInteractor.removeTrackFromPlaylist(playlistId, trackId)
        }
    }
}
