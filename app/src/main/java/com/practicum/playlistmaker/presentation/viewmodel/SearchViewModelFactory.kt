package com.practicum.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.Creator

// 1. Создание ViewModel экрана поиска
class SearchViewModelFactory(
    private val initialQuery: String = ""
) : ViewModelProvider.Factory {

    // 2. Передача интеракторов из Domain
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SearchViewModel::class.java) {
            "Неизвестный класс ViewModel: ${modelClass.name}"
        }

        return SearchViewModel(
            Creator.provideTracksInteractor(),
            Creator.provideHistoryInteractor(),
            initialQuery
        ) as T
    }
}