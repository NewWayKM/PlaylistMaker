package com.practicum.playlistmaker.presentation.util

// 1. Подготовка URL обложки высокого разрешения
object TrackCoverFormatter {

    // 1.1. Преобразование адреса обложки
    fun format(artworkUrl: String?): String? {
        return artworkUrl
            ?.takeIf { it.isNotBlank() }
            ?.replaceAfterLast('/', "512x512bb.jpg")
    }
}