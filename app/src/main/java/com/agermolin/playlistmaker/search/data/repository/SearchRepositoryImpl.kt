package com.agermolin.playlistmaker.search.data.repository

import com.agermolin.playlistmaker.core.data.db.AppDatabase
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.search.data.datasource.RemoteDataSource
import com.agermolin.playlistmaker.search.data.mapper.TrackMapper
import com.agermolin.playlistmaker.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val database: AppDatabase
) : SearchRepository {
    override fun searchTracks(query: String): Flow<Result<List<Track>>> =
        remoteDataSource.searchTracks(query).map { result ->
            result.fold(
                onSuccess = { dtos ->
                    val tracks = TrackMapper.map(dtos)
                    val favoriteIds = database.favoriteTrackDao().getFavoriteTrackIds()
                    tracks.forEach { it.isFavorite = it.trackId in favoriteIds }
                    kotlin.Result.success(tracks)
                },
                onFailure = { kotlin.Result.failure(it) }
            )
        }
}

