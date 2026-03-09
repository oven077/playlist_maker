package com.agermolin.playlistmaker.library.data.repository

import com.agermolin.playlistmaker.core.data.db.AppDatabase
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.library.data.mapper.FavoriteTrackMapper
import com.agermolin.playlistmaker.library.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val database: AppDatabase
) : FavoritesRepository {

    private val dao = database.favoriteTrackDao()

    override suspend fun addTrack(track: Track) {
        dao.insert(FavoriteTrackMapper.mapToEntity(track))
    }

    override suspend fun removeTrack(track: Track) {
        dao.delete(FavoriteTrackMapper.mapToEntity(track))
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean =
        dao.getFavoriteTrackIds().contains(trackId)

    override fun getFavoriteTracks(): Flow<List<Track>> =
        dao.getAll().map { FavoriteTrackMapper.map(it) }
}
