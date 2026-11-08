package com.practicum.playlistmaker.domain.models

// 1. Настройки темы приложения
// null означает, что пользователь ещё не выбрал тему
data class ThemeSettings(
    val darkTheme: Boolean?
)