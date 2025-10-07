package com.example.playlistmaker.manager

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "search_history"
        private const val KEY_HISTORY = "search_history_tracks"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * Добавляет трек в историю поиска
     * Если трек уже существует, перемещает его в начало списка
     * Ограничивает размер истории до MAX_HISTORY_SIZE элементов
     */
    fun addTrackToHistory(track: Track) {
        val currentHistory = getSearchHistory().toMutableList()

        // Удаляем трек, если он уже существует в истории
        currentHistory.removeAll { it.trackId == track.trackId }

        // Добавляем новый трек в начало списка
        currentHistory.add(0, track)

        // Ограничиваем размер истории
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        // Сохраняем обновленную историю
        saveSearchHistory(currentHistory)
    }

    /**
     * Получает список треков из истории поиска
     */
    fun getSearchHistory(): List<Track> {
        val historyJson = sharedPreferences.getString(KEY_HISTORY, null)
        return if (historyJson != null) {
            try {
                val type = object : TypeToken<List<Track>>() {}.type
                gson.fromJson(historyJson, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    /**
     * Очищает всю историю поиска
     */
    fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(KEY_HISTORY)
            .apply()
    }

    /**
     * Проверяет, есть ли треки в истории
     */
    fun hasSearchHistory(): Boolean {
        return getSearchHistory().isNotEmpty()
    }

    /**
     * Сохраняет список треков в SharedPreferences
     */
    private fun saveSearchHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(KEY_HISTORY, json)
            .apply()
    }
}