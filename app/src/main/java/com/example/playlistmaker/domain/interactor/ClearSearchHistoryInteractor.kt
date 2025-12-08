package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.HistoryRepository

class ClearSearchHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    fun execute() {
        historyRepository.clearSearchHistory()
    }
}
