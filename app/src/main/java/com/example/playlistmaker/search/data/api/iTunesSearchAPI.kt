package com.example.playlistmaker.search.data.api

import com.example.playlistmaker.search.data.dto.TrackResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesSearchAPI {
    @GET("search?entity=song")
    suspend fun searchTrack(
        @Query("term") text: String
    ): Response<TrackResponseDto>
}

