package com.example.playlistmaker.search.data.datasource

import com.example.playlistmaker.search.data.api.ApiConstants
import com.example.playlistmaker.search.data.api.iTunesSearchAPI
import com.example.playlistmaker.search.data.dto.TrackDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(
    private val api: iTunesSearchAPI
) {
    fun searchTracks(query: String): Flow<Result<List<TrackDto>>> = flow {
        val response = api.searchTrack(query)
        if (response.isSuccessful && response.code() == ApiConstants.SUCCESS_CODE) {
            val tracks = response.body()?.results ?: emptyList()
            emit(Result.success(tracks))
        } else {
            emit(Result.failure(Exception("HTTP Error: ${response.code()}")))
        }
    }.catch { e -> emit(Result.failure(e)) }
        .flowOn(Dispatchers.IO)
}

