package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository

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
