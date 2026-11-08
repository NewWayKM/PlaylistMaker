package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.Cancellable
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.adapter.TrackAdapter

class SearchActivity : AppCompatActivity() {
    // 1. Состояние поиска
    private var searchText = ""
    private var lastSearchQuery = ""
    private var currentSearchTask: Cancellable? = null
    private var searchGeneration = 0L

    // 1.1. Отложенный поиск
    private val handler = Handler(Looper.getMainLooper())
    private var isSearchPending = false
    private var resumePendingSearch = false
    private var isScreenResumed = false

    // 1.2. Защита от повторного открытия аудиоплеера
    private var lastTrackClickTime = 0L
    private var isPlayerOpening = false
    private val searchRunnable = Runnable {
        isSearchPending = false

        if (!isScreenResumed) {
            resumePendingSearch = true
        } else {
            val query = searchEditText.text.toString().trim()

            if (query.isNotEmpty()) {
                searchTracks(query)
            }
        }
    }

    // 2. История и адаптеры
    // 2.1. Интеракторы новой архитектуры
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var historyInteractor: HistoryInteractor
    private val trackAdapter = TrackAdapter(ArrayList()) { track ->
        onTrackClicked(track)
    }
    private val historyAdapter = TrackAdapter(ArrayList()) { track ->
        onTrackClicked(track)
    }

    // 3. Элементы интерфейса
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var errorState: View
    private lateinit var emptyState: View
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyContainer: View
    private lateinit var backButton: View
    private lateinit var retryButton: View
    private lateinit var clearHistoryButton: View
    private lateinit var loadingContainer: View

    // 4. Создание экрана
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initInteractors()
        setupRecyclerViews()
        setupClickListeners()
        setupSearchField()
        setupSearchKeyboard()
        setupWindowInsets()

        searchEditText.requestFocus()
        updateHistoryVisibility()
    }

