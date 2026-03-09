package com.agermolin.playlistmaker.search.domain.repository

import com.agermolin.playlistmaker.core.entity.Track

interface HistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
    fun hasSearchHistory(): Boolean
}

