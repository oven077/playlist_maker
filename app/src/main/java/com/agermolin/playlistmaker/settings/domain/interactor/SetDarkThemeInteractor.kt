package com.agermolin.playlistmaker.settings.domain.interactor

import com.agermolin.playlistmaker.settings.domain.repository.SettingsRepository

interface ISetDarkThemeInteractor {
    fun execute(enabled: Boolean)
}

class SetDarkThemeInteractor(
    private val settingsRepository: SettingsRepository
) : ISetDarkThemeInteractor {
    override fun execute(enabled: Boolean) {
        settingsRepository.setDarkThemeEnabled(enabled)
    }
}
