package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity()
{
    private var searchText = ""
    private var lastSearchQuery = ""
    private var currentSearchCall: Call<TrackSearchResponse>? = null
    private val trackAdapter = TrackAdapter(ArrayList())
    private lateinit var tracksRecyclerView: RecyclerView

    private lateinit var errorState: View
    private lateinit var emptyState: View

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<View>(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        tracksRecyclerView = findViewById(R.id.tracksRecyclerView)
        emptyState = findViewById(R.id.emptyState)

        errorState = findViewById(R.id.errorState)
        val retryButton = findViewById<View>(R.id.retryButton)
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)

        tracksRecyclerView.adapter = trackAdapter

        setupSearchKeyboard(searchEditText)
        retryButton.setOnClickListener {

            if (lastSearchQuery.isNotEmpty()) {
                searchTracks(lastSearchQuery)
            }
        }
        clearButton.isVisible = false
        searchEditText.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()
            clearButton.isVisible = !text.isNullOrEmpty()
        }

        clearButton.setOnClickListener {
            clearSearch(searchEditText)
        }

        val searchRoot = findViewById<View>(R.id.searchRoot)

        ViewCompat.setOnApplyWindowInsetsListener(searchRoot)
        { view, windowInsets ->

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

    private fun clearSearch(searchEditText: EditText) {
        searchEditText.setText("")
        currentSearchCall?.cancel()
        currentSearchCall = null
        lastSearchQuery = ""
        trackAdapter.updateTracks(emptyList())
        tracksRecyclerView.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = false
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            searchEditText.windowToken,
            0
        )
        searchEditText.clearFocus()
    }

    private fun setupSearchKeyboard(searchEditText: EditText) {

        searchEditText.setOnEditorActionListener { _, actionId, event ->

            val isDone = actionId == EditorInfo.IME_ACTION_DONE

            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN

            if (isDone || isEnter) {

                val query = searchEditText.text.toString().trim()

                if (query.isNotEmpty()) {
                    searchTracks(query)
                }

                true
            } else {
                false
            }
        }
    }

    private fun searchTracks(query: String) {
        currentSearchCall?.cancel()
        clearSearchResults()
        lastSearchQuery = query
        val newCall = ITunesNetworkClient.apiService.searchTracks(query)
        currentSearchCall = newCall
        newCall.enqueue(object : Callback<TrackSearchResponse> {

                override fun onResponse(
                    call: Call<TrackSearchResponse>,
                    response: Response<TrackSearchResponse>
                ) {
                    if (call !== currentSearchCall || call.isCanceled) {
                        return
                    }

                    if (response.isSuccessful && response.body() != null) {
                        val tracks = response.body()?.results.orEmpty()
                        showSearchResults(tracks)

                    } else {
                        showSearchError()
                    }
                }

                override fun onFailure(
                call: Call<TrackSearchResponse>,
                t: Throwable
                ) {
                    if (call !== currentSearchCall || call.isCanceled) {
                        return
                    }
                    showSearchError()
                }
            })
    }

    private fun clearSearchResults() {
        trackAdapter.updateTracks(emptyList())
        tracksRecyclerView.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = false
    }

    private fun showSearchResults(tracks: List<Track>) {
        trackAdapter.updateTracks(tracks)
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
        tracksRecyclerView.isVisible = false
        emptyState.isVisible = false
        errorState.isVisible = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
        outState.putString(LAST_SEARCH_QUERY, lastSearchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchText = savedInstanceState.getString(SEARCH_TEXT, "")
        lastSearchQuery = savedInstanceState.getString(LAST_SEARCH_QUERY, "")

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText(searchText)
    }

    override fun onDestroy() {
        currentSearchCall?.cancel()
        currentSearchCall = null
        super.onDestroy()
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val LAST_SEARCH_QUERY = "LAST_SEARCH_QUERY"
    }
}