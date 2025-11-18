package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entity.Track

interface HistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
    fun hasSearchHistory(): Boolean
}
