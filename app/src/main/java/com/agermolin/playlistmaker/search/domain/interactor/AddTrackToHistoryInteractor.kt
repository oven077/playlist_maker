package com.agermolin.playlistmaker.search.domain.interactor

import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.search.domain.repository.HistoryRepository

interface IAddTrackToHistoryInteractor {
    fun execute(track: Track)
}

class AddTrackToHistoryInteractor(
    private val historyRepository: HistoryRepository
) : IAddTrackToHistoryInteractor {
    override fun execute(track: Track) {
        historyRepository.addTrackToHistory(track)
    }
}
