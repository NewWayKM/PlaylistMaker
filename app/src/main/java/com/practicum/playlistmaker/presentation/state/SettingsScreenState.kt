package com.practicum.playlistmaker.presentation.state

import com.practicum.playlistmaker.domain.models.SharingData

// 1. Состояние настроек и ожидающее внешнее действие
data class SettingsScreenState(
    val darkTheme: Boolean?,
    val pendingAction: SettingsAction? = null
)

// 2. Данные для внешнего действия
sealed interface SettingsAction {
    data class Share(val appLink: String) : SettingsAction
    data class Support(val data: SharingData) : SettingsAction
    data class Agreement(val url: String) : SettingsAction
}