package com.practicum.playlistmaker.presentation.state

import com.practicum.playlistmaker.domain.models.Track

// 1. Возможные состояния результатов поиска
sealed interface SearchContent {
    object Idle : SearchContent
    object Loading : SearchContent
    data class Tracks(val tracks: List<Track>) : SearchContent
    object Empty : SearchContent
    object Error : SearchContent
}

// 2. Общее состояние экрана поиска
data class SearchScreenState(
    val query: String = "",
    val content: SearchContent = SearchContent.Idle,
    val history: List<Track> = emptyList(),
    val isInputFocused: Boolean = true,
    val pendingTrack: Track? = null
) {

    // 2.1. Отображение истории при пустом поле поиска
    val showHistory: Boolean
        get() = query.isEmpty() && isInputFocused &&
                content == SearchContent.Idle && history.isNotEmpty()
}