package com.agermolin.playlistmaker.library.domain.repository

import android.net.Uri
import com.agermolin.playlistmaker.core.data.db.PlaylistEntity
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.library.domain.model.PlaylistDetailResult
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(name: String, description: String, coverUri: Uri?): Long

    suspend fun updatePlaylist(playlist: PlaylistEntity)

    fun observePlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean

    fun observePlaylistDetail(playlistId: Long): Flow<PlaylistDetailResult>
}
