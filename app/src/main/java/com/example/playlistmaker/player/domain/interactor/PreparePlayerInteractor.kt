package com.example.playlistmaker.player.domain.interactor

import android.util.Log
import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PreparePlayerInteractor(
    private val playerRepository: PlayerRepository
) {
    companion object {
        private const val TAG = "PreparePlayerInteractor"
    }
    
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
                Log.e(TAG, "Error preparing player", it)
                onError()
            }
        }
    }
}

