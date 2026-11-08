package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.local.SharingLocalStorage
import com.practicum.playlistmaker.data.mapper.SharingMapper
import com.practicum.playlistmaker.domain.models.SharingData
import com.practicum.playlistmaker.domain.repository.SharingRepository

// 1. Реализация репозитория внешних действий
class SharingRepositoryImpl(
    private val storage: SharingLocalStorage
) : SharingRepository {

    // 1.1. Получение данных через Mapper
    override fun read(): SharingData {
        return SharingMapper.toDomain(storage.read())
    }
}