package com.practicum.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.Creator

// 1. Фабрика создаёт ViewModel с зависимостями из Domain
class SettingsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SettingsViewModel::class.java) {
            "Неизвестный класс ViewModel: ${modelClass.name}"
        }

        return SettingsViewModel(
            Creator.provideSettingsInteractor(),
            Creator.provideSharingInteractor()
        ) as T
    }
}