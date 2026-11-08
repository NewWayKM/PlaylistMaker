package com.practicum.playlistmaker.data.dto

// 1. Данные внешних действий из ресурсов приложения
data class SharingDataDto(
    val appLink: String,
    val supportEmail: String,
    val supportSubject: String,
    val supportBody: String,
    val agreementUrl: String
)