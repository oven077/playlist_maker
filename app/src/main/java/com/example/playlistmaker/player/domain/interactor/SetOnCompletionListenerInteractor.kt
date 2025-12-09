package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

class SetOnCompletionListenerInteractor(
    private val playerRepository: PlayerRepository
) {
    fun execute(listener: () -> Unit) {
        playerRepository.setOnCompletionListener(listener)
    }
}

