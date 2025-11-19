package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.datasource.LocalDataSource
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val localDataSource: LocalDataSource
) : SettingsRepository {
    override fun getDarkThemeEnabled(): Boolean {
        return localDataSource.getDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        localDataSource.setDarkThemeEnabled(enabled)
    }
}

