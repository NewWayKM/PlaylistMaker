package com.practicum.playlistmaker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackName: String? = null,
    val artistName: String? = null,
    val trackTimeMillis: Long? = null,
    val artworkUrl100: String? = null,
    val trackId: Long = 0L,

    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null
) : Parcelable {
    // 1. Получение обложки высокого разрешения для аудиоплеера
    fun getCoverArtwork(): String? {
        return artworkUrl100
            ?.takeIf { it.isNotBlank() }
            ?.replaceAfterLast('/', "512x512bb.jpg")
    }
}