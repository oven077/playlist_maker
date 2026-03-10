package com.agermolin.playlistmaker.player.domain.interactor

import com.agermolin.playlistmaker.player.domain.model.PlayerState
import com.agermolin.playlistmaker.player.domain.repository.PlayerRepository

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
