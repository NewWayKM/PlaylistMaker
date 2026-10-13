package com.practicum.playlistmaker

data class Track(
    val trackName: String? = null,
    val artistName: String? = null,
    val trackTimeMillis: Long? = null,
    val artworkUrl100: String? = null,
    val trackId: Long = 0L
)