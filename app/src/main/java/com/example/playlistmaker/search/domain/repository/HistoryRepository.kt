package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.core.entity.Track

interface HistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
    fun hasSearchHistory(): Boolean
}

