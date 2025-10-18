package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
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

    // История поиска
    private lateinit var searchHistoryManager: SearchHistoryManager
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: TextView
    private lateinit var historyAdapter: SearchRecyclerAdapter
    private val historyTracks = ArrayList<Track>()

    private val tracks = ArrayList<Track>()
    var textSearch = ""

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
                placeholderNothingWasFound.isVisible = false
                placeholderCommunicationsProblem.isVisible = false
                tracks.clear()
                searchAdapter.notifyDataSetChanged()
                recyclerView.visibility = View.GONE
                updateSearchHistoryVisibility()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTrack()
                true
            } else false
        }
    }

    private fun inputText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchClearIcon.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                textSearch = searchEditText.text.toString()
                updateSearchHistoryVisibility()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) updateSearchHistoryVisibility()
            else searchHistoryContainer.visibility = View.GONE
        }
    }

    private fun initRecycler(tracks: ArrayList<Track>) {
        recyclerView = findViewById(R.id.recycler_view)
        searchAdapter = SearchRecyclerAdapter(tracks) { track ->
            addTrackToHistory(track)
            val intent = Intent(this, AudioplayerActivity::class.java)
            intent.putExtra("TRACK", Gson().toJson(track))
            startActivity(intent)
        }
        recyclerView.adapter = searchAdapter
    }




    private fun retry() {
        buttonRetry.setOnClickListener {
            getTrack()
        }
    }

    private fun getTrack() {
        searchHistoryContainer.visibility = View.GONE
        val query = searchEditText.text.toString()
        serviceSearch.searchTrack(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (query.isNotEmpty() && response.code() == ApiConstants.SUCCESS_CODE &&
                    !response.body()?.results.isNullOrEmpty()
                ) {
                    tracks.clear()
                    tracks.addAll(response.body()!!.results)
                    searchAdapter.notifyDataSetChanged()
                    showPlaceholder(PlaceHolder.SEARCH_RESULT)
                } else {
                    showPlaceholder(PlaceHolder.NOT_FOUND)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showPlaceholder(PlaceHolder.ERROR)
            }
        })
    }

    private fun showPlaceholder(placeholder: PlaceHolder) {
        searchHistoryContainer.visibility = View.GONE
        when (placeholder) {
            PlaceHolder.NOT_FOUND -> {
                recyclerView.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.VISIBLE
            }
            PlaceHolder.ERROR -> {
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.VISIBLE
            }
            else -> {
                recyclerView.visibility = View.VISIBLE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
            }
        }
    }

    private fun initSearchHistory() {
        searchHistoryManager = SearchHistoryManager(this)
        searchHistoryContainer = findViewById(R.id.history_list)
        historyRecyclerView = findViewById(R.id.recyclerViewHistory)
        clearHistoryButton = findViewById(R.id.button_clear_history)

        historyAdapter = SearchRecyclerAdapter(historyTracks) { track ->
            // Открываем плеер с выбранным треком
            val intent = Intent(this, AudioplayerActivity::class.java)
            intent.putExtra("TRACK", Gson().toJson(track))
            startActivity(intent)
        }
        historyRecyclerView.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            searchHistoryManager.clearSearchHistory()
            historyTracks.clear()
            historyAdapter.notifyDataSetChanged()
            searchHistoryContainer.visibility = View.GONE
        }

        updateSearchHistoryDisplay()
    }

    private fun updateSearchHistoryDisplay() {
        val history = searchHistoryManager.getSearchHistory()
        historyTracks.clear()
        historyTracks.addAll(history)
        historyAdapter.notifyDataSetChanged()
        // Показываем историю только если поле поиска пустое И история не пустая
        val shouldShowHistory = searchEditText.text.isEmpty() && history.isNotEmpty()
        searchHistoryContainer.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
    }

    private fun addTrackToHistory(track: Track) {
        searchHistoryManager.addTrackToHistory(track)
        updateSearchHistoryDisplay()
    }

    private fun updateSearchHistoryVisibility() {
        val isSearchEmpty = searchEditText.text.isEmpty()
        val hasHistory = searchHistoryManager.hasSearchHistory()

        if (isSearchEmpty && hasHistory) {
            searchHistoryContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholderNothingWasFound.visibility = View.GONE
            placeholderCommunicationsProblem.visibility = View.GONE
        } else {
            searchHistoryContainer.visibility = View.GONE
        }
    }
}
