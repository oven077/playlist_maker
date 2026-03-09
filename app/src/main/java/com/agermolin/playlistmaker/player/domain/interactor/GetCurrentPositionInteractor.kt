package com.agermolin.playlistmaker.player.domain.interactor

import com.agermolin.playlistmaker.player.domain.repository.PlayerRepository

interface IGetCurrentPositionInteractor {
    fun execute(): Int
}

class GetCurrentPositionInteractor(
    private val playerRepository: PlayerRepository
) : IGetCurrentPositionInteractor {
    override fun execute(): Int {
        return playerRepository.getCurrentPosition()
    }
}
