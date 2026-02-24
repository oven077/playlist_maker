package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ISearchTracksInteractor {
    fun execute(query: String): Flow<Result<List<Track>>>
}

class SearchTracksInteractor(
    private val searchRepository: SearchRepository
) : ISearchTracksInteractor {
    override fun execute(query: String): Flow<Result<List<Track>>> =
        if (query.isBlank()) flow { emit(Result.success(emptyList())) }
        else searchRepository.searchTracks(query)
}
