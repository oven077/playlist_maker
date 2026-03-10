package com.agermolin.playlistmaker.search.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.search.domain.interactor.IAddTrackToHistoryInteractor
import com.agermolin.playlistmaker.search.domain.interactor.IClearSearchHistoryInteractor
import com.agermolin.playlistmaker.search.domain.interactor.IGetSearchHistoryInteractor
import com.agermolin.playlistmaker.search.domain.interactor.ISearchTracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val searchTracksInteractor: ISearchTracksInteractor,
    private val getSearchHistoryInteractor: IGetSearchHistoryInteractor,
    private val addTrackToHistoryInteractor: IAddTrackToHistoryInteractor,
    private val clearSearchHistoryInteractor: IClearSearchHistoryInteractor
) : AndroidViewModel(application) {

    private val _screenState = MutableLiveData<SearchScreenState>()
    val screenState: LiveData<SearchScreenState> = _screenState

    private val _isClickAllowed = MutableLiveData<Boolean>(true)
    val isClickAllowed: LiveData<Boolean> = _isClickAllowed

    private var searchJob: Job? = null
    private var clickDebounceJob: Job? = null
    private var lastQuery: String? = null

    init {
        showHistory()
    }

    fun searchDebounce(changedText: String?) {
        searchJob?.cancel()
        if (!changedText.isNullOrEmpty()) {
            if (lastQuery == changedText) return
            lastQuery = changedText
            searchJob = viewModelScope.launch {
                delay(Constants.SEARCH_DEBOUNCE_DELAY)
                getTracks(changedText)
            }
        } else {
            lastQuery = null
            showHistory()
        }
    }

    fun getTracks(query: String?) {
        searchJob?.cancel()
        if (query.isNullOrBlank()) {
            showHistory()
            return
        }

        searchJob = viewModelScope.launch {
            _screenState.value = SearchScreenState.Loading
            searchTracksInteractor.execute(query).collect { result ->
                result.fold(
                    onSuccess = { trackList ->
                        _screenState.value = if (trackList.isNotEmpty()) {
                            SearchScreenState.Success(trackList)
                        } else {
                            SearchScreenState.NothingFound
                        }
                    },
                    onFailure = {
                        _screenState.value = SearchScreenState.Error(
                            "Загрузка не удалась. Проверьте подключение к интернету"
                        )
                    }
                )
            }
        }
    }

    fun onTrackClicked(track: Track) {
        if (_isClickAllowed.value == false) return

        trackOnClickDebounce()
        addTrackToHistory(track)
    }

    private fun trackOnClickDebounce() {
        _isClickAllowed.value = false
        clickDebounceJob?.cancel()
        clickDebounceJob = viewModelScope.launch {
            delay(Constants.CLICK_DEBOUNCE_DELAY)
            _isClickAllowed.value = true
        }
    }

    private fun addTrackToHistory(track: Track) {
        addTrackToHistoryInteractor.execute(track)
    }

    fun clearSearch() {
        searchJob?.cancel()
        searchJob = null
        val history = getSearchHistoryInteractor.execute()
        if (history.isNotEmpty()) {
            _screenState.postValue(SearchScreenState.ShowHistory(history))
        } else {
            _screenState.postValue(SearchScreenState.Success(emptyList()))
        }
    }

    fun clearHistory() {
        clearSearchHistoryInteractor.execute()
        showHistory()
    }

    private fun showHistory() {
        val history = getSearchHistoryInteractor.execute()
        if (history.isNotEmpty()) {
            _screenState.postValue(SearchScreenState.ShowHistory(history))
        } else {
            _screenState.postValue(SearchScreenState.Success(emptyList()))
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        clickDebounceJob?.cancel()
    }
}

