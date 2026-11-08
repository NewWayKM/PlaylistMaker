package com.practicum.playlistmaker.domain.models

// 1. Состояния аудиоплеера
enum class PlayerState {
    // 1.1. Начальное состояние
    IDLE,

    // 1.2. Подготовка аудиофайла
    PREPARING,

    // 1.3. Аудиофайл подготовлен
    PREPARED,

    // 1.4. Воспроизведение
    PLAYING,

    // 1.5. Пауза
    PAUSED,

    // 1.6. Воспроизведение завершено
    COMPLETED,

    // 1.7. Ошибка воспроизведения
    ERROR,

    // 1.8. Ресурсы освобождены
    RELEASED
}