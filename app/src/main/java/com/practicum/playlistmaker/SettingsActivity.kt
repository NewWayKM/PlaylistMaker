package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import androidx.core.net.toUri
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import android.content.res.Configuration

class SettingsActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
            findViewById<SwitchCompat>(R.id.darkThemeSwitch)

        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_YES

        darkThemeSwitch.isChecked = isDarkTheme

        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->

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

            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_message)
            )

            startActivity(Intent.createChooser(shareIntent, null))
        }

        val supportRow = findViewById<View>(R.id.supportRow)

        supportRow.setOnClickListener {

            val supportIntent = Intent(Intent.ACTION_SENDTO)

            supportIntent.data =
                "mailto:${getString(R.string.support_email)}".toUri()

            supportIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.support_email_subject)
            )

            supportIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.support_email_body)
            )

            startActivity(supportIntent)
        }

        val userAgreementRow = findViewById<View>(R.id.userAgreementRow)

        userAgreementRow.setOnClickListener {

            val agreementIntent = Intent(
                Intent.ACTION_VIEW,
                getString(R.string.user_agreement_url).toUri()
            )

            startActivity(agreementIntent)
        }

    }
}