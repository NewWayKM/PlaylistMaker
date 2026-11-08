package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.models.ThemeSettings
import com.practicum.playlistmaker.domain.repository.SettingsRepository

// 1. Бизнес-логика пользовательских настроек
class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    // 2. Получение текущих настроек
    override fun read(): ThemeSettings {
        return repository.read()
    }

    // 3. Сохранение выбранной темы
    override fun setDarkTheme(enabled: Boolean) {
        repository.save(
            ThemeSettings(darkTheme = enabled)
        )
    }
}