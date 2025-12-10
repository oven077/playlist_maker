package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PreparePlayerInteractor(
    private val playerRepository: PlayerRepository
) {
    fun execute(
        previewUrl: String,
        onPrepared: () -> Unit,
        onError: () -> Unit
    ) {
        if (previewUrl.isEmpty()) {
            onError()
            return
        }
        
        playerRepository.setOnPreparedListener(onPrepared)
        playerRepository.preparePlayer(previewUrl) { result ->
            result.onFailure {
                onError()
            }
        }
    }
}

