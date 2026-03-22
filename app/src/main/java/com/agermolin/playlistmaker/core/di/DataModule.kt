package com.agermolin.playlistmaker.core.di

import androidx.room.Room
import com.agermolin.playlistmaker.core.data.db.AppDatabase
import com.agermolin.playlistmaker.player.data.datasource.PlayerDataSource
import com.agermolin.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.agermolin.playlistmaker.player.domain.repository.PlayerRepository
import com.agermolin.playlistmaker.search.data.api.ApiConstants
import com.agermolin.playlistmaker.search.data.api.iTunesSearchAPI
import com.agermolin.playlistmaker.search.data.datasource.LocalDataSource
import com.agermolin.playlistmaker.search.data.datasource.RemoteDataSource
import com.agermolin.playlistmaker.core.data.db.MIGRATION_3_4
import com.agermolin.playlistmaker.library.data.repository.FavoritesRepositoryImpl
import com.agermolin.playlistmaker.library.data.repository.PlaylistRepositoryImpl
import com.agermolin.playlistmaker.library.domain.repository.FavoritesRepository
import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository
import com.google.gson.Gson
import com.agermolin.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.agermolin.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.agermolin.playlistmaker.search.domain.repository.HistoryRepository
import com.agermolin.playlistmaker.search.domain.repository.SearchRepository
import com.agermolin.playlistmaker.settings.data.datasource.SettingsDataSource
import com.agermolin.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.agermolin.playlistmaker.settings.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<Gson> { Gson() }

    // Room Database
    single<AppDatabase> {
        Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "playlist_maker.db",
            )
            .addMigrations(MIGRATION_3_4)
            .build()
    }

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
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(androidContext(), get<AppDatabase>().playlistDao(), get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }
    
    factory<HistoryRepository> {
        HistoryRepositoryImpl(get(), get())
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

