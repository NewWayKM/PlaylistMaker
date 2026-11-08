package com.practicum.playlistmaker.data.mapper

import com.practicum.playlistmaker.data.dto.ThemeSettingsDto
import com.practicum.playlistmaker.domain.models.ThemeSettings

// 1. Преобразование настроек темы
object SettingsMapper {

    // 1.1. DTO -> Domain
    fun toDomain(dto: ThemeSettingsDto): ThemeSettings {
        return ThemeSettings(
            darkTheme = dto.darkTheme
        )
    }

    // 1.2. Domain -> DTO
    fun toDto(settings: ThemeSettings): ThemeSettingsDto {
        return ThemeSettingsDto(
            darkTheme = settings.darkTheme
        )
    }
}