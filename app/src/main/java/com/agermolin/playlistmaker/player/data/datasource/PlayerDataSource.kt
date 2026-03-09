package com.agermolin.playlistmaker.player.data.datasource

import android.media.MediaPlayer

class PlayerDataSource {
    private var mediaPlayer: MediaPlayer? = null
    
    fun createMediaPlayer(): MediaPlayer {
        return MediaPlayer().also { mediaPlayer = it }
    }
    
    fun getMediaPlayer(): MediaPlayer? = mediaPlayer
    
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

