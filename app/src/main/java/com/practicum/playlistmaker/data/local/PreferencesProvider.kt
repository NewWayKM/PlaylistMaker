package com.practicum.playlistmaker.data.local

import android.content.Context
import android.content.SharedPreferences

// 1. Создание общего хранилища только в слое Data
object PreferencesProvider {

    // 1.1. Имя файла и ключи остаются от спринта 15
    fun create(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(
            StorageKeys.PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }
}