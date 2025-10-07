package com.example.playlistmaker

import android.content.Context
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
import com.sakal.playlistmaker.model.ApiConstants
import com.sakal.playlistmaker.model.iTunesSearchAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

import com.example.playlistmaker.adapters.SearchHistoryAdapter
import com.example.playlistmaker.manager.SearchHistoryManager

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"

    }

    private var searchInputQuery = ""
    private lateinit var searchInput: EditText
    private lateinit var searchInputClearButton: ImageView
    private val searchInputTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchInputClearButton.visibility = clearButtonVisibility(s)
            searchInputQuery = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    private lateinit var recyclerView: RecyclerView


    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val serviceSearch = retrofit.create(iTunesSearchAPI::class.java)

    lateinit var searchEditText: EditText
    var textSearch = ""
    private val tracks = ArrayList<Track>()

    lateinit var searchAdapter: SearchRecyclerAdapter

    private lateinit var placeholderNothingWasFound: TextView
    private lateinit var placeholderCommunicationsProblem: LinearLayout

    lateinit var searchClearIcon: ImageView

    private lateinit var buttonRetry: Button

    // История поиска
    private lateinit var searchHistoryManager: SearchHistoryManager
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: TextView
    private lateinit var historyAdapter: SearchHistoryAdapter
    private val historyTracks = ArrayList<Track>()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initSearchHistory()
        initRecycler(tracks)
        retry()
        initToolbar()
        initSearch()
        inputText()


    }

    private fun retry() {
        buttonRetry = findViewById(R.id.button_retry)
        buttonRetry.setOnClickListener {
            getTrack()
        }
    }



    private fun initToolbar() {
        findViewById<Toolbar>(R.id.search_toolbar).setNavigationOnClickListener() {
            finish()
        }
    }

    private fun initSearch() {

        searchClearIcon = findViewById(R.id.clear_form)
        searchEditText = findViewById(R.id.input_search_form)
        searchEditText.setText(textSearch)
        searchEditText.requestFocus()

        searchClearIcon.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            searchEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)

            placeholderNothingWasFound.isVisible = false
            placeholderNothingWasFound.isVisible = false
            tracks.clear()
            searchAdapter.notifyDataSetChanged()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTrack()
                true
            }
            false
        }

    }

    private fun inputText() {
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchClearIcon.visibility = searchClearIconVisibility(s)
                textSearch = searchEditText.text.toString()

                // Показываем/скрываем историю в зависимости от содержимого поля
                updateSearchHistoryVisibility()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)

        // Добавляем обработчик фокуса
        searchEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateSearchHistoryVisibility()
            } else {
                // Скрываем историю при потере фокуса
                searchHistoryContainer.visibility = View.GONE
            }
        }
    }


    private fun initRecycler(tracks: ArrayList<Track>) {
        recyclerView = findViewById(R.id.recycler_view)
        searchAdapter = SearchRecyclerAdapter(tracks) { track ->
            // Обработка клика по треку в результатах поиска
            addTrackToHistory(track)
            // Скрываем результаты поиска после добавления в историю
            recyclerView.visibility = View.GONE
        }
        recyclerView.adapter = searchAdapter
    }

    private fun searchClearIconVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchInputQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInputQuery = savedInstanceState.getString(SEARCH_QUERY, "")
        searchInput.setText(searchInputQuery)
    }

    private fun getTrack() {

        // Скрываем историю при поиске
        searchHistoryContainer.visibility = View.GONE

        serviceSearch.searchTrack(searchEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>,
                ) {
                    if (textSearch.isNotEmpty() && !response.body()?.results.isNullOrEmpty() && response.code() == ApiConstants.SUCCESS_CODE) {
                        tracks.clear()
                        tracks.addAll(response.body()?.results!!)
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
        placeholderNothingWasFound = findViewById(R.id.placeholderNothingWasFound)
        placeholderCommunicationsProblem = findViewById(R.id.placeholderCommunicationsProblem)

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
        searchHistoryContainer = findViewById(R.id.search_history_container)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        clearHistoryButton = findViewById(R.id.clear_history_button)

        // Инициализируем адаптер истории
        historyAdapter = SearchHistoryAdapter(historyTracks) { track ->
            // При клике на трек из истории - только заполняем поле поиска
            searchEditText.setText(track.trackName)
            searchEditText.setSelection(searchEditText.text.length)
            // НЕ добавляем в историю повторно
        }
        historyRecyclerView.adapter = historyAdapter

        // Обработчик кнопки очистки истории
        clearHistoryButton.setOnClickListener {
            searchHistoryManager.clearSearchHistory()
            updateSearchHistoryDisplay()
        }

        // Загружаем историю при инициализации
        updateSearchHistoryDisplay()
    }

    private fun updateSearchHistoryDisplay() {
        val history = searchHistoryManager.getSearchHistory()
        historyTracks.clear()
        historyTracks.addAll(history)
        historyAdapter.notifyDataSetChanged()

        // Показываем/скрываем контейнер истории в зависимости от наличия данных
        if (history.isNotEmpty()) {
            searchHistoryContainer.visibility = View.VISIBLE
        } else {
            searchHistoryContainer.visibility = View.GONE
        }
    }

    private fun addTrackToHistory(track: Track) {
        searchHistoryManager.addTrackToHistory(track)
        updateSearchHistoryDisplay()
    }

    private fun updateSearchHistoryVisibility() {
        val isSearchEmpty = searchEditText.text.toString().isEmpty()
        val hasHistory = searchHistoryManager.hasSearchHistory()

        if (isSearchEmpty && hasHistory) {
            // Показываем ТОЛЬКО историю
            searchHistoryContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholderNothingWasFound.visibility = View.GONE
            placeholderCommunicationsProblem.visibility = View.GONE
        } else {
            // Скрываем историю
            searchHistoryContainer.visibility = View.GONE
        }
    }


}