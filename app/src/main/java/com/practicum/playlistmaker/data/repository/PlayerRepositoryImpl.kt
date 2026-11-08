package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.dto.PlayerSourceDto
import com.practicum.playlistmaker.data.mapper.PlayerMapper
import com.practicum.playlistmaker.data.player.MediaPlayerDataSource
import com.practicum.playlistmaker.domain.models.PlayerSnapshot
import com.practicum.playlistmaker.domain.repository.PlayerRepository

// 1. Реализация репозитория аудиоплеера
class PlayerRepositoryImpl(
    private val source: MediaPlayerDataSource
) : PlayerRepository {

    // 2. Подготовка аудиофрагмента
    override fun prepare(previewUrl: String?) {
        source.prepare(PlayerSourceDto(previewUrl))
    }

    // 3. Запуск воспроизведения
    override fun start() {
        source.start()
    }

    // 4. Приостановка воспроизведения
    override fun pause() {
        source.pause()
    }

    // 5. Получение доменной модели состояния
    override fun snapshot(): PlayerSnapshot {
        return PlayerMapper.toDomain(source.snapshot())
    }

    // 6. Подписка на изменение состояния
    override fun setListener(
        listener: ((PlayerSnapshot) -> Unit)?
    ) {
        if (listener == null) {
            source.setListener(null)
        } else {
            source.setListener { dto ->
                listener(PlayerMapper.toDomain(dto))
            }
        }

    }

    // 7. Освобождение ресурсов
    override fun release() {
        source.release()
    }
}


