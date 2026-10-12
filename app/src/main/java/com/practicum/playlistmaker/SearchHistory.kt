package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(
    private val sharedPreferences: SharedPreferences
) {

    private val gson = Gson()

    fun read(): ArrayList<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
            ?: return arrayListOf()

        return try {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            gson.fromJson<ArrayList<Track>>(json, type)
                ?: arrayListOf()
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    fun add(track: Track) {
        val history = read()

        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }

        sharedPreferences.edit()
            .putString(HISTORY_KEY, gson.toJson(history))
            .apply()
    }

    fun clear() {
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()
    }

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}