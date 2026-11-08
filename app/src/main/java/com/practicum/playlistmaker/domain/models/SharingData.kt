package com.practicum.playlistmaker.domain.models

// 1. Данные для действий «Поделиться», «Поддержка» и «Соглашение»
data class SharingData(
    val appLink: String,
    val supportEmail: String,
    val supportSubject: String,
    val supportBody: String,
    val agreementUrl: String
)