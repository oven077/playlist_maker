package com.example.playlistmaker.search.data.api

import com.example.playlistmaker.search.data.dto.TrackResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesSearchAPI {
    @GET("search?entity=song")
    fun searchTrack(
        @Query("term") text: String
    ): Call<TrackResponseDto>
}

