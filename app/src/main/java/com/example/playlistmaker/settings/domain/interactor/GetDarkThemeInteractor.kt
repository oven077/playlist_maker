package com.example.playlistmaker.settings.domain.interactor

import com.example.playlistmaker.settings.domain.repository.SettingsRepository

interface IGetDarkThemeInteractor {
    fun execute(): Boolean
}

class GetDarkThemeInteractor(
    private val settingsRepository: SettingsRepository
) : IGetDarkThemeInteractor {
    override fun execute(): Boolean {
        return settingsRepository.getDarkThemeEnabled()
    }
}
