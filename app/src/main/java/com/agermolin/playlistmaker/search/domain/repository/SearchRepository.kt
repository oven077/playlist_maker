package com.agermolin.playlistmaker.search.domain.repository

import com.agermolin.playlistmaker.core.entity.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}

