package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PauseTrackInteractor(
    private val playerRepository: PlayerRepository
) {
    fun execute() {
        playerRepository.pause()
    }
}

