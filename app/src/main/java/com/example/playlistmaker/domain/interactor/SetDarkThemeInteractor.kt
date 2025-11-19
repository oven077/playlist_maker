package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.SettingsRepository

class SetDarkThemeInteractor(
    private val settingsRepository: SettingsRepository
) {
    fun execute(enabled: Boolean) {
        settingsRepository.setDarkThemeEnabled(enabled)
    }
}

