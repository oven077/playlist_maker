package com.example.playlistmaker.settings.domain.repository

interface SettingsRepository {
    fun getDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}

