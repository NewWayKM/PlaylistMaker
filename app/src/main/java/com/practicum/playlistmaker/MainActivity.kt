package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainRoot = findViewById<View>(R.id.mainRoot)

        ViewCompat.setOnApplyWindowInsetsListener(mainRoot)
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

        val searchButton =
            findViewById<MaterialButton>(R.id.searchButton)

        val mediaLibraryButton =
            findViewById<MaterialButton>(R.id.mediaLibraryButton)

        val settingsButton =
            findViewById<MaterialButton>(R.id.settingsButton)

        searchButton.setOnClickListener {
            val intent = Intent(
                this,
                SearchActivity::class.java
            )
            startActivity(intent)
        }

        mediaLibraryButton.setOnClickListener {
            val intent = Intent(
                this,
                MediaLibraryActivity::class.java
            )
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(
                this,
                SettingsActivity::class.java
            )
            startActivity(intent)
        }
    }
}