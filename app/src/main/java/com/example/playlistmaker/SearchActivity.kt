package com.example.playlistmaker

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
import com.example.playlistmaker.adapters.SearchRecyclerAdapter
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.TrackResponse
import com.example.playlistmaker.model.ApiConstants
import com.example.playlistmaker.model.iTunesSearchAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.playlistmaker.manager.SearchHistoryManager
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
    private lateinit var searchHistoryManager: SearchHistoryManager
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

    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val serviceSearch = retrofit.create(iTunesSearchAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация всех view
        searchEditText = findViewById(R.id.input_search_form)
        searchClearIcon = findViewById(R.id.clear_form)
        placeholderNothingWasFound = findViewById(R.id.placeholderNothingWasFound)
        placeholderCommunicationsProblem = findViewById(R.id.placeholderCommunicationsProblem)
        recyclerView = findViewById(R.id.recycler_view)
        buttonRetry = findViewById(R.id.button_retry)
        progressBar = findViewById(R.id.searchProgressBar)

        searchClearIcon.visibility = View.INVISIBLE

        initSearchHistory()
        initRecycler(tracks)
        retry()
        initToolbar()
        initSearch()
        inputText()
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
                val intent = Intent(this, AudioplayerActivity::class.java)
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
        serviceSearch.searchTrack(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (query != searchEditText.text.toString().trim()) {
                    return
                }

                if (response.code() == ApiConstants.SUCCESS_CODE) {
                    val items = response.body()?.results.orEmpty()
                    if (items.isNotEmpty()) {
                        tracks.clear()
                        tracks.addAll(items)
                        searchAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceHolder.SEARCH_RESULT)
                    } else {
                        tracks.clear()
                        searchAdapter.notifyDataSetChanged()
                        showPlaceholder(PlaceHolder.NOT_FOUND)
                    }
                } else {
                    showPlaceholder(PlaceHolder.ERROR)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showPlaceholder(PlaceHolder.ERROR)
            }
        })
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
        searchHistoryManager = SearchHistoryManager(this)
        searchHistoryContainer = findViewById(R.id.history_list)
        historyRecyclerView = findViewById(R.id.recyclerViewHistory)
        clearHistoryButton = findViewById(R.id.button_clear_history)

        historyAdapter = SearchRecyclerAdapter(historyTracks) { track ->
            if (clickDebounce()) {
                addTrackToHistory(track)
                val intent = Intent(this, AudioplayerActivity::class.java)
                intent.putExtra(Constants.TRACK, Gson().toJson(track))
                startActivity(intent)
            }
        }
        historyRecyclerView.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            searchHistoryManager.clearSearchHistory()
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
        val history = searchHistoryManager.getSearchHistory()
        historyTracks.clear()
        historyTracks.addAll(history)
        historyAdapter.notifyDataSetChanged()
    }

    private fun addTrackToHistory(track: Track) {
        searchHistoryManager.addTrackToHistory(track)
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
