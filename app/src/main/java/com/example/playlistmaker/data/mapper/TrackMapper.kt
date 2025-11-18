package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.entity.Track

object TrackMapper {
    fun map(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName ?: "",
            artistName = dto.artistName ?: "",
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100 ?: "",
            collectionName = dto.collectionName ?: "",
            releaseDate = dto.releaseDate ?: "",
            primaryGenreName = dto.primaryGenreName ?: "",
            country = dto.country ?: "",
            previewUrl = dto.previewUrl ?: ""
        )
    }

    fun map(dtos: List<TrackDto>): List<Track> {
        return dtos.map { map(it) }
    }

    fun mapToDto(track: Track): TrackDto {
        return TrackDto(
            trackId = track.trackId,
            trackName = track.trackName.takeIf { it.isNotEmpty() },
            artistName = track.artistName.takeIf { it.isNotEmpty() },
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100.takeIf { it.isNotEmpty() },
            collectionName = track.collectionName.takeIf { it.isNotEmpty() },
            releaseDate = track.releaseDate.takeIf { it.isNotEmpty() },
            primaryGenreName = track.primaryGenreName.takeIf { it.isNotEmpty() },
            country = track.country.takeIf { it.isNotEmpty() },
            previewUrl = track.previewUrl.takeIf { it.isNotEmpty() }
        )
    }

    fun mapToDto(tracks: List<Track>): List<TrackDto> {
        return tracks.map { mapToDto(it) }
    }
}

