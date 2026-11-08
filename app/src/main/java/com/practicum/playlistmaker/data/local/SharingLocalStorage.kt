package com.practicum.playlistmaker.data.local

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.SharingDataDto

// 1. Источник данных для внешних действий
class SharingLocalStorage(context: Context) {

    // 1.1. Используем Application Context
    private val appContext = context.applicationContext

    // 2. Получение данных из ресурсов приложения
    fun read(): SharingDataDto {
        return SharingDataDto(
            appLink = appContext.getString(R.string.share_message),
            supportEmail = appContext.getString(R.string.support_email),
            supportSubject = appContext.getString(R.string.support_email_subject),
            supportBody = appContext.getString(R.string.support_email_body),
            agreementUrl = appContext.getString(R.string.user_agreement_url)
        )
    }
}