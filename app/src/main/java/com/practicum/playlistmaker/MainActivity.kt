package com.practicum.playlistmaker

import android.os.Bundle
// import androidx.activity.enableEdgeToEdge // спринт 8. Структура проекта
import androidx.appcompat.app.AppCompatActivity
// import androidx.core.view.ViewCompat // спринт 8. Структура проекта
// import androidx.core.view.WindowInsetsCompat // спринт 8. Структура проекта

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // спринт 8. Структура проекта
        setContentView(R.layout.activity_main)
        /* // спринт 8. Структура проекта
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        */
    }
}