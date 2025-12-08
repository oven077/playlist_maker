package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entity.Track

interface SearchRepository {
    fun searchTracks(
        query: String,
        callback: (Result<List<Track>>) -> Unit
    )
}
