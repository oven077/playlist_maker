package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.repository.HistoryRepository

class ClearSearchHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    fun execute() {
        historyRepository.clearSearchHistory()
    }
}

