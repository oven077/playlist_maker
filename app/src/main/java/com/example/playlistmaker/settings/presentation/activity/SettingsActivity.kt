package com.example.playlistmaker.settings.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.example.playlistmaker.R
import com.example.playlistmaker.core.di.Creator
import com.example.playlistmaker.settings.domain.interactor.GetDarkThemeInteractor
import com.example.playlistmaker.settings.domain.interactor.SetDarkThemeInteractor

class SettingsActivity : AppCompatActivity() {
    
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var getDarkThemeInteractor: GetDarkThemeInteractor
    private lateinit var setDarkThemeInteractor: SetDarkThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        initDependencies()
        
        findViewById<Toolbar>(R.id.settings_toolbar).setNavigationOnClickListener() {
            finish()
        }

        val isDarkThemeEnabled = getDarkThemeInteractor.execute()

        val darkThemeSwitch = findViewById<Switch>(R.id.switch_dark_theme)
        darkThemeSwitch.isChecked = isDarkThemeEnabled
        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
            setDarkThemeInteractor.execute(isChecked)
        }

        findViewById<Button>(R.id.button_sharing).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, null))
        }

        findViewById<Button>(R.id.button_support).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_address)))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_theme))
            startActivity(Intent.createChooser(intent, null))
        }

        findViewById<Button>(R.id.button_user_agreement).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.support_user_agreement))
            startActivity(Intent.createChooser(intent, null))
        }
    }
    
    private fun initDependencies() {
        getDarkThemeInteractor = Creator.provideGetDarkThemeInteractor(this)
        setDarkThemeInteractor = Creator.provideSetDarkThemeInteractor(this)
    }
}

