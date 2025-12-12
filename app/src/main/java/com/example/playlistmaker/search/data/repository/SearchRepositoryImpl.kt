package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.data.datasource.RemoteDataSource
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.repository.SearchRepository

class SearchRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : SearchRepository {
    override fun searchTracks(
        query: String,
        callback: (Result<List<Track>>) -> Unit
    ) {
        remoteDataSource.searchTracks(query) { result ->
            result.fold(
                onSuccess = { dtos ->
                    callback(Result.success(TrackMapper.map(dtos)))
                },
                onFailure = { error ->
                    callback(Result.failure(error))
                }
            )
        }
    }
}

