package com.agermolin.playlistmaker.settings.domain.interactor

import com.agermolin.playlistmaker.settings.domain.repository.SettingsRepository

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
