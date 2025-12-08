package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class GetSearchHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    fun execute(): List<Track> {
        return historyRepository.getSearchHistory()
    }
}
