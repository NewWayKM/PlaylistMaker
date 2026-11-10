package com.practicum.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SharingInteractor
import com.practicum.playlistmaker.presentation.state.SettingsAction
import com.practicum.playlistmaker.presentation.state.SettingsScreenState

// 1. Получение данных и обработка действий экрана настроек
class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    // 2. Изменяемое состояние доступно только внутри ViewModel
    private val mutableState = MutableLiveData(
        SettingsScreenState(darkTheme = settingsInteractor.read().darkTheme)
    )
    val state: LiveData<SettingsScreenState> = mutableState

    // 3. Сохранение темы по действию пользователя
    fun onThemeChanged(enabled: Boolean) {
        val current = requireNotNull(mutableState.value)
        if (current.darkTheme == enabled) return

        settingsInteractor.setDarkTheme(enabled)
        mutableState.value = current.copy(darkTheme = enabled)
    }

    // 4. Подготовка данных для внешних приложений
    fun onShareClicked() {
        publishAction(SettingsAction.Share(sharingInteractor.read().appLink))
    }

    fun onSupportClicked() {
        publishAction(SettingsAction.Support(sharingInteractor.read()))
    }

    fun onAgreementClicked() {
        publishAction(SettingsAction.Agreement(sharingInteractor.read().agreementUrl))
    }

    // 5. Подтверждение обработки предотвращает повтор после пересоздания Activity
    fun onExternalActionHandled() {
        val current = requireNotNull(mutableState.value)
        if (current.pendingAction != null) {
            mutableState.value = current.copy(pendingAction = null)
        }
    }

    // 6. Передача действия через то же состояние экрана
    private fun publishAction(action: SettingsAction) {
        mutableState.value = requireNotNull(mutableState.value).copy(
            pendingAction = action
        )
    }
}