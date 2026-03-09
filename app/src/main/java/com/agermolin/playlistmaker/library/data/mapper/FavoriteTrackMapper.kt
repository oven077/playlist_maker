package com.agermolin.playlistmaker.library.data.mapper

import com.agermolin.playlistmaker.core.data.db.FavoriteTrackEntity
import com.agermolin.playlistmaker.core.entity.Track

object FavoriteTrackMapper {
    fun map(entity: FavoriteTrackEntity): Track = Track(
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
        isFavorite = true
    )

    fun mapToEntity(track: Track): FavoriteTrackEntity = FavoriteTrackEntity(
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
        addedAt = System.currentTimeMillis()
    )

    fun map(entities: List<FavoriteTrackEntity>): List<Track> = entities.map { map(it) }
}
