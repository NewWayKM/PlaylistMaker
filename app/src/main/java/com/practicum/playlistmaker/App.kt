package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

// 1. Запуск приложения и инициализация зависимостей
class App : Application() {

    // 2. Создание приложения
    override fun onCreate() {
        super.onCreate()

        // 2.1. Инициализация Creator
        Creator.init(this)

        // 2.2. Получение сохранённой темы
        val darkTheme = Creator.provideSettingsInteractor()
            .read()
            .darkTheme

        // 2.3. Применение темы
        AppCompatDelegate.setDefaultNightMode(
            when (darkTheme) {
                true -> AppCompatDelegate.MODE_NIGHT_YES
                false -> AppCompatDelegate.MODE_NIGHT_NO
                null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}