package com.practicum.playlistmaker.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.state.SettingsAction
import com.practicum.playlistmaker.presentation.state.SettingsScreenState
import com.practicum.playlistmaker.presentation.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.presentation.viewmodel.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    // 1. ViewModel экрана настроек
    private lateinit var viewModel: SettingsViewModel

    // 1.1. Переключатель темы и защита от повторной обработки
    private lateinit var darkThemeSwitch: SwitchMaterial
    private var isRenderingTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 2. Создание ViewModel через ViewModelProvider
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory()
        )[SettingsViewModel::class.java]

        val settingsRoot = findViewById<View>(R.id.settingsRoot)

        ViewCompat.setOnApplyWindowInsetsListener(settingsRoot)
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

        val backButton =
            findViewById<ImageView>(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // 3. Получение переключателя темы
        darkThemeSwitch =
            findViewById<SwitchMaterial>(R.id.darkThemeSwitch)

        // 3.1. Отключение автоматического восстановления состояния View
        darkThemeSwitch.isSaveEnabled = false

        // 3.2. Передача выбора пользователя во ViewModel
        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isRenderingTheme) {
                viewModel.onThemeChanged(isChecked)
            }
        }

        val shareAppRow = findViewById<View>(R.id.shareAppRow)

        shareAppRow.setOnClickListener {
            viewModel.onShareClicked()
        }

        val supportRow = findViewById<View>(R.id.supportRow)

        supportRow.setOnClickListener {
            viewModel.onSupportClicked()
        }

        val userAgreementRow = findViewById<View>(R.id.userAgreementRow)

        userAgreementRow.setOnClickListener {
            viewModel.onAgreementClicked()
        }

        // 3.3. Подписка на состояние экрана настроек
        viewModel.state.observe(this) { state ->
            render(state)
        }
    }

    // 3.4. Отображение состояния, полученного из ViewModel
    private fun render(state: SettingsScreenState) {

        // 3.4.1. Определение системной темы
        val systemDark = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        // 3.4.2. Обновление переключателя без повторного сохранения
        isRenderingTheme = true
        darkThemeSwitch.isChecked = state.darkTheme ?: systemDark
        isRenderingTheme = false

        // 3.4.3. Применение выбранной темы
        val mode = when (state.darkTheme) {
            true -> AppCompatDelegate.MODE_NIGHT_YES
            false -> AppCompatDelegate.MODE_NIGHT_NO
            null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // 3.4.4. Выполнение внешнего действия
        state.pendingAction?.let { action ->
            viewModel.onExternalActionHandled()
            openExternalAction(action)
        }
    }

    // 3.5. Открытие внешних приложений
    private fun openExternalAction(action: SettingsAction) {

        when (action) {

            // 3.5.1. Поделиться приложением
            is SettingsAction.Share -> {
                val shareIntent = Intent(Intent.ACTION_SEND)

                shareIntent.type = "text/plain"
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    action.appLink
                )

                startExternalActivity(Intent.createChooser(shareIntent, null))
            }

            // 3.5.2. Написать в поддержку
            is SettingsAction.Support -> {
                val sharingData = action.data

                // Создание Intent для электронной почты
                val supportIntent = Intent(Intent.ACTION_SENDTO)

                supportIntent.data =
                    "mailto:${sharingData.supportEmail}".toUri()

                supportIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    sharingData.supportSubject
                )

                supportIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    sharingData.supportBody
                )

                // Открытие почтового приложения
                startExternalActivity(supportIntent)
            }

            // 3.5.3. Пользовательское соглашение
            is SettingsAction.Agreement -> {
                val agreementIntent = Intent(
                    Intent.ACTION_VIEW,
                    action.url.toUri()
                )

                startExternalActivity(agreementIntent)
            }
        }
    }

    // 4. Безопасное открытие внешнего приложения
    private fun startExternalActivity(intent: Intent) {
        try {
            startActivity(intent)
        } catch (error: ActivityNotFoundException) {
            Toast.makeText(
                this,
                R.string.external_app_not_found,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}