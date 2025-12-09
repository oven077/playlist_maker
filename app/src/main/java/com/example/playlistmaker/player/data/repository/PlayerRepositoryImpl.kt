package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.player.data.datasource.PlayerDataSource
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val playerDataSource: PlayerDataSource
) : PlayerRepository {
    
    companion object {
        private const val TAG = "PlayerRepositoryImpl"
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private var currentState = PlayerState.DEFAULT
    private var onPreparedCallback: (() -> Unit)? = null
    private var onCompletionCallback: (() -> Unit)? = null
    
    init {
        mediaPlayer = playerDataSource.createMediaPlayer()
        setupListeners()
    }
    
    private fun setupListeners() {
        mediaPlayer?.setOnPreparedListener {
            if (currentState == PlayerState.PREPARING) {
                currentState = PlayerState.PREPARED
            }
            onPreparedCallback?.invoke() ?: Log.w(TAG, "onPreparedCallback is null")
        }
        
        mediaPlayer?.setOnCompletionListener {
            currentState = PlayerState.PREPARED
            mediaPlayer?.seekTo(0)
            onCompletionCallback?.invoke() ?: Log.w(TAG, "onCompletionCallback is null")
        }
        
        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
            currentState = PlayerState.DEFAULT
            false
        }
    }
    
    override fun preparePlayer(previewUrl: String, callback: (Result<Unit>) -> Unit) {
        if (currentState == PlayerState.PREPARING) {
            return
        }
        
        currentState = PlayerState.PREPARING
        
        try {
            // Проверяем, что MediaPlayer существует, если нет - создаем заново
            if (mediaPlayer == null) {
                mediaPlayer = playerDataSource.createMediaPlayer()
                setupListeners()
            }
            
            // Всегда сбрасываем MediaPlayer перед установкой нового источника
            mediaPlayer?.reset()
            // После reset нужно переустановить listeners, так как они сбрасываются
            setupListeners()
            mediaPlayer?.setDataSource(previewUrl)
            mediaPlayer?.prepareAsync()
            callback(Result.success(Unit))
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing player", e)
            currentState = PlayerState.DEFAULT
            callback(Result.failure(e))
        }
    }
    
    override fun play() {
        if (currentState == PlayerState.PREPARED || currentState == PlayerState.PAUSED) {
            // Всегда сбрасываем позицию на начало перед воспроизведением, если трек завершен
            val duration = mediaPlayer?.duration ?: 0
            val currentPosition = mediaPlayer?.currentPosition ?: 0
            if (duration > 0 && currentPosition >= duration - 100) {
                mediaPlayer?.seekTo(0)
            }
            try {
                mediaPlayer?.start()
                currentState = PlayerState.PLAYING
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Error starting playback", e)
                currentState = PlayerState.DEFAULT
            }
        }
    }
    
    override fun pause() {
        if (currentState == PlayerState.PLAYING) {
            mediaPlayer?.pause()
            currentState = PlayerState.PAUSED
        }
    }
    
    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
    
    override fun getState(): PlayerState {
        return currentState
    }
    
    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentState = PlayerState.DEFAULT
        playerDataSource.release()
    }
    
    override fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedCallback = listener
        // Если MediaPlayer уже создан, устанавливаем listener сразу
        // Важно: listener будет переустановлен в setupListeners() после reset()
        if (mediaPlayer != null) {
            mediaPlayer?.setOnPreparedListener {
                currentState = PlayerState.PREPARED
                onPreparedCallback?.invoke() ?: Log.w(TAG, "onPreparedCallback is null")
            }
        }
    }
    
    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionCallback = listener
        // Переустанавливаем listener в MediaPlayer, чтобы убедиться, что он актуален
        mediaPlayer?.setOnCompletionListener {
            currentState = PlayerState.PREPARED
            mediaPlayer?.seekTo(0)
            onCompletionCallback?.invoke()
        }
    }
}

