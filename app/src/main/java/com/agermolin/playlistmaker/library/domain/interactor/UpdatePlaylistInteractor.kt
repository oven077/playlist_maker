package com.agermolin.playlistmaker.library.domain.interactor

import android.net.Uri
import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository

interface IUpdatePlaylistInteractor {
    suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        coverUri: Uri?,
    )
}

class UpdatePlaylistInteractor(
    private val playlistRepository: PlaylistRepository,
) : IUpdatePlaylistInteractor {

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        coverUri: Uri?,
    ) {
        playlistRepository.updatePlaylistInfo(playlistId, name, description, coverUri)
    }
}
