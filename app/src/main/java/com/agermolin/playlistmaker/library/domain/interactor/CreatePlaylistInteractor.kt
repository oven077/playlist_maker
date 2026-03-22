package com.agermolin.playlistmaker.library.domain.interactor

import android.net.Uri
import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository

interface ICreatePlaylistInteractor {
    suspend fun execute(name: String, description: String, coverUri: Uri?): Long
}

class CreatePlaylistInteractor(
    private val playlistRepository: PlaylistRepository,
) : ICreatePlaylistInteractor {

    override suspend fun execute(name: String, description: String, coverUri: Uri?): Long =
        playlistRepository.createPlaylist(name, description, coverUri)
}
