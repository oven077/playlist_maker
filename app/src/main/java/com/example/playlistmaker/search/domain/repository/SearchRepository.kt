package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.core.entity.Track

interface SearchRepository {
    fun searchTracks(
        query: String,
        callback: (Result<List<Track>>) -> Unit
    )
}

