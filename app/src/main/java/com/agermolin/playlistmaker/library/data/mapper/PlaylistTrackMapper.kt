package com.agermolin.playlistmaker.library.data.mapper

import com.agermolin.playlistmaker.core.data.db.PlaylistTrackEntity
import com.agermolin.playlistmaker.core.entity.Track

object PlaylistTrackMapper {

    fun toTrack(entity: PlaylistTrackEntity): Track = Track(
        trackId = entity.trackId,
        trackName = entity.trackName,
        artistName = entity.artistName,
        trackTimeMillis = entity.trackTimeMillis,
        artworkUrl100 = entity.artworkUrl100,
        collectionName = entity.collectionName,
        releaseDate = entity.releaseDate,
        primaryGenreName = entity.primaryGenreName,
        country = entity.country,
        previewUrl = entity.previewUrl,
        isFavorite = false,
    )

    fun mapToEntity(track: Track): PlaylistTrackEntity = PlaylistTrackEntity(
        trackId = track.trackId,
        artworkUrl100 = track.artworkUrl100,
        trackName = track.trackName,
        artistName = track.artistName,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        trackTimeMillis = track.trackTimeMillis,
        previewUrl = track.previewUrl,
        addedAt = System.currentTimeMillis(),
    )
}
