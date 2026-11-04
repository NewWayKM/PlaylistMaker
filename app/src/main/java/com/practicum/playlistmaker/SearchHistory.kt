package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonParseException

// 1. Управление историей поиска
class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) {

    // 2. Чтение истории поиска
    fun read(): ArrayList<Track> {
        val json = sharedPreferences.getString(AppConstants.HISTORY_KEY, null)
            ?: return arrayListOf()

        // 2.1. Преобразование JSON в список треков
        return try {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            gson.fromJson<ArrayList<Track>>(json, type)
                ?: arrayListOf()
        } catch (e: JsonParseException) {
            // 2.2. Некорректные данные истории
            arrayListOf()
        }
    }

    // 3. Добавление трека в историю
    fun add(track: Track) {
        val history = read()

        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }

        // 3.1. Сохранение обновлённой истории
        sharedPreferences.edit()
            .putString(AppConstants.HISTORY_KEY, gson.toJson(history))
            .apply()
    }

    // 4. Очистка истории поиска
    fun clear() {
        sharedPreferences.edit()
            .remove(AppConstants.HISTORY_KEY)
            .apply()
    }

    // 5. Константы
    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}