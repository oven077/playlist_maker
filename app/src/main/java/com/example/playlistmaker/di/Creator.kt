package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.api.ApiConstants
import com.example.playlistmaker.data.api.iTunesSearchAPI
import com.example.playlistmaker.data.datasource.LocalDataSource
import com.example.playlistmaker.data.datasource.RemoteDataSource
import com.example.playlistmaker.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.domain.interactor.AddTrackToHistoryInteractor
import com.example.playlistmaker.domain.interactor.ClearSearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.GetDarkThemeInteractor
import com.example.playlistmaker.domain.interactor.GetSearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.interactor.SetDarkThemeInteractor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideApi(): iTunesSearchAPI {
        return provideRetrofit().create(iTunesSearchAPI::class.java)
    }

    private fun provideRemoteDataSource(): RemoteDataSource {
        return RemoteDataSource(provideApi())
    }

    private fun provideLocalDataSource(context: Context): LocalDataSource {
        return LocalDataSource(context)
    }

    fun provideSearchRepository(context: Context): SearchRepositoryImpl {
        return SearchRepositoryImpl(provideRemoteDataSource())
    }

    fun provideHistoryRepository(context: Context): HistoryRepositoryImpl {
        return HistoryRepositoryImpl(provideLocalDataSource(context))
    }

    fun provideSearchTracksInteractor(context: Context): SearchTracksInteractor {
        return SearchTracksInteractor(provideSearchRepository(context))
    }

    fun provideGetSearchHistoryInteractor(context: Context): GetSearchHistoryInteractor {
        return GetSearchHistoryInteractor(provideHistoryRepository(context))
    }

    fun provideAddTrackToHistoryInteractor(context: Context): AddTrackToHistoryInteractor {
        return AddTrackToHistoryInteractor(provideHistoryRepository(context))
    }

    fun provideClearSearchHistoryInteractor(context: Context): ClearSearchHistoryInteractor {
        return ClearSearchHistoryInteractor(provideHistoryRepository(context))
    }

    fun provideSettingsRepository(context: Context): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(provideLocalDataSource(context))
    }

    fun provideGetDarkThemeInteractor(context: Context): GetDarkThemeInteractor {
        return GetDarkThemeInteractor(provideSettingsRepository(context))
    }

    fun provideSetDarkThemeInteractor(context: Context): SetDarkThemeInteractor {
        return SetDarkThemeInteractor(provideSettingsRepository(context))
    }
}

