package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.ThemeSettings

// 1. Операции управления настройками
interface SettingsInteractor {
    // 1.1. Получение сохранённых настроек
    fun read(): ThemeSettings

    // 1.2. Изменение темы приложения
    fun setDarkTheme(enabled: Boolean)
}