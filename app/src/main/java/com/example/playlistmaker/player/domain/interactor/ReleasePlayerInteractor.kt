package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

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
