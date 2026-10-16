package com.practicum.playlistmaker

object TrackReleaseYearFormatter {

    fun format(releaseDate: String?): String? {
        if (releaseDate.isNullOrBlank()) {
            return null
        }

        val year = releaseDate.take(4)

        return year.takeIf {
            it.length == 4 && it.all(Char::isDigit)
        }
    }
}
