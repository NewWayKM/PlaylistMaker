package com.practicum.playlistmaker.presentation.model

import android.os.Parcelable
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.parcelize.Parcelize

// 1. Модель для передачи трека между Activity
@Parcelize
data class TrackParcel(
    val trackName: String? = null,
    val artistName: String? = null,
    val trackTimeMillis: Long? = null,
    val artworkUrl100: String? = null,
    val trackId: Long = 0L,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val previewUrl: String? = null
) : Parcelable {

    // 2. Преобразование в доменную модель
    fun toDomain(): Track {
        return Track(
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            trackId = trackId,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl
        )
    }

    // 3. Преобразование доменной модели для Intent
    companion object {

        fun fromDomain(track: Track): TrackParcel {
            return TrackParcel(
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                trackId = track.trackId,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl
            )
        }
    }
}