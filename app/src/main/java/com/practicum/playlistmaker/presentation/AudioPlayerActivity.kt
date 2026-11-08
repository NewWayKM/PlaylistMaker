package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.models.PlayerSnapshot
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.model.TrackParcel
import com.practicum.playlistmaker.presentation.util.TrackCoverFormatter
import com.practicum.playlistmaker.presentation.util.TrackDurationFormatter
import com.practicum.playlistmaker.presentation.util.TrackReleaseYearFormatter

class AudioPlayerActivity : AppCompatActivity() {

    // 1. Данные выбранного трека
    private var currentTrack: Track? = null

    // 1.1. Управление плеером через Domain
    private lateinit var playerInteractor: PlayerInteractor
    private lateinit var playButton: ImageButton

    // 1.2. Отображение текущего времени воспроизведения
    private lateinit var progressTime: TextView
    private val progressHandler = Handler(Looper.getMainLooper())

    private val progressRunnable = object : Runnable {
        override fun run() {
            val snapshot = playerInteractor.snapshot()

            if (snapshot.state == PlayerState.PLAYING) {
                progressTime.text =
                    TrackDurationFormatter.format(snapshot.positionMillis)

                progressHandler.postDelayed(this, PROGRESS_UPDATE_DELAY)
            }
        }
    }

    // 2. Создание экрана
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2.1. Получение выбранного трека
        currentTrack = getTrackFromIntent()

        if (currentTrack == null) {
            finish()
            return
        }

        // 2.2. Подключение интерфейса
        setContentView(R.layout.activity_audio_player)

        // 2.2.1. Создание интерактора аудиоплеера
        playerInteractor = Creator.createPlayerInteractor()

        // 2.3. Настройка экрана
        setupSystemBars()
        setupBackButton()

