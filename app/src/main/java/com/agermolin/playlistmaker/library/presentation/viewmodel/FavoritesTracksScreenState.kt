package com.agermolin.playlistmaker.library.presentation.viewmodel

import com.agermolin.playlistmaker.core.entity.Track

sealed class FavoritesTracksScreenState {
    object Empty : FavoritesTracksScreenState()
    data class Content(val tracks: List<Track>) : FavoritesTracksScreenState()
}
