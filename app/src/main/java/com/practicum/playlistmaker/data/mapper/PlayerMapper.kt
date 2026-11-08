package com.practicum.playlistmaker.data.mapper

import com.practicum.playlistmaker.data.dto.PlayerSnapshotDto
import com.practicum.playlistmaker.data.dto.PlayerStateDto
import com.practicum.playlistmaker.domain.models.PlayerSnapshot
import com.practicum.playlistmaker.domain.models.PlayerState

// 1. Преобразование состояния плеера из Data в Domain
object PlayerMapper {

    // 1.1. Преобразование снимка состояния
    fun toDomain(dto: PlayerSnapshotDto): PlayerSnapshot {
        return PlayerSnapshot(
            state = when (dto.state) {
                PlayerStateDto.IDLE -> PlayerState.IDLE
                PlayerStateDto.PREPARING -> PlayerState.PREPARING
                PlayerStateDto.PREPARED -> PlayerState.PREPARED
                PlayerStateDto.PLAYING -> PlayerState.PLAYING
                PlayerStateDto.PAUSED -> PlayerState.PAUSED
                PlayerStateDto.COMPLETED -> PlayerState.COMPLETED
                PlayerStateDto.ERROR -> PlayerState.ERROR
                PlayerStateDto.RELEASED -> PlayerState.RELEASED
            },
            positionMillis = dto.positionMillis
        )
    }
}