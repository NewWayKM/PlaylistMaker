package com.practicum.playlistmaker.data.dto

// 1. Технические состояния аудиоплеера
enum class PlayerStateDto {
    IDLE,
    PREPARING,
    PREPARED,
    PLAYING,
    PAUSED,
    COMPLETED,
    ERROR,
    RELEASED
}