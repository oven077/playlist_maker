package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

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
