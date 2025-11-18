package com.example.playlistmaker.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.di.Creator
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.interactor.AddTrackToHistoryInteractor
import com.example.playlistmaker.domain.interactor.ClearSearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.GetSearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.presentation.adapter.SearchRecyclerAdapter
import com.example.playlistmaker.Constants
import com.example.playlistmaker.PlaceHolder
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
    }

    private lateinit var searchEditText: EditText
    private lateinit var searchClearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchRecyclerAdapter
    private lateinit var placeholderNothingWasFound: TextView
    private lateinit var placeholderCommunicationsProblem: LinearLayout
    private lateinit var buttonRetry: Button
    private lateinit var progressBar: ProgressBar

    // История поиска
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: TextView
    private lateinit var historyAdapter: SearchRecyclerAdapter
    private val historyTracks = ArrayList<Track>()

    private val tracks = ArrayList<Track>()
    private var textSearch = ""
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { getTrack() }
    private var isClickAllowed = true

    // Interactors
    private lateinit var searchTracksInteractor: SearchTracksInteractor
    private lateinit var getSearchHistoryInteractor: GetSearchHistoryInteractor
    private lateinit var addTrackToHistoryInteractor: AddTrackToHistoryInteractor
    private lateinit var clearSearchHistoryInteractor: ClearSearchHistoryInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initDependencies()
        initViews()
        initSearchHistory()
        initRecycler(tracks)
        retry()
        initToolbar()
        initSearch()
        inputText()
    }

    private fun initDependencies() {
        searchTracksInteractor = Creator.provideSearchTracksInteractor(this)
        getSearchHistoryInteractor = Creator.provideGetSearchHistoryInteractor(this)
        addTrackToHistoryInteractor = Creator.provideAddTrackToHistoryInteractor(this)
        clearSearchHistoryInteractor = Creator.provideClearSearchHistoryInteractor(this)
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.input_search_form)
        searchClearIcon = findViewById(R.id.clear_form)
        placeholderNothingWasFound = findViewById(R.id.placeholderNothingWasFound)
        placeholderCommunicationsProblem = findViewById(R.id.placeholderCommunicationsProblem)
        recyclerView = findViewById(R.id.recycler_view)
        buttonRetry = findViewById(R.id.button_retry)
        progressBar = findViewById(R.id.searchProgressBar)

        searchClearIcon.visibility = View.INVISIBLE
    }

    private fun initToolbar() {
        findViewById<Toolbar>(R.id.search_toolbar).setNavigationOnClickListener {
            finish()
        }
    }

    private fun initSearch() {
        searchEditText.setText(textSearch)
        searchEditText.requestFocus()

        searchClearIcon.setOnClickListener {
            if (searchEditText.text.isNotEmpty()) {
                searchEditText.text.clear()
                searchClearIcon.visibility = View.INVISIBLE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
                tracks.clear()
                searchAdapter.notifyDataSetChanged()
                recyclerView.visibility = View.GONE
                handler.removeCallbacks(searchRunnable)
                updateSearchHistoryDisplay()
                showPlaceholder(PlaceHolder.HISTORY)

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                getTrack()
                true
            } else false
        }
    }

    private fun inputText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearIcon.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                textSearch = searchEditText.text.toString()
                handler.removeCallbacks(searchRunnable)

                if (textSearch.isBlank()) {
                    tracks.clear()
                    searchAdapter.notifyDataSetChanged()
                    updateSearchHistoryDisplay()
                    showPlaceholder(PlaceHolder.HISTORY)
                } else {
                    showPlaceholder(PlaceHolder.LOADING)
                    handler.postDelayed(searchRunnable, Constants.SEARCH_DEBOUNCE_DELAY)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                updateSearchHistoryDisplay()
                showPlaceholder(PlaceHolder.HISTORY)
            } else if (!hasFocus && searchHistoryContainer.visibility == View.VISIBLE) {
                searchHistoryContainer.visibility = View.GONE
            }
        }
    }

    private fun initRecycler(tracks: ArrayList<Track>) {
        recyclerView = findViewById(R.id.recycler_view)
        searchAdapter = SearchRecyclerAdapter(tracks) { track ->
            if (clickDebounce()) {
                addTrackToHistory(track)
                val intent = Intent(this, com.example.playlistmaker.presentation.activity.AudioplayerActivity::class.java)
                intent.putExtra(Constants.TRACK, Gson().toJson(track))
                startActivity(intent)
            }
        }
        recyclerView.adapter = searchAdapter
    }

    private fun retry() {
        buttonRetry.setOnClickListener {
            handler.removeCallbacks(searchRunnable)
            getTrack()
        }
    }

    private fun getTrack() {
        val query = searchEditText.text.toString().trim()
        if (query.isEmpty()) {
            updateSearchHistoryDisplay()
            showPlaceholder(PlaceHolder.HISTORY)
            return
        }

        showPlaceholder(PlaceHolder.LOADING)
        searchTracksInteractor.execute(query) { result ->
            if (query != searchEditText.text.toString().trim()) {
                return@execute
            }

            result.fold(
                onSuccess = { trackList ->
                    if (trackList.isNotEmpty()) {
                        tracks.clear()
                        tracks.addAll(trackList)
                        searchAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceHolder.SEARCH_RESULT)
                    } else {
                        tracks.clear()
                        searchAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceHolder.NOT_FOUND)
                    }
                },
                onFailure = {
                    showPlaceholder(PlaceHolder.ERROR)
                }
            )
        }
    }

    private fun showPlaceholder(placeholder: PlaceHolder) {
        when (placeholder) {
            PlaceHolder.LOADING -> {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
                searchHistoryContainer.visibility = View.GONE
            }
            PlaceHolder.SEARCH_RESULT -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
                searchHistoryContainer.visibility = View.GONE
            }
            PlaceHolder.NOT_FOUND -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.VISIBLE
                placeholderCommunicationsProblem.visibility = View.GONE
                searchHistoryContainer.visibility = View.GONE
            }
            PlaceHolder.ERROR -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.VISIBLE
                searchHistoryContainer.visibility = View.GONE
            }
            PlaceHolder.HISTORY -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
                searchHistoryContainer.visibility =
                    if (historyTracks.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun initSearchHistory() {
        searchHistoryContainer = findViewById(R.id.history_list)
        historyRecyclerView = findViewById(R.id.recyclerViewHistory)
        clearHistoryButton = findViewById(R.id.button_clear_history)

        historyAdapter = SearchRecyclerAdapter(historyTracks) { track ->
            if (clickDebounce()) {
                addTrackToHistory(track)
                val intent = Intent(this, com.example.playlistmaker.presentation.activity.AudioplayerActivity::class.java)
                intent.putExtra(Constants.TRACK, Gson().toJson(track))
                startActivity(intent)
            }
        }
        historyRecyclerView.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            clearSearchHistoryInteractor.execute()
            historyTracks.clear()
            historyAdapter.notifyDataSetChanged()
            searchHistoryContainer.visibility = View.GONE
        }

        updateSearchHistoryDisplay()
        if (searchEditText.text.isEmpty() && historyTracks.isNotEmpty()) {
            showPlaceholder(PlaceHolder.HISTORY)
        }
    }

    private fun updateSearchHistoryDisplay() {
        val history = getSearchHistoryInteractor.execute()
        historyTracks.clear()
        historyTracks.addAll(history)
        historyAdapter.notifyDataSetChanged()
    }

    private fun addTrackToHistory(track: Track) {
        addTrackToHistoryInteractor.execute(track)
        updateSearchHistoryDisplay()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, Constants.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
        super.onDestroy()
    }
}
