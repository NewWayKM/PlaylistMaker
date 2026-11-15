package com.practicum.playlistmaker.di

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.practicum.playlistmaker.data.local.HistoryLocalStorage
import com.practicum.playlistmaker.data.local.SettingsLocalStorage
import com.practicum.playlistmaker.data.local.SharingLocalStorage
import com.practicum.playlistmaker.data.local.StorageKeys
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.player.MediaPlayerDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 1. Создание технических зависимостей и источников данных
val dataModule = module {

    // 1.1. Сохраняем прежнее имя файла настроек и истории
    single {
        androidContext().getSharedPreferences(
            StorageKeys.PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    single { Gson() }

    // 1.2. Retrofit создаётся здесь, а репозиторий получает готовый API
    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(ITunesApiService::class.java)
    }

    // 1.3. Локальные источники данных
    single { HistoryLocalStorage(get(), get()) }
    single { SettingsLocalStorage(get()) }
    single { SharingLocalStorage(androidContext()) }

    // 1.4. У каждого получателя свой Handler, но один главный поток
    factory { Handler(Looper.getMainLooper()) }

    // 1.5. Неизменяемые настройки звука можно использовать совместно
    single {
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
    }

    // 1.6. Освобождённый MediaPlayer повторно использовать нельзя
    factory { MediaPlayer() }

    // 1.7. Новый источник плеера получает фабрику, а не общий MediaPlayer
    factory {
        MediaPlayerDataSource(
            playerFactory = { get<MediaPlayer>() },
            audioAttributes = get()
        )
    }
}
