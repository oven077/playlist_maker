package com.agermolin.playlistmaker.core.di

import com.agermolin.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.agermolin.playlistmaker.search.presentation.viewmodel.SearchViewModel
import com.agermolin.playlistmaker.library.presentation.viewmodel.FavoritesTracksViewModel
import com.agermolin.playlistmaker.library.presentation.viewmodel.LibraryViewModel
import com.agermolin.playlistmaker.library.presentation.viewmodel.NewPlaylistViewModel
import com.agermolin.playlistmaker.library.presentation.viewmodel.PlaylistDetailViewModel
import com.agermolin.playlistmaker.library.presentation.viewmodel.PlaylistsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    
    viewModel { 
        SearchViewModel(
            androidApplication(),
            get(),
            get(),
            get(),
            get()
        ) 
    }
    
    viewModel { 
        PlayerViewModel(
            androidApplication(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        ) 
    }

    viewModel { LibraryViewModel() }
    viewModel { FavoritesTracksViewModel(get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { NewPlaylistViewModel(get()) }

    viewModel { (playlistId: Long) ->
        PlaylistDetailViewModel(playlistId, get(), get())
    }
}

