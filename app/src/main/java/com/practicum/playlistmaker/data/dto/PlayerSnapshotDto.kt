package com.practicum.playlistmaker.data.dto

// 1. Техническое состояние и позиция аудиоплеера
data class PlayerSnapshotDto(
    val state: PlayerStateDto,
    val positionMillis: Long = 0L
)