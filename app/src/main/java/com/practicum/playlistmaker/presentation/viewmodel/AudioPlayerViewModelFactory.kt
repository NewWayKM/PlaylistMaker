package com.practicum.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.models.Track

// 1. Фабрика хранит только переданный трек, а не Activity или Context
class AudioPlayerViewModelFactory(
    private val track: Track
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == AudioPlayerViewModel::class.java) {
            "Неизвестный класс ViewModel: ${modelClass.name}"
        }

        // 1.1. Новый плеер создаётся здесь, только когда Provider создаёт ViewModel
        return AudioPlayerViewModel(
            Creator.createPlayerInteractor(),
            track
        ) as T
    }
}