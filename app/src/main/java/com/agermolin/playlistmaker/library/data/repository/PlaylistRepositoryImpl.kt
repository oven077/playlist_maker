package com.agermolin.playlistmaker.library.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.agermolin.playlistmaker.core.data.db.PlaylistDao
import com.agermolin.playlistmaker.core.data.db.PlaylistEntity
import com.agermolin.playlistmaker.library.data.mapper.PlaylistMapper
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class PlaylistRepositoryImpl(
    private val appContext: Context,
    private val playlistDao: PlaylistDao,
    private val gson: Gson,
) : PlaylistRepository {

    override suspend fun createPlaylist(name: String, description: String, coverUri: Uri?): Long =
        withContext(Dispatchers.IO) {
            val coverPath = coverUri?.let { copyCoverToAppStorage(it) }
            val entity = PlaylistEntity(
                name = name,
                description = description,
                coverImagePath = coverPath,
                trackIdsJson = gson.toJson(emptyList<Long>()),
                trackCount = 0,
            )
            playlistDao.insert(entity)
        }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) =
        withContext(Dispatchers.IO) {
            playlistDao.update(playlist)
        }

    override fun observePlaylists(): Flow<List<Playlist>> =
        playlistDao.observeAll().map { entities ->
            entities.map { PlaylistMapper.toDomain(it) }
        }

    private fun copyCoverToAppStorage(uri: Uri): String {
        val dir = File(appContext.filesDir, PLAYLIST_COVERS_DIR).apply { mkdirs() }
        val extension = resolveExtension(uri)
        val file = File(dir, "${UUID.randomUUID()}.$extension")
        appContext.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Cannot open cover stream" }
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file.absolutePath
    }

    private fun resolveExtension(uri: Uri): String {
        val mime = appContext.contentResolver.getType(uri)
        val fromMime = mime?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
        if (!fromMime.isNullOrBlank()) return fromMime
        val path = uri.lastPathSegment ?: return "jpg"
        val dot = path.lastIndexOf('.')
        if (dot >= 0 && dot < path.length - 1) {
            return path.substring(dot + 1).lowercase().takeIf { it.length <= 5 } ?: "jpg"
        }
        return "jpg"
    }

    companion object {
        private const val PLAYLIST_COVERS_DIR = "playlist_covers"
    }
}
