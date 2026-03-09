package com.agermolin.playlistmaker.search.presentation.viewmodel

import com.agermolin.playlistmaker.core.entity.Track

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    data class Success(val tracks: List<Track>) : SearchScreenState()
    data class ShowHistory(val tracks: List<Track>) : SearchScreenState()
    data class Error(val message: String) : SearchScreenState()
    object NothingFound : SearchScreenState()
}

