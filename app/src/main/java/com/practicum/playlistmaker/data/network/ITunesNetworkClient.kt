package com.practicum.playlistmaker.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 1. Сетевой клиент iTunes
object ITunesNetworkClient {

    // 1.1. Базовый адрес API
    private const val BASE_URL = "https://itunes.apple.com/"

    // 2. Создание Retrofit-сервиса
    val apiService: ITunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }
}