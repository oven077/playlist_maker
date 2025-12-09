package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.data.datasource.LocalDataSource
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val localDataSource: LocalDataSource
) : HistoryRepository {
    override fun getSearchHistory(): List<Track> {
        return TrackMapper.map(localDataSource.getSearchHistory())
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

