package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.data.datasource.SettingsDataSource
import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val settingsDataSource: SettingsDataSource
) : SettingsRepository {
    override fun getDarkThemeEnabled(): Boolean {
        return settingsDataSource.getDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        settingsDataSource.setDarkThemeEnabled(enabled)
    }
}

