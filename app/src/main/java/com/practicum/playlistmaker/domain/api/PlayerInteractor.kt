package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.PlayerSnapshot
import com.practicum.playlistmaker.domain.models.Track

// 1. Операции управления аудиоплеером
interface PlayerInteractor {
    // 1.1. Подготовка выбранного трека
    fun prepare(track: Track)

    // 1.2. Переключение Play/Pause
    fun togglePlayback()

    // 1.3. Приостановка воспроизведения
    fun pause()

    // 1.4. Получение состояния и времени
    fun snapshot(): PlayerSnapshot

    // 1.5. Подписка на изменение состояния
    fun setListener(listener: ((PlayerSnapshot) -> Unit)?)

    // 1.6. Освобождение ресурсов
    fun release()
}