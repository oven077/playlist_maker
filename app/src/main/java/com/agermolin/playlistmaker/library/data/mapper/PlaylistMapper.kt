package com.agermolin.playlistmaker.library.data.mapper

import com.agermolin.playlistmaker.core.data.db.PlaylistEntity
import com.agermolin.playlistmaker.library.data.dto.PlaylistDto
import com.agermolin.playlistmaker.library.domain.model.Playlist

object PlaylistMapper {

    fun toDto(entity: PlaylistEntity): PlaylistDto = PlaylistDto(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        coverImagePath = entity.coverImagePath,
        trackIdsJson = entity.trackIdsJson,
        trackCount = entity.trackCount,
    )

    fun toDomain(dto: PlaylistDto): Playlist = Playlist(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        coverImagePath = dto.coverImagePath,
        trackCount = dto.trackCount,
    )

    fun toDomain(entity: PlaylistEntity): Playlist = toDomain(toDto(entity))
}
