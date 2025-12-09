package com.example.playlistmaker.core.di

import android.content.Context
import com.example.playlistmaker.search.data.api.ApiConstants
import com.example.playlistmaker.search.data.api.iTunesSearchAPI
import com.example.playlistmaker.search.data.datasource.LocalDataSource
import com.example.playlistmaker.search.data.datasource.RemoteDataSource
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.interactor.AddTrackToHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.ClearSearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.GetSearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.settings.data.datasource.SettingsDataSource
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.interactor.GetDarkThemeInteractor
import com.example.playlistmaker.settings.domain.interactor.SetDarkThemeInteractor
import com.example.playlistmaker.player.data.datasource.PlayerDataSource
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactor.GetCurrentPositionInteractor
import com.example.playlistmaker.player.domain.interactor.GetPlayerStateInteractor
import com.example.playlistmaker.player.domain.interactor.PauseTrackInteractor
import com.example.playlistmaker.player.domain.interactor.PlayTrackInteractor
import com.example.playlistmaker.player.domain.interactor.PreparePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.ReleasePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.SetOnCompletionListenerInteractor
import com.example.playlistmaker.player.domain.repository.PlayerRepository
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
        return SettingsRepositoryImpl(SettingsDataSource(context))
    }

    fun provideGetDarkThemeInteractor(context: Context): GetDarkThemeInteractor {
        return GetDarkThemeInteractor(provideSettingsRepository(context))
    }

    fun provideSetDarkThemeInteractor(context: Context): SetDarkThemeInteractor {
        return SetDarkThemeInteractor(provideSettingsRepository(context))
    }

    private var playerRepositoryInstance: PlayerRepository? = null

    private fun providePlayerDataSource(): PlayerDataSource {
        return PlayerDataSource()
    }

    fun providePlayerRepository(context: Context): PlayerRepository {
        if (playerRepositoryInstance == null) {
            playerRepositoryInstance = PlayerRepositoryImpl(providePlayerDataSource())
        }
        return playerRepositoryInstance!!
    }

    fun providePreparePlayerInteractor(context: Context): PreparePlayerInteractor {
        return PreparePlayerInteractor(providePlayerRepository(context))
    }

    fun providePlayTrackInteractor(context: Context): PlayTrackInteractor {
        return PlayTrackInteractor(providePlayerRepository(context))
    }

    fun providePauseTrackInteractor(context: Context): PauseTrackInteractor {
        return PauseTrackInteractor(providePlayerRepository(context))
    }

    fun provideGetPlayerStateInteractor(context: Context): GetPlayerStateInteractor {
        return GetPlayerStateInteractor(providePlayerRepository(context))
    }

    fun provideGetCurrentPositionInteractor(context: Context): GetCurrentPositionInteractor {
        return GetCurrentPositionInteractor(providePlayerRepository(context))
    }

    fun provideSetOnCompletionListenerInteractor(context: Context): SetOnCompletionListenerInteractor {
        return SetOnCompletionListenerInteractor(providePlayerRepository(context))
    }

    fun provideReleasePlayerInteractor(context: Context): ReleasePlayerInteractor {
        return ReleasePlayerInteractor(providePlayerRepository(context))
    }
}

