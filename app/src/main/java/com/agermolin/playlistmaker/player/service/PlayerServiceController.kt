package com.agermolin.playlistmaker.player.service

import com.agermolin.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceController {
    fun play()
    fun pause()
    fun stopPlayback()
    fun getPlayerState(): PlayerState
    fun startForegroundMode()
    fun stopForegroundMode()
    fun getPlaybackStateFlow(): StateFlow<PlayerServiceState>
}

data class PlayerServiceState(
    val playerState: PlayerState = PlayerState.DEFAULT,
    val currentPosition: Int = 0,
    val isPrepared: Boolean = false,
    val wasPrepared: Boolean = false,
    val error: String? = null,
)
