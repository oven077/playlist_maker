package com.example.playlistmaker.settings.data.datasource

import android.content.Context
import android.content.SharedPreferences

class SettingsDataSource(context: Context) {
    companion object {
        private const val PREF_NAME_SETTINGS = "app_preferences"
        private const val KEY_DARK_THEME = "dark_theme"
    }

    private val settingsPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME_SETTINGS, Context.MODE_PRIVATE)

    fun getDarkThemeEnabled(): Boolean {
        return settingsPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        settingsPreferences.edit()
            .putBoolean(KEY_DARK_THEME, enabled)
            .apply()
    }
}

