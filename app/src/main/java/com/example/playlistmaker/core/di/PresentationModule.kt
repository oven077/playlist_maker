package com.example.playlistmaker.core.di

import com.example.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.presentation.viewmodel.SearchViewModel
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
            get()
        ) 
    }
}

