package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.models.PlayerSnapshot
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.PlayerRepository

// 1. Бизнес-логика управления аудиоплеером
class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    // 2. Подготовка аудиофрагмента
    override fun prepare(track: Track) {
        repository.prepare(track.previewUrl)
    }

    // 3. Переключение воспроизведения
    override fun togglePlayback() {
        when (repository.snapshot().state) {

            // 3.1. Если играет — ставим на паузу
            PlayerState.PLAYING -> repository.pause()

            // 3.2. Если готов, на паузе или завершён — запускаем
            PlayerState.PREPARED,
            PlayerState.PAUSED,
            PlayerState.COMPLETED -> repository.start()

            // 3.3. В остальных состояниях ничего не делаем
            else -> Unit
        }
    }

    // 4. Пауза при уходе приложения в фон
    override fun pause() {
        if (repository.snapshot().state == PlayerState.PLAYING) {
            repository.pause()
        }
    }

    // 5. Получение текущего состояния
    override fun snapshot(): PlayerSnapshot {
        return repository.snapshot()
    }

    // 6. Подписка на изменение состояния
    override fun setListener(listener: ((PlayerSnapshot) -> Unit)?) {
        repository.setListener(listener)
    }

    // 7. Освобождение ресурсов
    override fun release() {
        repository.setListener(null)
        repository.release()
    }
}