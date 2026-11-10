package com.practicum.playlistmaker.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.models.PlayerSnapshot
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.state.PlayerScreenState

// 1. Управление плеером через Domain; сам MediaPlayer остаётся в Data
class AudioPlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    track: Track
) : ViewModel() {

    // 2. Activity получает только LiveData
    private val mutableState = MutableLiveData(PlayerScreenState(track))
    val state: LiveData<PlayerScreenState> = mutableState

    private val progressHandler = Handler(Looper.getMainLooper())
    private var isScreenResumed = false
    private var isCleared = false

    // 3. Обновление фактической позиции воспроизведения
    private val progressRunnable = Runnable {
        if (!isCleared && isScreenResumed) {
            publishSnapshot(playerInteractor.snapshot())
        }
    }

    // 4. Подготовка плеера один раз за жизнь ViewModel
    init {
        playerInteractor.setListener { snapshot ->
            publishSnapshot(snapshot)
        }
        playerInteractor.prepare(track)
    }

    // 5. Обработка кнопки Play/Pause
    fun onPlaybackClicked() {
        if (isScreenResumed) {
            playerInteractor.togglePlayback()
        }
    }

    // 6. Возобновление экрана
    fun onScreenResumed() {
        isScreenResumed = true
        publishSnapshot(playerInteractor.snapshot())
    }

    // 6.1. Пауза при уходе с экрана
    fun onScreenPaused() {
        isScreenResumed = false
        progressHandler.removeCallbacks(progressRunnable)
        playerInteractor.pause()
        publishSnapshot(playerInteractor.snapshot())
    }

    // 7. Обновление состояния и таймера
    private fun publishSnapshot(snapshot: PlayerSnapshot) {
        if (isCleared) return

        progressHandler.removeCallbacks(progressRunnable)

        mutableState.value = requireNotNull(mutableState.value).copy(
            playback = snapshot
        )

        if (snapshot.state == PlayerState.PLAYING && isScreenResumed) {
            progressHandler.postDelayed(
                progressRunnable,
                PROGRESS_UPDATE_DELAY
            )
        }
    }

    // 8. Освобождение ресурсов
    override fun onCleared() {
        isCleared = true
        progressHandler.removeCallbacks(progressRunnable)
        playerInteractor.setListener(null)
        playerInteractor.release()
        super.onCleared()
    }

    companion object {
        private const val PROGRESS_UPDATE_DELAY = 300L
    }
}