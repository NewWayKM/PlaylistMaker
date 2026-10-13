package com.practicum.playlistmaker

import com.google.gson.Gson

object TrackJsonConverter {

    private val gson = Gson()

    // 1. Преобразование объекта Track в JSON
    fun toJson(track: Track): String {
        return gson.toJson(track)
    }

    // 2. Восстановление объекта Track из JSON
    fun fromJson(json: String): Track? {
        return try {
            gson.fromJson(json, Track::class.java)
        } catch (e: Exception) {
            null
        }
    }
}