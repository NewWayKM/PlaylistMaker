package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.HistoryRepository

// 1. Бизнес-логика истории поиска
class HistoryInteractorImpl(
    private val repository: HistoryRepository
) : HistoryInteractor {

    // 2. Чтение истории без дубликатов, максимум 10 треков
    override fun read(): List<Track> {
        return repository.read()
            .distinctBy { it.trackId }
            .take(MAX_HISTORY_SIZE)
    }

    // 3. Добавление трека в начало истории
    override fun add(track: Track) {

        // 3.1. Получаем текущую историю
        val history = read().toMutableList()

        // 3.2. Удаляем предыдущую запись этого трека
        history.removeAll { it.trackId == track.trackId }

        // 3.3. Добавляем выбранный трек первым
        history.add(0, track)

        // 3.4. Сохраняем не более 10 треков
        repository.save(history.take(MAX_HISTORY_SIZE))
    }

    // 4. Очистка истории
    override fun clear() {
        repository.clear()
    }

    // 5. Константы
    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}