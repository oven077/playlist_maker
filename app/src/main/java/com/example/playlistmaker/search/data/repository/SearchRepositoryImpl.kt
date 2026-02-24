package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.data.datasource.RemoteDataSource
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : SearchRepository {
    override fun searchTracks(query: String): Flow<Result<List<Track>>> =
        remoteDataSource.searchTracks(query).map { result ->
            result.map { dtos -> TrackMapper.map(dtos) }
        }
}

