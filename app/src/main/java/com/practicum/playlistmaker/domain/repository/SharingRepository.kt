package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.SharingData

// 1. Контракт получения данных для внешних действий
interface SharingRepository {
    // 1.1. Получение данных для отправки, поддержки и соглашения
    fun read(): SharingData
}