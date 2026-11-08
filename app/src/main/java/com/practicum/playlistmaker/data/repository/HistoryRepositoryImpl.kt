package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.local.HistoryLocalStorage
import com.practicum.playlistmaker.data.mapper.TrackMapper
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.HistoryRepository

// 1. Реализация репозитория истории поиска
class HistoryRepositoryImpl(
    private val storage: HistoryLocalStorage
) : HistoryRepository {

    // 1.1. Чтение истории и преобразование DTO в Domain
    override fun read(): List<Track> {
        return storage.read().map(TrackMapper::toDomain)
    }

    // 1.2. Преобразование Domain в DTO и сохранение
    override fun save(tracks: List<Track>) {
        storage.save(tracks.map(TrackMapper::toDto))
    }

    // 1.3. Очистка истории
    override fun clear() {
        storage.clear()
    }
}