package com.agermolin.playlistmaker.library.domain.interactor

import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

interface IGetPlaylistsInteractor {
    fun observePlaylists(): Flow<List<Playlist>>
}

class GetPlaylistsInteractor(
    private val playlistRepository: PlaylistRepository,
) : IGetPlaylistsInteractor {

    override fun observePlaylists(): Flow<List<Playlist>> =
        playlistRepository.observePlaylists()
}
