package com.agermolin.playlistmaker.player.domain.interactor

import com.agermolin.playlistmaker.player.domain.repository.PlayerRepository

interface IReleasePlayerInteractor {
    fun execute()
}

class ReleasePlayerInteractor(
    private val playerRepository: PlayerRepository
) : IReleasePlayerInteractor {
    override fun execute() {
        playerRepository.release()
    }
}
