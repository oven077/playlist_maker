package com.example.playlistmaker.data.datasource

import com.example.playlistmaker.data.api.ApiConstants
import com.example.playlistmaker.data.api.iTunesSearchAPI
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource(
    private val api: iTunesSearchAPI
) {
    fun searchTracks(
        query: String,
        callback: (Result<List<TrackDto>>) -> Unit
    ) {
        api.searchTrack(query).enqueue(object : Callback<TrackResponseDto> {
            override fun onResponse(
                call: Call<TrackResponseDto>,
                response: Response<TrackResponseDto>
            ) {
                if (response.isSuccessful && response.code() == ApiConstants.SUCCESS_CODE) {
                    val tracks = response.body()?.results ?: emptyList()
                    callback(Result.success(tracks))
                } else {
                    callback(Result.failure(Exception("HTTP Error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<TrackResponseDto>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
