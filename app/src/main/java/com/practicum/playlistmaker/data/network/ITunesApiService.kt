package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackSearchResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// 1. Интерфейс сетевого API iTunes
interface ITunesApiService {

    // 1.1. Поиск музыкальных треков
    @GET("search?entity=song")
    fun searchTracks(
        @Query("term") searchText: String
    ): Call<TrackSearchResponseDto>
}