package com.agermolin.playlistmaker.player.domain.interactor

import com.agermolin.playlistmaker.player.domain.repository.PlayerRepository

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
