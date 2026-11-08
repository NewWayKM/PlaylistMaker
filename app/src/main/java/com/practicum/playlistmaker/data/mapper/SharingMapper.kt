package com.practicum.playlistmaker.data.mapper

import com.practicum.playlistmaker.data.dto.SharingDataDto
import com.practicum.playlistmaker.domain.models.SharingData

// 1. Преобразование данных внешних действий
object SharingMapper {

    // 1.1. DTO -> Domain
    fun toDomain(dto: SharingDataDto): SharingData {
        return SharingData(
            appLink = dto.appLink,
            supportEmail = dto.supportEmail,
            supportSubject = dto.supportSubject,
            supportBody = dto.supportBody,
            agreementUrl = dto.agreementUrl
        )
    }
}