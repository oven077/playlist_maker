package com.agermolin.playlistmaker.library.domain.repository

import com.agermolin.playlistmaker.core.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}
