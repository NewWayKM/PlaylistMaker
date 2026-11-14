package com.practicum.playlistmaker.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.api.Cancellable
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.state.SearchContent
import com.practicum.playlistmaker.presentation.state.SearchScreenState

// 1. Состояние и сценарии поиска
class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor,
    initialQuery: String = ""
) : ViewModel() {

    // 2. Состояние экрана
    private val mutableState = MutableLiveData(
        SearchScreenState(
            query = initialQuery,
            history = historyInteractor.read().toList()
        ).let { initialState ->
            initialState.copy(
                showHistory = initialState.query.isEmpty() &&
                        initialState.isInputFocused &&
                        initialState.content == SearchContent.Idle &&
                        initialState.history.isNotEmpty()
            )
        }
    )

    val state: LiveData<SearchScreenState> = mutableState

    private val currentState: SearchScreenState
        get() = requireNotNull(mutableState.value)

    // 2.1 Вычисление признака отображения истории во ViewModel
    private fun updateState(newState: SearchScreenState) {
        val showHistory = newState.query.isEmpty() &&
                newState.isInputFocused &&
                newState.content == SearchContent.Idle &&
                newState.history.isNotEmpty()

        mutableState.value = newState.copy(showHistory = showHistory)
    }

    // 3. Управление отложенным поиском
    private val handler = Handler(Looper.getMainLooper())
    private var currentSearchTask: Cancellable? = null
    private var searchGeneration = 0L
    private var lastSearchQuery = ""
    private var isSearchPending = false
    private var resumePendingSearch = initialQuery.isNotBlank()
    private var isScreenResumed = false
    private var isCleared = false

    // 3.1. Защита от повторного открытия плеера
    private var lastTrackClickTime: Long? = null
    private var isPlayerOpening = false

    // 3.2. Отложенный запуск поиска
    private val searchRunnable = Runnable {
        isSearchPending = false

        if (!isScreenResumed) {
            resumePendingSearch = currentState.query.isNotBlank()
        } else {
            performSearch(currentState.query.trim())
        }
    }

    // 4. Обработка изменения поискового запроса
    fun onQueryChanged(query: String) {
        if (query == currentState.query) return

        cancelPendingSearch()
        resumePendingSearch = false
        cancelCurrentSearch()

        if (query.isEmpty()) {
            lastSearchQuery = ""
        }

        updateState(
            currentState.copy(
                query = query,
                content = SearchContent.Idle
            )
        )

        if (query.isNotBlank()) {
            scheduleSearch()
        }
    }

    // 4.1. Очистка поисковой строки
    fun onClearClicked() {
        cancelPendingSearch()
        resumePendingSearch = false
        cancelCurrentSearch()
        lastSearchQuery = ""

        updateState(
            currentState.copy(
                query = "",
                content = SearchContent.Idle
            )
        )
    }

    // 4.2. Изменение фокуса поля ввода
    fun onInputFocusChanged(hasFocus: Boolean) {
        if (currentState.isInputFocused != hasFocus) {
            updateState(
                currentState.copy(
                    isInputFocused = hasFocus
                )
            )
        }
    }

    // 5. Поиск по нажатию Enter или Done
    fun onSearchSubmitted() {
        val query = currentState.query.trim()

        if (query.isNotEmpty()) {
            performSearch(query)
        }
    }

    // 5.1. Повтор поиска после ошибки
    fun onRetryClicked() {
        if (lastSearchQuery.isNotEmpty()) {
            performSearch(lastSearchQuery)
        }
    }

    // 6. Очистка истории поиска
    fun onClearHistoryClicked() {
        historyInteractor.clear()

        updateState(
            currentState.copy(
                history = emptyList()
            )
        )
    }

    // 6.1. Выбор трека
    fun onTrackClicked(track: Track) {
        val now = SystemClock.elapsedRealtime()
        val previousClick = lastTrackClickTime

        if (
            isPlayerOpening ||
            (previousClick != null &&
                    now - previousClick < TRACK_CLICK_DEBOUNCE_DELAY)
        ) {
            return
        }

        lastTrackClickTime = now
        isPlayerOpening = true

        historyInteractor.add(track)

        updateState(
            currentState.copy(
                history = historyInteractor.read().toList(),
                pendingTrack = track
            )
        )
    }

    // 6.2. Подтверждение перехода в плеер
    fun onPlayerNavigationHandled() {
        if (currentState.pendingTrack != null) {
            updateState(
                currentState.copy(
                    pendingTrack = null
                )
            )
        }
    }

    // 7. Возвращение на экран
    fun onScreenResumed() {
        isScreenResumed = true
        isPlayerOpening = false

        if (resumePendingSearch) {
            resumePendingSearch = false
            scheduleSearch()
        }
    }

    // 7.1. Уход с экрана
    fun onScreenPaused(isFinishing: Boolean) {
        isScreenResumed = false

        resumePendingSearch =
            !isFinishing && currentState.query.isNotBlank() &&
                    (resumePendingSearch ||
                            isSearchPending ||
                            currentSearchTask != null)

        cancelPendingSearch()
        cancelCurrentSearch()

        if (currentState.content == SearchContent.Loading) {
            updateState(
                currentState.copy(
                    content = SearchContent.Idle
                )
            )
        }
    }

    // 8. Запуск поиска с задержкой
    private fun scheduleSearch() {
        cancelPendingSearch()

        if (currentState.query.isBlank()) return

        if (!isScreenResumed) {
            resumePendingSearch = true
            return
        }

        isSearchPending = true

        handler.postDelayed(
            searchRunnable,
            SEARCH_DEBOUNCE_DELAY
        )
    }

    // 9. Выполнение поискового запроса через Domain
    private fun performSearch(query: String) {
        cancelPendingSearch()
        resumePendingSearch = false
        cancelCurrentSearch()

        if (query.isBlank()) return

        if (!isScreenResumed) {
            resumePendingSearch = true
            return
        }

        lastSearchQuery = query.trim()

        updateState(
            currentState.copy(
                content = SearchContent.Loading
            )
        )

        val generation = searchGeneration
        var deliveredSynchronously = false

        val task = tracksInteractor.search(lastSearchQuery) { result ->
            deliveredSynchronously = true

            if (isCleared || generation != searchGeneration) {
                return@search
            }

            currentSearchTask = null

            val content = when (result) {
                is SearchResult.Success -> {
                    if (result.tracks.isEmpty()) {
                        SearchContent.Empty
                    } else {
                        SearchContent.Tracks(result.tracks.toList())
                    }
                }

                SearchResult.Error -> SearchContent.Error
            }

            updateState(
                currentState.copy(
                    content = content
                )
            )
        }

        // 9.1. Защита от синхронного ответа
        if (generation != searchGeneration || isCleared) {
            task.cancel()
        } else if (!deliveredSynchronously) {
            currentSearchTask = task
        }
    }

    // 10. Отмена отложенного поиска
    private fun cancelPendingSearch() {
        handler.removeCallbacks(searchRunnable)
        isSearchPending = false
    }

    // 10.1. Отмена текущего запроса
    private fun cancelCurrentSearch() {
        searchGeneration++
        currentSearchTask?.cancel()
        currentSearchTask = null
    }

    // 11. Освобождение ресурсов ViewModel
    override fun onCleared() {
        isCleared = true

        cancelPendingSearch()
        cancelCurrentSearch()

        super.onCleared()
    }

    // 12. Константы из спринта 15
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val TRACK_CLICK_DEBOUNCE_DELAY = 1000L
    }
}