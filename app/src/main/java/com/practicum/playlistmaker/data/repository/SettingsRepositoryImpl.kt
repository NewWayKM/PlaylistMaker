package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.local.SettingsLocalStorage
import com.practicum.playlistmaker.data.mapper.SettingsMapper
import com.practicum.playlistmaker.domain.models.ThemeSettings
import com.practicum.playlistmaker.domain.repository.SettingsRepository

// 1. Реализация репозитория настроек
class SettingsRepositoryImpl(
    private val storage: SettingsLocalStorage
) : SettingsRepository {

    // 1.1. Получение доменной модели настроек
    override fun read(): ThemeSettings {
        return SettingsMapper.toDomain(storage.read())
    }

    // 1.2. Сохранение настроек через DTO
    override fun save(settings: ThemeSettings) {
        storage.save(SettingsMapper.toDto(settings))
    }
}