// 5. Инициализация интерфейса

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)

        tracksRecyclerView = findViewById(R.id.tracksRecyclerView)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        historyContainer = findViewById(R.id.historyContainer)
        emptyState = findViewById(R.id.emptyState)
        errorState = findViewById(R.id.errorState)

        backButton = findViewById(R.id.backButton)
        retryButton = findViewById(R.id.retryButton)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        loadingContainer = findViewById(R.id.loadingContainer)
    }

    // 5.1. Получение интеракторов через Creator
    private fun initInteractors() {
        tracksInteractor = Creator.provideTracksInteractor()
        historyInteractor = Creator.provideHistoryInteractor()
    }

    // 6. Инициализация истории

    // 7. Настройка списков

    private fun setupRecyclerViews() {
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackAdapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        historyAdapter.updateTracks(historyInteractor.read())
    }

    // 8. Обработка нажатий

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            clearSearch()
        }

        retryButton.setOnClickListener {
            if (lastSearchQuery.isNotEmpty()) {
                searchTracks(lastSearchQuery)
            }
        }

        clearHistoryButton.setOnClickListener {
            clearHistory()
        }
    }

    // 9. Настройка поля поиска

    private fun setupSearchField() {
        clearButton.isVisible = searchEditText.text.isNotEmpty()

        searchEditText.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()
            clearButton.isVisible = !text.isNullOrEmpty()

            // 9.1. Отменяем предыдущий отложенный поиск
            handler.removeCallbacks(searchRunnable)
            isSearchPending = false
            resumePendingSearch = false

            // 9.2. Отменяем устаревший сетевой запросам
            cancelCurrentSearch()

            // 9.3. Скрываем результаты предыдущего запроса
            clearSearchResults()

            // 9.4. Запускаем отложенный поиск при непустом запросе
            if (searchText.isNotBlank()) {
                searchDebounce()
            }

            updateHistoryVisibility()
        }

        searchEditText.setOnFocusChangeListener { _, _ ->
            updateHistoryVisibility()
        }
    }

    // 9.1. Поиск через две секунды после последнего изменения текста
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)

        val hasQuery = searchEditText.text.toString().isNotBlank()

        if (!isScreenResumed) {
            isSearchPending = false
            resumePendingSearch = hasQuery
            return
        }

        isSearchPending = hasQuery

        if (isSearchPending) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    // 10. Системные отступы

    private fun setupWindowInsets() {
        val searchRoot = findViewById<View>(R.id.searchRoot)

        ViewCompat.setOnApplyWindowInsetsListener(searchRoot) { view, windowInsets ->
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

    // 11. Работа с историей

    private fun onTrackClicked(track: Track) {
        val currentTime = SystemClock.elapsedRealtime()

        // 11.1. Блокируем повторное нажатие
        if (isPlayerOpening ||
            currentTime - lastTrackClickTime < TRACK_CLICK_DEBOUNCE_DELAY
        ) {
            return
        }

        lastTrackClickTime = currentTime
        isPlayerOpening = true

        // 11.2. Сохраняем выбранный трек в историю
        historyInteractor.add(track)
        historyAdapter.updateTracks(historyInteractor.read())

        // 11.3. Открываем аудиоплеер
        val intent = AudioPlayerActivity.Companion.createIntent(this, track)
        startActivity(intent)
    }

    private fun clearHistory() {
        historyInteractor.clear()
        historyAdapter.updateTracks(emptyList())
        updateHistoryVisibility()
    }

    private fun updateHistoryVisibility() {
        historyContainer.isVisible =
            searchEditText.hasFocus() &&
                    searchEditText.text.isEmpty() &&
                    historyAdapter.itemCount > 0
    }


    // 12. Очистка поискового запроса
    private fun clearSearch() {
        searchEditText.setText("")
        cancelCurrentSearch()
        lastSearchQuery = ""

        clearSearchResults()

        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            searchEditText.windowToken,
            0
        )
        updateHistoryVisibility()
    }

    // 13. Настройка клавиатуры
    private fun setupSearchKeyboard() {
        searchEditText.setOnEditorActionListener { _, actionId, event ->
            val isDone = actionId == EditorInfo.IME_ACTION_DONE
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN

            if (isDone || isEnter) {
                val query = searchEditText.text.toString().trim()

                if (query.isNotEmpty()) {
                    handler.removeCallbacks(searchRunnable)
                    isSearchPending = false
                    resumePendingSearch = false
                    searchTracks(query)
                }

                true
            } else {
                false
            }
        }
    }

    // 14. Выполнение поискового запроса (интерфейс)
    private fun searchTracks(query: String) {
        handler.removeCallbacks(searchRunnable)
        isSearchPending = false
        resumePendingSearch = false

        cancelCurrentSearch()
        clearSearchResults()

        if (query.isBlank()) {
            updateHistoryVisibility()
            return
        }

        loadingContainer.isVisible = true
        lastSearchQuery = query.trim()

        val generation = searchGeneration

        currentSearchTask = tracksInteractor.search(lastSearchQuery) { result ->

            // 14.1. Защита от устаревшего ответа
            if (generation != searchGeneration) {
                return@search
            }

            currentSearchTask = null
            loadingContainer.isVisible = false

            when (result) {
                is SearchResult.Success -> showSearchResults(result.tracks)
                SearchResult.Error -> showSearchError()
            }
        }
    }

    // 14.2. Отмена текущего поискового запроса
    private fun cancelCurrentSearch() {
        searchGeneration++
        currentSearchTask?.cancel()
        currentSearchTask = null
    }

    // 15. Управление результатами поиска
    private fun clearSearchResults() {
        loadingContainer.isVisible = false
        trackAdapter.updateTracks(emptyList())
        tracksRecyclerView.isVisible = false
        historyContainer.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = false
    }

    private fun showSearchResults(tracks: List<Track>) {
        trackAdapter.updateTracks(tracks)
        historyContainer.isVisible = false
        errorState.isVisible = false
        if (tracks.isEmpty()) {
            tracksRecyclerView.isVisible = false
            emptyState.isVisible = true

        } else {
            tracksRecyclerView.isVisible = true
            emptyState.isVisible = false
        }
    }

    private fun showSearchError() {
        trackAdapter.updateTracks(emptyList())
        historyContainer.isVisible = false
        tracksRecyclerView.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = true
    }

    // 16. Сохранение состояния экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
        outState.putString(LAST_SEARCH_QUERY, lastSearchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchText = savedInstanceState.getString(SEARCH_TEXT, "")
        lastSearchQuery = savedInstanceState.getString(LAST_SEARCH_QUERY, "")

        searchEditText.setText(searchText)
    }

    // 16.1. Возобновление работы экрана
    override fun onResume() {
        super.onResume()
        isScreenResumed = true
        isPlayerOpening = false

        if (resumePendingSearch) {
            resumePendingSearch = false
            searchDebounce()
        }
    }

    // 16.2. Приостановка работы экрана
    override fun onPause() {
        isScreenResumed = false

        resumePendingSearch = !isFinishing &&
                searchText.isNotBlank() &&
                (resumePendingSearch || isSearchPending || currentSearchTask != null)

        handler.removeCallbacks(searchRunnable)
        isSearchPending = false

        cancelCurrentSearch()
        loadingContainer.isVisible = false

        super.onPause()
    }

    // 17. Завершение работы экрана
    override fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
        cancelCurrentSearch()
        super.onDestroy()
    }

    // 18. Константы
    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val LAST_SEARCH_QUERY = "LAST_SEARCH_QUERY"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val TRACK_CLICK_DEBOUNCE_DELAY = 1000L

    }
}