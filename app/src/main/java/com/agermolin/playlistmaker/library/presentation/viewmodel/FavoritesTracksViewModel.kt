package com.agermolin.playlistmaker.library.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agermolin.playlistmaker.library.domain.interactor.IFavoritesInteractor
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FavoritesTracksViewModel(
    private val favoritesInteractor: IFavoritesInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<FavoritesTracksScreenState>()
    val screenState: LiveData<FavoritesTracksScreenState> = _screenState

    init {
        favoritesInteractor.getFavoriteTracks()
            .onEach { tracks ->
                _screenState.postValue(
                    if (tracks.isEmpty()) {
                        FavoritesTracksScreenState.Empty
                    } else {
                        FavoritesTracksScreenState.Content(tracks)
                    }
                )
            }
            .catch { _ ->
                _screenState.postValue(FavoritesTracksScreenState.Empty)
            }
            .launchIn(viewModelScope)
    }
}

