package com.agermolin.playlistmaker.core.di

import com.agermolin.playlistmaker.player.domain.interactor.GetCurrentPositionInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IGetCurrentPositionInteractor
import com.agermolin.playlistmaker.player.domain.interactor.GetPlayerStateInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IGetPlayerStateInteractor
import com.agermolin.playlistmaker.player.domain.interactor.PauseTrackInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IPauseTrackInteractor
import com.agermolin.playlistmaker.player.domain.interactor.PlayTrackInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IPlayTrackInteractor
import com.agermolin.playlistmaker.player.domain.interactor.PreparePlayerInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IPreparePlayerInteractor
import com.agermolin.playlistmaker.player.domain.interactor.ReleasePlayerInteractor
import com.agermolin.playlistmaker.player.domain.interactor.IReleasePlayerInteractor
import com.agermolin.playlistmaker.player.domain.interactor.SetOnCompletionListenerInteractor
import com.agermolin.playlistmaker.player.domain.interactor.ISetOnCompletionListenerInteractor
import com.agermolin.playlistmaker.search.domain.interactor.AddTrackToHistoryInteractor
import com.agermolin.playlistmaker.search.domain.interactor.IAddTrackToHistoryInteractor
import com.agermolin.playlistmaker.search.domain.interactor.ClearSearchHistoryInteractor
import com.agermolin.playlistmaker.search.domain.interactor.IClearSearchHistoryInteractor
import com.agermolin.playlistmaker.search.domain.interactor.GetSearchHistoryInteractor
import com.agermolin.playlistmaker.search.domain.interactor.IGetSearchHistoryInteractor
import com.agermolin.playlistmaker.library.domain.interactor.FavoritesInteractor
import com.agermolin.playlistmaker.library.domain.interactor.IFavoritesInteractor
import com.agermolin.playlistmaker.search.domain.interactor.SearchTracksInteractor
import com.agermolin.playlistmaker.search.domain.interactor.ISearchTracksInteractor
import com.agermolin.playlistmaker.settings.domain.interactor.GetDarkThemeInteractor
import com.agermolin.playlistmaker.settings.domain.interactor.IGetDarkThemeInteractor
import com.agermolin.playlistmaker.settings.domain.interactor.SetDarkThemeInteractor
import com.agermolin.playlistmaker.settings.domain.interactor.ISetDarkThemeInteractor
import org.koin.dsl.module

val domainModule = module {

    // Favorites Interactor
    factory<IFavoritesInteractor> {
        FavoritesInteractor(get())
    }

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

