package com.practicum.playlistmaker.data.dto

// 1. Ответ сервера поиска треков
data class TrackSearchResponseDto(
    val resultCount: Int = 0,
    val results: List<TrackDto?>? = emptyList()
)