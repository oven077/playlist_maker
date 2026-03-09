package com.agermolin.playlistmaker.library.domain.interactor

import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.library.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IFavoritesInteractor {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    suspend fun isTrackFavorite(trackId: Long): Boolean
    fun getFavoriteTracks(): Flow<List<Track>>
}

class FavoritesInteractor(
    private val favoritesRepository: FavoritesRepository
) : IFavoritesInteractor {

    override suspend fun addTrack(track: Track) {
        favoritesRepository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        favoritesRepository.removeTrack(track)
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean =
        favoritesRepository.isTrackFavorite(trackId)

    override fun getFavoriteTracks(): Flow<List<Track>> =
        favoritesRepository.getFavoriteTracks().map { tracks ->
            // Последние добавленные в верхней части (DAO: ORDER BY addedAt DESC)
            tracks
        }
}
