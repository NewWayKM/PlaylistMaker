package com.practicum.playlistmaker.di

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
import org.koin.dsl.module

// 1. Создание интеракторов слоя Domain
val interactorModule = module {

    // 1.1. Интерактор поиска треков
    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    // 1.2. Интерактор истории поиска
    single<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    // 1.3. Интерактор пользовательских настроек
    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    // 1.4. Интерактор внешних действий
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    // 1.5. Отдельный интерактор для каждого плеера
    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }
}
