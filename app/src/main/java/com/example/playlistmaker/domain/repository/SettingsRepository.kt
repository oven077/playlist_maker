package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun getDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}

