package com.example.playlistmaker.search.presentation.activity

import android.content.Context
import android.content.Intent
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.player.presentation.activity.AudioplayerActivity
import com.example.playlistmaker.search.presentation.adapter.SearchRecyclerAdapter
import com.example.playlistmaker.search.presentation.viewmodel.SearchScreenState
import com.example.playlistmaker.search.presentation.viewmodel.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var searchEditText: EditText
    private lateinit var searchClearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchRecyclerAdapter
    private lateinit var placeholderNothingWasFound: TextView
    private lateinit var placeholderCommunicationsProblem: LinearLayout
    private lateinit var buttonRetry: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: TextView
    private lateinit var historyAdapter: SearchRecyclerAdapter
    private val historyTracks = ArrayList<Track>()

    private val tracks = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initSearchHistory()
        initRecycler(tracks)
        retry()
        initToolbar()
        initSearch()
        inputText()

        viewModel.screenState.observe(this) { state ->
            render(state)
        }
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
        searchEditText.requestFocus()

        searchClearIcon.setOnClickListener {
            if (searchEditText.text.isNotEmpty()) {
                searchEditText.text.clear()
                searchClearIcon.visibility = View.INVISIBLE
                viewModel.clearSearch()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                viewModel.getTracks(query)
                true
            } else false
        }
    }

    private fun inputText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearIcon.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                
                viewModel.searchDebounce(s?.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.clearSearch()
            } else if (!hasFocus && searchHistoryContainer.visibility == View.VISIBLE) {
                searchHistoryContainer.visibility = View.GONE
            }
        }
    }

    private fun initRecycler(tracks: ArrayList<Track>) {
        recyclerView = findViewById(R.id.recycler_view)
        searchAdapter = SearchRecyclerAdapter(tracks) { track ->
            viewModel.onTrackClicked(track)
            
            val intent = Intent(this, AudioplayerActivity::class.java)
            intent.putExtra(Constants.TRACK, Gson().toJson(track))
            startActivity(intent)
        }
        recyclerView.adapter = searchAdapter
    }

    private fun retry() {
        buttonRetry.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            viewModel.getTracks(query)
        }
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
                searchHistoryContainer.visibility = View.GONE
            }
            is SearchScreenState.Success -> {
                progressBar.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
                searchHistoryContainer.visibility = View.GONE
                
                if (state.tracks.isNotEmpty()) {
                    tracks.clear()
                    tracks.addAll(state.tracks)
                    searchAdapter.notifyDataSetChanged()
                    recyclerView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.GONE
                }
            }
            is SearchScreenState.ShowHistory -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.GONE
                
                historyTracks.clear()
                historyTracks.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()
                searchHistoryContainer.visibility =
                    if (state.tracks.isNotEmpty()) View.VISIBLE else View.GONE
            }
            is SearchScreenState.Error -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.GONE
                placeholderCommunicationsProblem.visibility = View.VISIBLE
                searchHistoryContainer.visibility = View.GONE
            }
            is SearchScreenState.NothingFound -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                placeholderNothingWasFound.visibility = View.VISIBLE
                placeholderCommunicationsProblem.visibility = View.GONE
                searchHistoryContainer.visibility = View.GONE
            }
        }
    }

    private fun initSearchHistory() {
        searchHistoryContainer = findViewById(R.id.history_list)
        historyRecyclerView = findViewById(R.id.recyclerViewHistory)
        clearHistoryButton = findViewById(R.id.button_clear_history)

        historyAdapter = SearchRecyclerAdapter(historyTracks) { track ->
            viewModel.onTrackClicked(track)
            
            val intent = Intent(this, AudioplayerActivity::class.java)
            intent.putExtra(Constants.TRACK, Gson().toJson(track))
            startActivity(intent)
        }
        historyRecyclerView.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }
}

