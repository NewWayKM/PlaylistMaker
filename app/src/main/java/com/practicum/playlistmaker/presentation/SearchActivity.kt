package com.practicum.playlistmaker.presentation

import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.adapter.TrackAdapter
import com.practicum.playlistmaker.presentation.state.SearchContent
import com.practicum.playlistmaker.presentation.state.SearchScreenState
import com.practicum.playlistmaker.presentation.viewmodel.SearchViewModel
import com.practicum.playlistmaker.presentation.viewmodel.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {
    // 1. ViewModel экрана поиска
    private lateinit var viewModel: SearchViewModel

    // 1.1. Защита от повторной обработки программного изменения текста
    private var isRenderingQuery = false

    // 2. Адаптеры результатов поиска и истории
    private val trackAdapter = TrackAdapter(ArrayList()) { track ->
        viewModel.onTrackClicked(track)
    }
    private val historyAdapter = TrackAdapter(ArrayList()) { track ->
        viewModel.onTrackClicked(track)
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

        // 4.1. Создание ViewModel через ViewModelProvider
        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(savedInstanceState?.getString(SEARCH_TEXT).orEmpty())
        )[SearchViewModel::class.java]

        initViews()
        setupRecyclerViews()
        setupClickListeners()
        setupSearchField()
        setupSearchKeyboard()
        setupWindowInsets()

        searchEditText.requestFocus()

        // 4.2. Подписка на состояние экрана
        viewModel.state.observe(this) { state ->
            render(state)

        }
    }

    // 5. Инициализация интерфейса
    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)

        // 5.1. Восстановление текста выполняется через ViewModel
        searchEditText.isSaveEnabled = false

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
    // 6. (---)
    // 7. Настройка списков

    private fun setupRecyclerViews() {
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackAdapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
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
            viewModel.onRetryClicked()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }
    }

    // 9. Настройка поля поиска

    private fun setupSearchField() {
        clearButton.isVisible = searchEditText.text.isNotEmpty()

        // 9.1. Передача изменения текста во ViewModel
        searchEditText.doOnTextChanged { text, _, _, _ ->
            if (!isRenderingQuery) {
                viewModel.onQueryChanged(text?.toString().orEmpty())
            }
        }

        // 9.2. Передача изменения фокуса во ViewModel
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onInputFocusChanged(hasFocus)
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

    // 11. Работа с историей перенесена во ViewModel

    // 12. Очистка поискового запроса
    private fun clearSearch() {
        // 12.1. Очистка поискового запроса через ViewModel
        viewModel.onClearClicked()

        // 12.2. Скрытие клавиатуры остаётся в Activity
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            searchEditText.windowToken,
            0
        )
    }

    // 13. Настройка клавиатуры
    private fun setupSearchKeyboard() {
        searchEditText.setOnEditorActionListener { _, actionId, event ->
            val isDone = actionId == EditorInfo.IME_ACTION_DONE
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN

            if (isDone || isEnter) {
                // 13.1. Немедленный поиск через ViewModel
                viewModel.onSearchSubmitted()
                true
            } else {
                false
            }
        }
    }

    // 14. Отображение состояния поиска из LiveData
    private fun render(state: SearchScreenState) {

        // 14.1. Восстановление текста без повторного поиска
        if (searchEditText.text.toString() != state.query) {
            isRenderingQuery = true
            searchEditText.setText(state.query)
            searchEditText.setSelection(state.query.length)
            isRenderingQuery = false
        }

        // 14.2. Управление видимостью элементов
        clearButton.isVisible = state.query.isNotEmpty()
        loadingContainer.isVisible = state.content == SearchContent.Loading
        emptyState.isVisible = state.content == SearchContent.Empty
        errorState.isVisible = state.content == SearchContent.Error

        tracksRecyclerView.isVisible =
            state.content is SearchContent.Tracks

        historyContainer.isVisible = state.showHistory

        // 14.3. Обновление списка результатов
        trackAdapter.updateTracks(
            (state.content as? SearchContent.Tracks)?.tracks.orEmpty()
        )

        // 14.4. Обновление истории
        historyAdapter.updateTracks(state.history)

        // 14.5. Переход в аудиоплеер
        state.pendingTrack?.let { track ->
            viewModel.onPlayerNavigationHandled()

            val intent = AudioPlayerActivity.Companion.createIntent(this, track)
            startActivity(intent)
        }
    }

    // 15. Отображение результатов и истории выполняется в render()

    // 16. Сохранение состояния экрана
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(
            SEARCH_TEXT,
            searchEditText.text.toString()
        )
        super.onSaveInstanceState(outState)
    }

    // 16.1. Возобновление работы экрана
    override fun onResume() {
        super.onResume()
        viewModel.onScreenResumed()
    }

    // 16.2. Приостановка работы экрана
    override fun onPause() {
        viewModel.onScreenPaused(isFinishing)
        super.onPause()
    }

    // 17. Освобождение ресурсов выполняется в SearchViewModel.onCleared()

    // 18. Константы
    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}