package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

// 1. Запуск приложения и инициализация зависимостей
class App : Application(), KoinComponent {

    // 2. Создание приложения
    override fun onCreate() {
        super.onCreate()

        // 2.1. Инициализация Koin
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule
            )
        }

        // 2.2. Получение сохранённой темы
        val darkTheme = get<SettingsInteractor>()
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