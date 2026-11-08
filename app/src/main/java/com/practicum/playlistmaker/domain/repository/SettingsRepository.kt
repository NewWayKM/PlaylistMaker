package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.ThemeSettings

// 1. Контракт хранения пользовательских настроек
interface SettingsRepository {
    // 1.1. Получение текущих настроек темы
    fun read(): ThemeSettings

    // 1.2. Сохранение настроек темы
    fun save(settings: ThemeSettings)
}