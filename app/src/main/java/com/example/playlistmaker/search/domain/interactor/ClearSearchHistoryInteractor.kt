package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.repository.HistoryRepository

interface IClearSearchHistoryInteractor {
    fun execute()
}

class ClearSearchHistoryInteractor(
    private val historyRepository: HistoryRepository
) : IClearSearchHistoryInteractor {
    override fun execute() {
        historyRepository.clearSearchHistory()
    }
}
