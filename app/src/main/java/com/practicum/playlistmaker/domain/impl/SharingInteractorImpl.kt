package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SharingInteractor
import com.practicum.playlistmaker.domain.models.SharingData
import com.practicum.playlistmaker.domain.repository.SharingRepository

// 1. Получение данных для внешних действий
class SharingInteractorImpl(
    private val repository: SharingRepository
) : SharingInteractor {

    // 1.1. Передача данных из репозитория
    override fun read(): SharingData {
        return repository.read()
    }
}