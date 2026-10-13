package com.practicum.playlistmaker

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
) {
    // 1. Получение обложки высокого разрешения для аудиоплеера
    fun getCoverArtwork(): String? {
        return artworkUrl100
            ?.takeIf { it.isNotBlank() }
            ?.replaceAfterLast('/', "512x512bb.jpg")
    }
}