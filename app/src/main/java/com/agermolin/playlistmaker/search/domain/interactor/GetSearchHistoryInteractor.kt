package com.agermolin.playlistmaker.search.domain.interactor

import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.search.domain.repository.HistoryRepository

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
