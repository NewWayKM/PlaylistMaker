package com.practicum.playlistmaker.presentation.util

import java.util.Locale

object TrackDurationFormatter {

    fun format(durationMillis: Long?): String {
        if (durationMillis == null || durationMillis < 0) {
            return "--:--"
        }

        val totalSeconds = durationMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format(
            Locale.US,
            "%02d:%02d",
            minutes,
            seconds
        )
    }
}