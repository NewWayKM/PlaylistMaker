package com.practicum.playlistmaker.presentation.state

import com.practicum.playlistmaker.domain.models.PlayerSnapshot
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track

// 1. Данные трека и состояние воспроизведения в одном снимке экрана
data class PlayerScreenState(
    val track: Track,
    val playback: PlayerSnapshot = PlayerSnapshot(PlayerState.IDLE)
)