package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.SharingData

// 1. Получение данных для внешних действий
interface SharingInteractor {
    // 1.1. Данные для отправки, поддержки и соглашения
    fun read(): SharingData
}