package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository

class GetSearchHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    fun execute(): List<Track> {
        return historyRepository.getSearchHistory()
    }
}

