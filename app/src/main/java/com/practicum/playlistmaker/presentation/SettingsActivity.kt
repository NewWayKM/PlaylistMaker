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
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SharingInteractor

class SettingsActivity : AppCompatActivity() {

    // 1. Интеракторы экрана настроек
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var sharingInteractor: SharingInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 2. Получение зависимостей через Creator
        settingsInteractor = Creator.provideSettingsInteractor()
        sharingInteractor = Creator.provideSharingInteractor()

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

        val darkThemeSwitch =
            findViewById<SwitchMaterial>(R.id.darkThemeSwitch)

        // 3. Определение системной темы
        val systemDark = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        // 3.1. Отображение сохранённой или системной темы
        darkThemeSwitch.isChecked =
            settingsInteractor.read().darkTheme ?: systemDark

        // 3.2. Сохранение и применение выбранной темы
        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsInteractor.setDarkTheme(isChecked)

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        val shareAppRow = findViewById<View>(R.id.shareAppRow)

        shareAppRow.setOnClickListener {

            val sharingData = sharingInteractor.read()

            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                sharingData.appLink
            )

            startExternalActivity(Intent.createChooser(shareIntent, null))
        }

        val supportRow = findViewById<View>(R.id.supportRow)

        supportRow.setOnClickListener {
            // 3.3. Получение данных поддержки через интерактор
            val sharingData = sharingInteractor.read()

            // 3.4. Создание Intent для электронной почты
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

            // 3.5. Открытие почтового приложения
            startExternalActivity(supportIntent)
        }

        val userAgreementRow = findViewById<View>(R.id.userAgreementRow)

        userAgreementRow.setOnClickListener {
            val sharingData = sharingInteractor.read()

            val agreementIntent = Intent(
                Intent.ACTION_VIEW,
                sharingData.agreementUrl.toUri()
            )

            startExternalActivity(agreementIntent)
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