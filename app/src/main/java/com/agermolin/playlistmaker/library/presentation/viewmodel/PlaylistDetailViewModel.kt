package com.agermolin.playlistmaker.library.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.agermolin.playlistmaker.library.domain.interactor.IObservePlaylistDetailInteractor

class PlaylistDetailViewModel(
    playlistId: Long,
    observePlaylistDetailInteractor: IObservePlaylistDetailInteractor,
) : ViewModel() {

    val screenState = observePlaylistDetailInteractor.observePlaylistDetail(playlistId).asLiveData()
}
