package com.example.playlistmaker.main.presentation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.library.presentation.activity.LibraryActivity
import com.example.playlistmaker.search.presentation.activity.SearchActivity
import com.example.playlistmaker.settings.domain.interactor.IGetDarkThemeInteractor
import com.example.playlistmaker.settings.presentation.activity.SettingsActivity
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val getDarkThemeInteractor: IGetDarkThemeInteractor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val isDarkThemeEnabled = getDarkThemeInteractor.execute()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_search).setOnClickListener {
            navigateTo(SearchActivity::class.java)
        }

        findViewById<Button>(R.id.btn_media).setOnClickListener {
            navigateTo(LibraryActivity::class.java)
        }

        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
    }

    private fun navigateTo(activityClass: Class<out Activity>) {
        startActivity(Intent(this@MainActivity, activityClass))
    }


}

