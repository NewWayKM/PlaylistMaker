package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track

// 1. Контракт хранения истории поиска
interface HistoryRepository {
    // 1.1. Чтение сохранённых треков
    fun read(): List<Track>

    // 1.2. Сохранение списка треков
    fun save(tracks: List<Track>)

    // 1.3. Очистка истории поиска
    fun clear()
}