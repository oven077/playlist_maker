package com.example.playlistmaker.core.di

import com.example.playlistmaker.player.data.datasource.PlayerDataSource
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.search.data.api.ApiConstants
import com.example.playlistmaker.search.data.api.iTunesSearchAPI
import com.example.playlistmaker.search.data.datasource.LocalDataSource
import com.example.playlistmaker.search.data.datasource.RemoteDataSource
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.search.domain.repository.SearchRepository
import com.example.playlistmaker.settings.data.datasource.SettingsDataSource
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    
    // Retrofit (только для Search)
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API
    single<iTunesSearchAPI> {
        get<Retrofit>().create(iTunesSearchAPI::class.java)
    }
    
    // Data Sources
    single<RemoteDataSource> {
        RemoteDataSource(get())
    }
    
    factory<LocalDataSource> {
        LocalDataSource(androidContext())
    }
    
    // Repositories
    single<SearchRepository> {
        SearchRepositoryImpl(get())
    }
    
    factory<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }
    
    // Settings Data Source and Repository
    factory<SettingsDataSource> {
        SettingsDataSource(androidContext())
    }
    
    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    
    // Player Data Source and Repository
    single<PlayerDataSource> {
        PlayerDataSource()
    }
    
    single<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }
}

