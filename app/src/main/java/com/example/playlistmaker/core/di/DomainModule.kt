package com.example.playlistmaker.core.di

import com.example.playlistmaker.player.domain.interactor.GetCurrentPositionInteractor
import com.example.playlistmaker.player.domain.interactor.IGetCurrentPositionInteractor
import com.example.playlistmaker.player.domain.interactor.GetPlayerStateInteractor
import com.example.playlistmaker.player.domain.interactor.IGetPlayerStateInteractor
import com.example.playlistmaker.player.domain.interactor.PauseTrackInteractor
import com.example.playlistmaker.player.domain.interactor.IPauseTrackInteractor
import com.example.playlistmaker.player.domain.interactor.PlayTrackInteractor
import com.example.playlistmaker.player.domain.interactor.IPlayTrackInteractor
import com.example.playlistmaker.player.domain.interactor.PreparePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.IPreparePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.ReleasePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.IReleasePlayerInteractor
import com.example.playlistmaker.player.domain.interactor.SetOnCompletionListenerInteractor
import com.example.playlistmaker.player.domain.interactor.ISetOnCompletionListenerInteractor
import com.example.playlistmaker.search.domain.interactor.AddTrackToHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.IAddTrackToHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.ClearSearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.IClearSearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.GetSearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.IGetSearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.search.domain.interactor.ISearchTracksInteractor
import com.example.playlistmaker.settings.domain.interactor.GetDarkThemeInteractor
import com.example.playlistmaker.settings.domain.interactor.IGetDarkThemeInteractor
import com.example.playlistmaker.settings.domain.interactor.SetDarkThemeInteractor
import com.example.playlistmaker.settings.domain.interactor.ISetDarkThemeInteractor
import org.koin.dsl.module

val domainModule = module {
    
    // Search Interactors
    factory<ISearchTracksInteractor> {
        SearchTracksInteractor(get())
    }
    
    factory<IGetSearchHistoryInteractor> {
        GetSearchHistoryInteractor(get())
    }
    
    factory<IAddTrackToHistoryInteractor> {
        AddTrackToHistoryInteractor(get())
    }
    
    factory<IClearSearchHistoryInteractor> {
        ClearSearchHistoryInteractor(get())
    }
    
    // Settings Interactors
    factory<IGetDarkThemeInteractor> {
        GetDarkThemeInteractor(get())
    }
    
    factory<ISetDarkThemeInteractor> {
        SetDarkThemeInteractor(get())
    }
    
    // Player Interactors
    factory<IPreparePlayerInteractor> {
        PreparePlayerInteractor(get())
    }
    
    factory<IPlayTrackInteractor> {
        PlayTrackInteractor(get())
    }
    
    factory<IPauseTrackInteractor> {
        PauseTrackInteractor(get())
    }
    
    factory<IGetPlayerStateInteractor> {
        GetPlayerStateInteractor(get())
    }
    
    factory<IGetCurrentPositionInteractor> {
        GetCurrentPositionInteractor(get())
    }
    
    factory<ISetOnCompletionListenerInteractor> {
        SetOnCompletionListenerInteractor(get())
    }
    
    factory<IReleasePlayerInteractor> {
        ReleasePlayerInteractor(get())
    }
}

