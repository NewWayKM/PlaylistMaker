package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

// 1. Действия с историей поиска
interface HistoryInteractor {

    // 1.1. Чтение истории
    fun read(): List<Track>

    // 1.2. Добавление трека
    fun add(track: Track)

    // 1.3. Очистка истории
    fun clear()
}