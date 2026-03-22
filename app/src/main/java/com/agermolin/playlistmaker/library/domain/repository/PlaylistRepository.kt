package com.agermolin.playlistmaker.library.domain.repository

import android.net.Uri
import com.agermolin.playlistmaker.core.data.db.PlaylistEntity
import com.agermolin.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(name: String, description: String, coverUri: Uri?): Long

    suspend fun updatePlaylist(playlist: PlaylistEntity)

    fun observePlaylists(): Flow<List<Playlist>>
}
