package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.api.Cancellable
import com.practicum.playlistmaker.domain.models.SearchResult

// 1. Контракт получения треков
// Реализация находится в слое Data
interface TracksRepository {

    // 1.1. Асинхронный поиск треков
    fun search(
        query: String,
        consumer: (SearchResult) -> Unit
    ): Cancellable
}