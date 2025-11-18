package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.SearchRepository

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
