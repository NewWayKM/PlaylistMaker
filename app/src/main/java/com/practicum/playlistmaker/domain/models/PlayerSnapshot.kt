package com.practicum.playlistmaker.domain.models

// 1. Снимок состояния и позиции воспроизведения
data class PlayerSnapshot(
    val state: PlayerState,
    val positionMillis: Long = 0L
)