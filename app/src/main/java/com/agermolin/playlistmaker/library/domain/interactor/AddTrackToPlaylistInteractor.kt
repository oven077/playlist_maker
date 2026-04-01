package com.agermolin.playlistmaker.library.domain.interactor

import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository

interface IAddTrackToPlaylistInteractor {
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean
}

class AddTrackToPlaylistInteractor(
    private val playlistRepository: PlaylistRepository,
) : IAddTrackToPlaylistInteractor {

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean =
        playlistRepository.addTrackToPlaylist(playlistId, track)
}
