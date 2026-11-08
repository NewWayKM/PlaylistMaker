package com.practicum.playlistmaker.data.local

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.dto.TrackDto

// 1. Источник локальных данных истории поиска
class HistoryLocalStorage(
    private val preferences: SharedPreferences,
    private val gson: Gson
) {

    // 2. Чтение истории из SharedPreferences
    fun read(): List<TrackDto> {
        return try {
            val json = preferences.getString(
                StorageKeys.HISTORY_KEY,
                null
            ) ?: return emptyList()

            // 2.1. Преобразование JSON в список DTO
            val type = object : TypeToken<List<TrackDto?>>() {}.type

            gson.fromJson<List<TrackDto?>>(json, type)
                ?.filterNotNull()
                .orEmpty()

        } catch (error: JsonParseException) {
            Log.w(TAG, "Не удалось прочитать JSON истории", error)
            emptyList()

        } catch (error: ClassCastException) {
            Log.w(TAG, "У ключа истории неверный тип", error)
            emptyList()
        }
    }

    // 3. Сохранение истории
    fun save(tracks: List<TrackDto>) {
        preferences.edit()
            .putString(StorageKeys.HISTORY_KEY, gson.toJson(tracks))
            .apply()
    }

    // 4. Очистка истории
    fun clear() {
        preferences.edit()
            .remove(StorageKeys.HISTORY_KEY)
            .apply()
    }

    // 5. Константы
    companion object {
        private const val TAG = "HistoryLocalStorage"
    }
}