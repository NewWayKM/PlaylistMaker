package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.viewmodel.AudioPlayerViewModel
import com.practicum.playlistmaker.presentation.viewmodel.SearchViewModel
import com.practicum.playlistmaker.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// 1. Создание ViewModel для экранов приложения
val viewModelModule = module {

    // 1.1. ViewModel экрана поиска
    viewModel { (initialQuery: String) ->
        SearchViewModel(
            get(),
            get(),
            initialQuery
        )
    }

    // 1.2. ViewModel экрана настроек
    viewModel {
        SettingsViewModel(
            get(),
            get()
        )
    }

    // 1.3. ViewModel аудиоплеера
    viewModel { (track: Track) ->
        AudioPlayerViewModel(
            get(),
            track
        )
    }
}
