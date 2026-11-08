package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.SearchResult

// 1. Операция поиска треков
interface TracksInteractor {

    // 1.1. Поиск с возможностью отмены
    fun search(
        query: String,
        consumer: (SearchResult) -> Unit
    ): Cancellable
}