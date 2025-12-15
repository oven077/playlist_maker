package com.example.playlistmaker.search.presentation.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.search.domain.interactor.IAddTrackToHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.IClearSearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.IGetSearchHistoryInteractor
import com.example.playlistmaker.search.domain.interactor.ISearchTracksInteractor

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

    private val handler = Handler(Looper.getMainLooper())
    private var lastQuery: String? = null
    private val SEARCH_REQUEST_TOKEN = Any()

    init {
        showHistory()
    }

    fun searchDebounce(changedText: String?) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (!changedText.isNullOrEmpty()) {
            if (lastQuery == changedText) {
                return
            }
            lastQuery = changedText
            makeDelaySearching(changedText)
        } else {
            showHistory()
        }
    }

    private fun makeDelaySearching(changedText: String) {
        val searchRunnable = Runnable {
            getTracks(changedText)
        }
        val postTime = SystemClock.uptimeMillis() + Constants.SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun getTracks(query: String?) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (query.isNullOrBlank()) {
            showHistory()
            return
        }

        _screenState.postValue(SearchScreenState.Loading)
        searchTracksInteractor.execute(query) { result ->
            result.fold(
                onSuccess = { trackList ->
                    if (trackList.isNotEmpty()) {
                        _screenState.postValue(SearchScreenState.Success(trackList))
                    } else {
                        _screenState.postValue(SearchScreenState.NothingFound)
                    }
                },
                onFailure = {
                    _screenState.postValue(
                        SearchScreenState.Error("Загрузка не удалась. Проверьте подключение к интернету")
                    )
                }
            )
        }
    }

    fun onTrackClicked(track: Track) {
        if (_isClickAllowed.value == false) return

        trackOnClickDebounce()
        addTrackToHistory(track)
    }

    private fun trackOnClickDebounce() {
        _isClickAllowed.value = false
        handler.postDelayed(
            { _isClickAllowed.value = true },
            Constants.CLICK_DEBOUNCE_DELAY
        )
    }

    private fun addTrackToHistory(track: Track) {
        addTrackToHistoryInteractor.execute(track)
    }

    fun clearSearch() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}

