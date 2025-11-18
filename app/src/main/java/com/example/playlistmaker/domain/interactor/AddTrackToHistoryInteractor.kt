package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class AddTrackToHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    fun execute(track: Track) {
        historyRepository.addTrackToHistory(track)
    }
}
