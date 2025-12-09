package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

class GetCurrentPositionInteractor(
    private val playerRepository: PlayerRepository
) {
    fun execute(): Int {
        return playerRepository.getCurrentPosition()
    }
}

