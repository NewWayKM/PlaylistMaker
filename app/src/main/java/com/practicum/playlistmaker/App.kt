package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private val preferences by lazy {
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
    }

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        if (preferences.contains(DARK_THEME_KEY)) {
            val savedTheme = preferences.getBoolean(DARK_THEME_KEY, false)
            switchTheme(savedTheme)
        } else {
            darkTheme = resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        preferences.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        private const val PREFERENCES_NAME = "playlist_maker_preferences"
        private const val DARK_THEME_KEY = "dark_theme"
    }
}