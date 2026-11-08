package com.practicum.playlistmaker.data.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.practicum.playlistmaker.data.dto.PlayerSnapshotDto
import com.practicum.playlistmaker.data.dto.PlayerSourceDto
import com.practicum.playlistmaker.data.dto.PlayerStateDto
import java.io.IOException

// 1. Управление MediaPlayer внутри слоя Data
class MediaPlayerDataSource(
    private val playerFactory: () -> MediaPlayer = { MediaPlayer() }
) {

    // 1.1. Экземпляр плеера и его состояние
    private var player: MediaPlayer? = null
    private var state = PlayerStateDto.IDLE
    private var listener: ((PlayerSnapshotDto) -> Unit)? = null

    // 2. Асинхронная подготовка аудиофрагмента
    fun prepare(source: PlayerSourceDto) {
        if (state == PlayerStateDto.RELEASED) return

        releasePlayer()
        state = PlayerStateDto.IDLE

        val url = source.previewUrl?.takeIf { it.isNotBlank() }

        if (url == null) {
            fail("У трека отсутствует previewUrl")
            return
        }

        try {
            // 2.1. Создание нового MediaPlayer
            val newPlayer = playerFactory()
            player = newPlayer

            // 2.2. Аудио подготовлено
            newPlayer.setOnPreparedListener { preparedPlayer ->
                if (preparedPlayer === player &&
                    state == PlayerStateDto.PREPARING
                ) {
                    state = PlayerStateDto.PREPARED
                    publish()
                }
            }

            // 2.3. Аудиофрагмент завершился
            newPlayer.setOnCompletionListener { completedPlayer ->
                if (completedPlayer === player &&
                    (state == PlayerStateDto.PLAYING ||
                            state == PlayerStateDto.PAUSED)
                ) {
                    state = PlayerStateDto.COMPLETED
                    publish()
                }
            }

            // 2.4. Ошибка воспроизведения
            newPlayer.setOnErrorListener { failedPlayer, what, extra ->
                if (failedPlayer === player) {
                    fail("Ошибка MediaPlayer: what=$what, extra=$extra")
                }
                true
            }

            // 2.5. Настройка источника и параметров аудио
            newPlayer.setDataSource(url)

            newPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )

            state = PlayerStateDto.PREPARING
            publish()

            newPlayer.prepareAsync()

        } catch (error: IOException) {
            fail("Источник аудио недоступен", error)
        } catch (error: IllegalArgumentException) {
            fail("Некорректный источник аудио", error)
        } catch (error: IllegalStateException) {
            fail("Ошибка состояния при подготовке", error)
        } catch (error: SecurityException) {
            fail("Нет доступа к источнику аудио", error)
        }
    }

    // 3. Запуск или продолжение воспроизведения
    fun start() {
        if (state != PlayerStateDto.PREPARED &&
            state != PlayerStateDto.PAUSED &&
            state != PlayerStateDto.COMPLETED
        ) return

        val currentPlayer = player ?: return

        try {
            // 3.1. После завершения возвращаемся в начало
            if (state == PlayerStateDto.COMPLETED) {
                currentPlayer.seekTo(0)
            }
            // 3.2. Запускаем воспроизведение
            currentPlayer.start()
            state = PlayerStateDto.PLAYING
            publish()
        } catch (error: IllegalStateException) {
            fail("Ошибка состояния при запуске", error)
        }
    }

    // 4. Приостановка воспроизведения
    fun pause() {
        if (state != PlayerStateDto.PLAYING) return

        val currentPlayer = player ?: return

        try {
            currentPlayer.pause()
            state = PlayerStateDto.PAUSED
            publish()
        } catch (error: IllegalStateException) {
            fail("Ошибка состояния при паузе", error)
        }
    }

    // 5. Получение состояния и фактической позиции
    fun snapshot(): PlayerSnapshotDto {
        val canReadPosition =
            state == PlayerStateDto.PREPARED ||
                    state == PlayerStateDto.PLAYING ||
                    state == PlayerStateDto.PAUSED

        val position = if (canReadPosition) {
            try {
                player?.currentPosition?.toLong()
                    ?.coerceAtLeast(0L) ?: 0L
            } catch (error: IllegalStateException) {
                fail("Ошибка чтения позиции", error)
                0L
            }
        } else {
            0L
        }

        // После завершения показываем 00:00
        return PlayerSnapshotDto(state, position)
    }

    // 6. Подписка на изменения состояния
    fun setListener(listener: ((PlayerSnapshotDto) -> Unit)?) {
        this.listener = listener

        if (listener != null) {
            publish()
        }
    }

    // 7. Уведомление подписчика
    private fun publish() {
        listener?.invoke(snapshot())
    }

    // 8. Обработка ошибки
    private fun fail(message: String, error: Throwable? = null) {
        Log.w(TAG, message, error)
        releasePlayer()
        state = PlayerStateDto.ERROR
        listener?.invoke(PlayerSnapshotDto(state))
    }

    // 9. Освобождение ресурсов
    fun release() {
        listener = null
        releasePlayer()
        state = PlayerStateDto.RELEASED
    }

    // 9.1. Освобождение конкретного экземпляра MediaPlayer
    private fun releasePlayer() {
        val oldPlayer = player
        player = null

        oldPlayer?.setOnPreparedListener(null)
        oldPlayer?.setOnCompletionListener(null)
        oldPlayer?.setOnErrorListener(null)
        oldPlayer?.release()
    }

    // 10. Константы
    companion object {
        private const val TAG = "MediaPlayerDataSource"
    }
}