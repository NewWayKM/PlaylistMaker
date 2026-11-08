package com.practicum.playlistmaker.domain.api

// 1. Отмена операции без передачи Retrofit.Call в интерфейс экрана
fun interface Cancellable {
    fun cancel()
}