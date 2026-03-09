package com.agermolin.playlistmaker.player.domain.interactor

import com.agermolin.playlistmaker.player.domain.repository.PlayerRepository

interface IPlayTrackInteractor {
    fun execute()
}

class PlayTrackInteractor(
    private val playerRepository: PlayerRepository
) : IPlayTrackInteractor {
    override fun execute() {
        playerRepository.play()
    }
}
