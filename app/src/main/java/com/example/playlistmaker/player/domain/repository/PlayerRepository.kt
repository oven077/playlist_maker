package com.example.playlistmaker.player.domain.repository

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerRepository {
    fun preparePlayer(previewUrl: String, callback: (Result<Unit>) -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun getState(): PlayerState
    fun release()
    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
}

