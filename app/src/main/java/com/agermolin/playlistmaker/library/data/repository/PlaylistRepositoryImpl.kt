package com.agermolin.playlistmaker.library.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.room.withTransaction
import com.agermolin.playlistmaker.core.data.db.AppDatabase
import com.agermolin.playlistmaker.core.data.db.PlaylistEntity
import com.agermolin.playlistmaker.library.data.mapper.PlaylistMapper
import com.agermolin.playlistmaker.library.data.mapper.PlaylistTrackMapper
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.library.domain.model.PlaylistDetailResult
import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository
import com.agermolin.playlistmaker.core.entity.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class PlaylistRepositoryImpl(
    private val appContext: Context,
    private val database: AppDatabase,
    private val gson: Gson,
) : PlaylistRepository {

    private val playlistDao = database.playlistDao()
    private val playlistTrackDao = database.playlistTrackDao()

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
            entities.map { PlaylistMapper.toDomain(it, gson) }
        }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean =
        withContext(Dispatchers.IO) {
            database.withTransaction {
                val entity = playlistDao.getById(playlistId) ?: return@withTransaction false
                val ids = parseTrackIds(entity.trackIdsJson)
                if (track.trackId in ids) return@withTransaction false
                val newIds = ids + track.trackId
                val updated = entity.copy(
                    trackIdsJson = gson.toJson(newIds),
                    trackCount = newIds.size,
                )
                playlistTrackDao.insert(PlaylistTrackMapper.mapToEntity(track))
                playlistDao.update(updated)
                true
            }
        }

    override fun observePlaylistDetail(playlistId: Long): Flow<PlaylistDetailResult> =
        playlistDao.observeAll().mapLatest { entities ->
            val entity = entities.find { it.id == playlistId }
            if (entity == null) {
                PlaylistDetailResult.NotFound
            } else {
                val playlist = PlaylistMapper.toDomain(entity, gson)
                val ids = parseTrackIds(entity.trackIdsJson)
                val tracks = if (ids.isEmpty()) {
                    emptyList()
                } else {
                    val trackEntities = playlistTrackDao.getByIds(ids)
                    val byId = trackEntities.associateBy { it.trackId }
                    ids.mapNotNull { byId[it] }.map { PlaylistTrackMapper.toTrack(it) }
                }
                PlaylistDetailResult.Content(playlist, tracks)
            }
        }.flowOn(Dispatchers.IO)

    private fun parseTrackIds(json: String): List<Long> {
        return try {
            val type = object : TypeToken<List<Long>>() {}.type
            gson.fromJson<List<Long>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
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
