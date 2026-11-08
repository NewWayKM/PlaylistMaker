package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.Cancellable
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.repository.TracksRepository

// 1. Бизнес-логика поиска треков
class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    // 1.1. Подготовка запроса
    override fun search(
        query: String,
        consumer: (SearchResult) -> Unit
    ): Cancellable {

        val normalizedQuery = query.trim()

        // 1.2. Не отправляем пустой запрос
        if (normalizedQuery.isEmpty()) {
            consumer(SearchResult.Success(emptyList()))
            return Cancellable { }
        }

        // 1.3. Передаём запрос репозиторию
        return repository.search(normalizedQuery, consumer)
    }
}