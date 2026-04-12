package com.agermolin.playlistmaker.library.domain.interactor

import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository

interface IDeletePlaylistInteractor {
    suspend fun deletePlaylist(playlistId: Long)
}

class DeletePlaylistInteractor(
    private val playlistRepository: PlaylistRepository,
) : IDeletePlaylistInteractor {

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistRepository.deletePlaylist(playlistId)
    }
}
