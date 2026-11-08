package com.practicum.playlistmaker.domain.models


// 1. Результат поиска треков
sealed class SearchResult {

    // 1.1. Успешный результат поиска
    data class Success(
        val tracks: List<Track>
    ) : SearchResult()

    // 1.2. Ошибка выполнения поиска
    object Error : SearchResult()
}