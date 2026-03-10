package com.agermolin.playlistmaker.search.data.repository

import com.agermolin.playlistmaker.core.data.db.AppDatabase
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.search.data.datasource.LocalDataSource
import com.agermolin.playlistmaker.search.data.mapper.TrackMapper
import com.agermolin.playlistmaker.search.domain.repository.HistoryRepository
import kotlinx.coroutines.runBlocking

class HistoryRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val database: AppDatabase
) : HistoryRepository {
    override fun getSearchHistory(): List<Track> = runBlocking {
        val tracks = TrackMapper.map(localDataSource.getSearchHistory())
        val favoriteIds = database.favoriteTrackDao().getFavoriteTrackIds()
        tracks.forEach { it.isFavorite = it.trackId in favoriteIds }
        tracks
    }

    override fun addTrackToHistory(track: Track) {
        localDataSource.addTrackToHistory(TrackMapper.mapToDto(track))
    }

    override fun clearSearchHistory() {
        localDataSource.clearSearchHistory()
    }

    override fun hasSearchHistory(): Boolean {
        return localDataSource.getSearchHistory().isNotEmpty()
    }
}

