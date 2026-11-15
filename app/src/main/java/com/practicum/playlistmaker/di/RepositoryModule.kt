package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.data.repository.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.repository.HistoryRepository
import com.practicum.playlistmaker.domain.repository.PlayerRepository
import com.practicum.playlistmaker.domain.repository.SettingsRepository
import com.practicum.playlistmaker.domain.repository.SharingRepository
import com.practicum.playlistmaker.domain.repository.TracksRepository
import org.koin.dsl.module

// 1. Связываем интерфейсы Domain с реализациями Data
val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(api = get(), callbackHandler = get())
    }

    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<SharingRepository> { SharingRepositoryImpl(get()) }

    // 1.1. Репозиторий плеера не разделяется между разными ViewModel
    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }
}
