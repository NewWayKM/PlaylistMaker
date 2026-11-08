package com.practicum.playlistmaker.data.local

import android.content.SharedPreferences
import android.util.Log
import com.practicum.playlistmaker.data.dto.ThemeSettingsDto

// 1. Источник локальных настроек приложения
class SettingsLocalStorage(
    private val preferences: SharedPreferences
) {

    // 2. Чтение сохранённой темы
    fun read(): ThemeSettingsDto {
        return try {
            val value = if (
                preferences.contains(StorageKeys.DARK_THEME_KEY)
            ) {
                preferences.getBoolean(
                    StorageKeys.DARK_THEME_KEY,
                    false
                )
            } else {
                null
            }

            ThemeSettingsDto(value)

        } catch (error: ClassCastException) {
            Log.w(TAG, "У ключа темы неверный тип", error)
            ThemeSettingsDto(null)
        }
    }

    // 3. Сохранение выбранной темы
    fun save(settings: ThemeSettingsDto) {
        val editor = preferences.edit()
        val darkTheme = settings.darkTheme

        if (darkTheme == null) {
            editor.remove(StorageKeys.DARK_THEME_KEY)
        } else {
            editor.putBoolean(StorageKeys.DARK_THEME_KEY, darkTheme)
        }

        editor.apply()
    }

    // 4. Константы
    companion object {
        private const val TAG = "SettingsLocalStorage"
    }
}