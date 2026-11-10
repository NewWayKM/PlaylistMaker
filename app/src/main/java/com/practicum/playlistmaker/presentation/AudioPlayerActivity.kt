package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.model.TrackParcel
import com.practicum.playlistmaker.presentation.state.PlayerScreenState
import com.practicum.playlistmaker.presentation.util.TrackCoverFormatter
import com.practicum.playlistmaker.presentation.util.TrackDurationFormatter
import com.practicum.playlistmaker.presentation.util.TrackReleaseYearFormatter
import com.practicum.playlistmaker.presentation.viewmodel.AudioPlayerViewModel
import com.practicum.playlistmaker.presentation.viewmodel.AudioPlayerViewModelFactory

class AudioPlayerActivity : AppCompatActivity() {

    // 1. Данные выбранного трека
    private var currentTrack: Track? = null

    // 1.1. Управление плеером через ViewModel
    private lateinit var viewModel: AudioPlayerViewModel
    private lateinit var playButton: ImageButton

    // 1.2. Отображение текущего времени воспроизведения
    private lateinit var progressTime: TextView

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

        // 2.2.1. Создание ViewModel через ViewModelProvider
        viewModel = ViewModelProvider(
            this,
            AudioPlayerViewModelFactory(requireNotNull(currentTrack))
        )[AudioPlayerViewModel::class.java]

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

        // 2.5. Подписка на состояние плеера
        viewModel.state.observe(this) { state ->
            render(state)
        }
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


    // 5.8. Отображение состояния аудиоплеера из LiveData
    private fun render(state: PlayerScreenState) {

        val playback = state.playback

        // 5.8.1. Текущее состояние воспроизведения
        val playerState = playback.state

        // 5.8.2. Доступность кнопки воспроизведения
        playButton.isEnabled = when (playerState) {
            PlayerState.PREPARED,
            PlayerState.PLAYING,
            PlayerState.PAUSED,
            PlayerState.COMPLETED -> true

            else -> false
        }

        // 5.8.3. Обновление изображения Play/Pause
        updatePlayButton(playerState == PlayerState.PLAYING)

        // 5.8.4. Обновление времени воспроизведения
        progressTime.text = TrackDurationFormatter.format(
            when (playerState) {
                PlayerState.IDLE,
                PlayerState.COMPLETED,
                PlayerState.ERROR,
                PlayerState.RELEASED -> 0L

                else -> playback.positionMillis
            }
        )
    }

    // 5.9. Настройка кнопки воспроизведения
    private fun setupPlayButton() {
        playButton = findViewById(R.id.playerPlayButton)
        playButton.isEnabled = false

        playButton.setOnClickListener {
            viewModel.onPlaybackClicked()
        }

        updatePlayButton(false)
    }

    // 5.10. (---)
    // 5.11. Обновление изображения кнопки
    private fun updatePlayButton(isPlaying: Boolean) {

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

    // 5.13. (---)
    // 5.14. (---)
    // 5.15. (---)
    // 5.16. Приостановка при уходе приложения в фон
    override fun onPause() {
        if (::viewModel.isInitialized) {
            viewModel.onScreenPaused()
        }
        super.onPause()
    }

    // 5.17. Возобновление работы аудиоплеера
    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized) {
            viewModel.onScreenResumed()
        }
    }
// Освобождение ресурсов выполняется в AudioPlayerViewModel.onCleared()

    // 6. Создание Intent для открытия аудиоплеера
    companion object {

        private const val EXTRA_TRACK = "extra_track"

        fun createIntent(context: Context, track: Track): Intent {
            return Intent(context, AudioPlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK, TrackParcel.Companion.fromDomain(track))
            }
        }
    }
}