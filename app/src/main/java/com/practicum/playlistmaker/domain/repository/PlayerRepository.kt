package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.PlayerSnapshot

// 1. Контракт управления аудиоплеером
interface PlayerRepository {
    // 1.1. Подготовка аудиофрагмента
    fun prepare(previewUrl: String?)

    // 1.2. Запуск или продолжение воспроизведения
    fun start()

    // 1.3. Приостановка воспроизведения
    fun pause()

    // 1.4. Получение текущего состояния и позиции
    fun snapshot(): PlayerSnapshot

    // 1.5. Подписка на изменения состояния
    fun setListener(listener: ((PlayerSnapshot) -> Unit)?)

    // 1.6. Освобождение ресурсов плеера
    fun release()
}