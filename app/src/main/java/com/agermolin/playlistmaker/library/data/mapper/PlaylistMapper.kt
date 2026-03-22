package com.agermolin.playlistmaker.library.data.mapper

import com.agermolin.playlistmaker.core.data.db.PlaylistEntity
import com.agermolin.playlistmaker.library.data.dto.PlaylistDto
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PlaylistMapper {

    fun toDto(entity: PlaylistEntity): PlaylistDto = PlaylistDto(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        coverImagePath = entity.coverImagePath,
        trackIdsJson = entity.trackIdsJson,
        trackCount = entity.trackCount,
    )

    fun toDomain(dto: PlaylistDto, gson: Gson): Playlist = Playlist(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        coverImagePath = dto.coverImagePath,
        trackCount = dto.trackCount,
        trackIds = parseTrackIds(dto.trackIdsJson, gson),
    )

    fun toDomain(entity: PlaylistEntity, gson: Gson): Playlist = toDomain(toDto(entity), gson)

    private fun parseTrackIds(json: String, gson: Gson): List<Long> {
        return try {
            val type = object : TypeToken<List<Long>>() {}.type
            gson.fromJson<List<Long>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