        // 2.4. Отображение данных трека
        showTrackCover()
        showTrackInfo()
        showTrackDuration()
        showTrackAlbum()
        showTrackReleaseYear()
        showTrackGenre()
        showTrackCountry()
        setupProgressTime()
        setupPlayButton()
        preparePlayer()
    }

    // 3. Получение данных выбранного трека
    private fun getTrackFromIntent(): Track? {
        return IntentCompat.getParcelableExtra(
            intent,
            EXTRA_TRACK,
            TrackParcel::class.java
        )?.toDomain()
    }

    // 4. Настройка интерфейса
    // 4.1. Учёт системных панелей Android
    private fun setupSystemBars() {
        val audioPlayerRoot = findViewById<View>(R.id.audioPlayerRoot)

        ViewCompat.setOnApplyWindowInsetsListener(audioPlayerRoot) { view, windowInsets ->
            val systemBarsInsets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                view.paddingLeft,
                systemBarsInsets.top,
                view.paddingRight,
                systemBarsInsets.bottom
            )

            windowInsets
        }
    }

    // 4.2. Обработка кнопки «Назад»
    private fun setupBackButton() {
        val backButton = findViewById<View>(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
    }

    // 5. Отображение данных трека
    // 5.1. Загрузка обложки
    private fun showTrackCover() {
        val playerCover = findViewById<ImageView>(R.id.playerCover)

        Glide.with(this)
            .load(TrackCoverFormatter.format(currentTrack?.artworkUrl100))
            .placeholder(R.drawable.ic_track_placeholder)
            .error(R.drawable.ic_track_placeholder)
            .fallback(R.drawable.ic_track_placeholder)
            .centerCrop()
            .into(playerCover)
    }

    // 5.2. Название трека и исполнитель
    private fun showTrackInfo() {
        val playerTrackName = findViewById<TextView>(R.id.playerTrackName)
        val playerArtistName = findViewById<TextView>(R.id.playerArtistName)

        playerTrackName.text = currentTrack?.trackName
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.track_unknown_name)

        playerArtistName.text = currentTrack?.artistName
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.track_unknown_artist)
    }

    // 5.3. Продолжительность трека
    private fun showTrackDuration() {
        val playerDurationValue = findViewById<TextView>(R.id.playerDurationValue)

        playerDurationValue.text =
            TrackDurationFormatter.format(currentTrack?.trackTimeMillis)
    }

    // 5.4. Альбом
    private fun showTrackAlbum() {
        val playerAlbumValue = findViewById<TextView>(R.id.playerAlbumValue)
        val playerAlbumGroup = findViewById<Group>(R.id.playerAlbumGroup)

        val albumName = currentTrack?.collectionName
            ?.takeIf { it.isNotBlank() }

        if (albumName != null) {
            playerAlbumValue.text = albumName
            playerAlbumGroup.visibility = View.VISIBLE
        } else {
            playerAlbumGroup.visibility = View.GONE
        }
    }

    // 5.5. Год релиза
    private fun showTrackReleaseYear() {
        val playerYearValue = findViewById<TextView>(R.id.playerYearValue)
        val playerYearGroup = findViewById<Group>(R.id.playerYearGroup)

        val releaseYear = TrackReleaseYearFormatter.format(
            currentTrack?.releaseDate
        )

        if (releaseYear != null) {
            playerYearValue.text = releaseYear
            playerYearGroup.visibility = View.VISIBLE
        } else {
            playerYearGroup.visibility = View.GONE
        }
    }

    // 5.6. Жанр
    private fun showTrackGenre() {
        val playerGenreValue = findViewById<TextView>(R.id.playerGenreValue)

        playerGenreValue.text = currentTrack?.primaryGenreName
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.player_unknown_value)
    }

    // 5.7. Страна
    private fun showTrackCountry() {
        val playerCountryValue = findViewById<TextView>(R.id.playerCountryValue)

        playerCountryValue.text = currentTrack?.country
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.player_unknown_value)
    }


    // 5.8. Подготовка аудиоплеера
    private fun preparePlayer() {
        // 5.8.1. Подписка на изменения состояния
        playerInteractor.setListener { snapshot ->
            updatePlayerState(snapshot)
        }

        // 5.8.2. Передача выбранного трека
        currentTrack?.let { track ->
            playerInteractor.prepare(track)
        }
    }

    // 5.8.3. Обновление интерфейса по состоянию плеера
    private fun updatePlayerState(snapshot: PlayerSnapshot) {

        val state = snapshot.state

        // 5.8.4. Доступность кнопки воспроизведения
        playButton.isEnabled = when (state) {
            PlayerState.PREPARED,
            PlayerState.PLAYING,
            PlayerState.PAUSED,
            PlayerState.COMPLETED -> true

            else -> false
        }

        // 5.8.5. Обновление изображения Play/Pause
        updatePlayButton()

        // 5.8.6. Обновление времени
        when (state) {
            PlayerState.PLAYING -> startProgressUpdate()

            PlayerState.PAUSED -> {
                stopProgressUpdate()
                progressTime.text =
                    TrackDurationFormatter.format(snapshot.positionMillis)
            }

            PlayerState.COMPLETED,
            PlayerState.IDLE,
            PlayerState.ERROR,
            PlayerState.RELEASED -> resetProgressTime()

            else -> stopProgressUpdate()
        }
    }


    // 5.9. Настройка кнопки воспроизведения
    private fun setupPlayButton() {
        playButton = findViewById(R.id.playerPlayButton)
        playButton.isEnabled = false

        playButton.setOnClickListener {
            togglePlayback()
        }

        updatePlayButton()
    }

    // 5.10. Переключение воспроизведения и паузы
    private fun togglePlayback() {
        playerInteractor.togglePlayback()
    }

    // 5.11. Обновление изображения кнопки
    private fun updatePlayButton() {

        val isPlaying =
            playerInteractor.snapshot().state == PlayerState.PLAYING

        playButton.setImageResource(
            if (isPlaying) {
                R.drawable.ic_player_pause
            } else {
                R.drawable.ic_player_play
            }
        )

        playButton.contentDescription = getString(
            if (isPlaying) {
                R.string.player_pause
            } else {
                R.string.player_play
            }
        )
    }

    // 5.12. Настройка отображения прогресса
    private fun setupProgressTime() {
        progressTime = findViewById(R.id.playerProgressTime)
        progressTime.text = TrackDurationFormatter.format(0L)
    }

    // 5.13. Запуск обновления времени
    private fun startProgressUpdate() {
        progressHandler.removeCallbacks(progressRunnable)
        progressRunnable.run()
    }

    // 5.14. Остановка обновления времени
    private fun stopProgressUpdate() {
        progressHandler.removeCallbacks(progressRunnable)
    }

    // 5.15. Сброс времени воспроизведения
    private fun resetProgressTime() {
        stopProgressUpdate()
        progressTime.text = TrackDurationFormatter.format(0L)
    }

    // 5.16. Приостановка при уходе приложения в фон
    override fun onPause() {
        playerInteractor.pause()
        stopProgressUpdate()
        super.onPause()
    }

    // 5.17. Освобождение ресурсов аудиоплеера
    override fun onDestroy() {
        stopProgressUpdate()

        playerInteractor.setListener(null)
        playerInteractor.release()

        super.onDestroy()
    }

    // 6. Создание Intent для открытия аудиоплеера
    companion object {

        private const val EXTRA_TRACK = "extra_track"
        private const val PROGRESS_UPDATE_DELAY = 300L

        fun createIntent(context: Context, track: Track): Intent {
            return Intent(context, AudioPlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK, TrackParcel.Companion.fromDomain(track))
            }
        }
    }
}