package com.agermolin.playlistmaker.library.domain.interactor

import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository

interface IRemoveTrackFromPlaylistInteractor {
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
}

class RemoveTrackFromPlaylistInteractor(
    private val playlistRepository: PlaylistRepository,
) : IRemoveTrackFromPlaylistInteractor {

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }
}
