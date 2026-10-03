package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity()
{

    private var searchText = ""

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

        clearButton.isVisible = false
        searchEditText.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()
            clearButton.isVisible = !text.isNullOrEmpty()
        }

        clearButton.setOnClickListener {

            searchEditText.setText("")

            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(
                searchEditText.windowToken,
                0
            )

            searchEditText.clearFocus()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchText = savedInstanceState.getString(SEARCH_TEXT, "")

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText(searchText)
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}