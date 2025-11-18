package com.example.playlistmaker.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalDataSource(context: Context) {
    companion object {
        private const val PREF_NAME = "search_history"
        private const val KEY_HISTORY = "search_history_tracks"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getSearchHistory(): List<TrackDto> {
        val historyJson = sharedPreferences.getString(KEY_HISTORY, null)
        return if (historyJson != null) {
            try {
                val type = object : TypeToken<List<TrackDto>>() {}.type
                gson.fromJson<List<TrackDto>>(historyJson, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun saveSearchHistory(tracks: List<TrackDto>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(KEY_HISTORY, json)
            .apply()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(KEY_HISTORY)
            .apply()
    }

    fun addTrackToHistory(track: TrackDto) {
        val currentHistory = getSearchHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)
        
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        
        saveSearchHistory(currentHistory)
    }
}
