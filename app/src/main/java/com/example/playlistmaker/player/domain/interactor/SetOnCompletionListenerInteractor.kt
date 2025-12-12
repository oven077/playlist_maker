package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

interface ISetOnCompletionListenerInteractor {
    fun execute(listener: () -> Unit)
}

class SetOnCompletionListenerInteractor(
    private val playerRepository: PlayerRepository
) : ISetOnCompletionListenerInteractor {
    override fun execute(listener: () -> Unit) {
        playerRepository.setOnCompletionListener(listener)
    }
}
