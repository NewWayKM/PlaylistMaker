package com.practicum.playlistmaker

data class TrackSearchResponse(
    val resultCount: Int = 0,
    val results: List<Track> = emptyList()
)