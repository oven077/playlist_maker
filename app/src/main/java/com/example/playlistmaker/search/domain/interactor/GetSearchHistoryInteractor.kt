package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository

interface IGetSearchHistoryInteractor {
    fun execute(): List<Track>
}

class GetSearchHistoryInteractor(
    private val historyRepository: HistoryRepository
) : IGetSearchHistoryInteractor {
    override fun execute(): List<Track> {
        return historyRepository.getSearchHistory()
    }
}
