package com.agermolin.playlistmaker.player.presentation.viewmodel

import com.agermolin.playlistmaker.core.entity.Track

data class PlayerScreenState(
    val track: Track? = null,
    val isPrepared: Boolean = false,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val error: String? = null,
    val wasPrepared: Boolean = false
)

