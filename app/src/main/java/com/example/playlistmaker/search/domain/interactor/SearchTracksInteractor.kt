package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.domain.repository.SearchRepository

class SearchTracksInteractor(
    private val searchRepository: SearchRepository
) {
    fun execute(
        query: String,
        callback: (Result<List<Track>>) -> Unit
    ) {
        if (query.isBlank()) {
            callback(Result.success(emptyList()))
            return
        }
        searchRepository.searchTracks(query, callback)
    }
}

