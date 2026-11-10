package com.practicum.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.local.HistoryLocalStorage
import com.practicum.playlistmaker.data.local.PreferencesProvider
import com.practicum.playlistmaker.data.local.SettingsLocalStorage
import com.practicum.playlistmaker.data.local.SharingLocalStorage
import com.practicum.playlistmaker.data.network.ITunesNetworkClient
import com.practicum.playlistmaker.data.player.MediaPlayerDataSource
import com.practicum.playlistmaker.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SharingInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

// 1. Централизованное создание зависимостей приложения
object Creator {

    // 1.1. Контекст приложения
    private lateinit var appContext: Context

    // 2. Инициализация Creator
    fun init(context: Context) {
        if (!::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }

    // 3. Общие зависимости
    // 3.1. SharedPreferences
    private val preferences by lazy {
        check(::appContext.isInitialized) {
            "Ошибка: необходимо вызвать Creator.init() в App"
        }

        PreferencesProvider.create(appContext)
    }

    // 3.2. Gson
    private val gson by lazy {
        Gson()
    }

    // 4. Интерактор поиска треков
    private val tracksInteractor: TracksInteractor by lazy {
        TracksInteractorImpl(
            TracksRepositoryImpl(
                ITunesNetworkClient.apiService
            )
        )
    }

    // 5. Интерактор истории поиска
    private val historyInteractor: HistoryInteractor by lazy {
        HistoryInteractorImpl(
            HistoryRepositoryImpl(
                HistoryLocalStorage(
                    preferences,
                    gson
                )
            )
        )
    }

    // 5.1. Интерактор пользовательских настроек
    private val settingsInteractor: SettingsInteractor by lazy {
        SettingsInteractorImpl(
            SettingsRepositoryImpl(
                SettingsLocalStorage(preferences)
            )
        )
    }

    // 5.2. Интерактор внешних действий
    private val sharingInteractor: SharingInteractor by lazy {
        SharingInteractorImpl(
            SharingRepositoryImpl(
                SharingLocalStorage(appContext)
            )
        )
    }

    // 6. Предоставление интеракторов экранам
    // 6.1. Получение интерактора поиска
    fun provideTracksInteractor(): TracksInteractor {
        return tracksInteractor
    }

    // 6.2. Получение интерактора истории
    fun provideHistoryInteractor(): HistoryInteractor {
        return historyInteractor
    }

    // 6.3. Получение интерактора настроек
    fun provideSettingsInteractor(): SettingsInteractor {
        return settingsInteractor
    }

    // 6.4. Получение интерактора внешних действий
    fun provideSharingInteractor(): SharingInteractor {
        return sharingInteractor
    }

    // 7. Создание отдельного интерактора аудиоплеера
    fun createPlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(
            PlayerRepositoryImpl(
                MediaPlayerDataSource()
            )
        )
    }

}