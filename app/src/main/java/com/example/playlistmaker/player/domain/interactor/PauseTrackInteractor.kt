package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

interface IPauseTrackInteractor {
    fun execute()
}

class PauseTrackInteractor(
    private val playerRepository: PlayerRepository
) : IPauseTrackInteractor {
    override fun execute() {
        playerRepository.pause()
    }
}
