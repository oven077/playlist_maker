package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository

class AddTrackToHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    fun execute(track: Track) {
        historyRepository.addTrackToHistory(track)
    }
}

