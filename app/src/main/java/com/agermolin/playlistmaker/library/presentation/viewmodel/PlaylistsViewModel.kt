package com.agermolin.playlistmaker.library.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.agermolin.playlistmaker.library.domain.interactor.IGetPlaylistsInteractor
import com.agermolin.playlistmaker.library.domain.model.Playlist

class PlaylistsViewModel(
    private val getPlaylistsInteractor: IGetPlaylistsInteractor,
) : ViewModel() {

    val playlists: LiveData<List<Playlist>> =
        getPlaylistsInteractor.observePlaylists().asLiveData()
}
