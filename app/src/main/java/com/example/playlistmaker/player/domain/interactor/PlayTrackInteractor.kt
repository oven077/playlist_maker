package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayTrackInteractor(
    private val playerRepository: PlayerRepository
) {
    fun execute() {
        playerRepository.play()
    }
}

