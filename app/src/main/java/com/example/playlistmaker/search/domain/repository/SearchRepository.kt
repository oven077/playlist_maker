package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.core.entity.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}

