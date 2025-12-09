package com.example.playlistmaker.settings.domain.interactor

import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class GetDarkThemeInteractor(
    private val settingsRepository: SettingsRepository
) {
    fun execute(): Boolean {
        return settingsRepository.getDarkThemeEnabled()
    }
}

