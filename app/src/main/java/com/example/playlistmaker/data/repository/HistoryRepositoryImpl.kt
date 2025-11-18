package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.datasource.LocalDataSource
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

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
