package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.repository.PlayerRepository

interface IGetPlayerStateInteractor {
    fun execute(): PlayerState
}

class GetPlayerStateInteractor(
    private val playerRepository: PlayerRepository
) : IGetPlayerStateInteractor {
    override fun execute(): PlayerState {
        return playerRepository.getState()
    }
}
