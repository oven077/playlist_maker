package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.datasource.RemoteDataSource
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.repository.SearchRepository

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
