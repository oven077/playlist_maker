package com.example.playlistmaker.main.presentation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.core.di.Creator
import com.example.playlistmaker.library.presentation.activity.LibraryActivity
import com.example.playlistmaker.search.presentation.activity.SearchActivity
import com.example.playlistmaker.settings.domain.interactor.GetDarkThemeInteractor
import com.example.playlistmaker.settings.presentation.activity.SettingsActivity


class MainActivity : AppCompatActivity() {

    private lateinit var getDarkThemeInteractor: GetDarkThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        initDependencies()

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

    private fun initDependencies() {
        getDarkThemeInteractor = Creator.provideGetDarkThemeInteractor(this)
    }

    private fun navigateTo(activityClass: Class<out Activity>) {
        startActivity(Intent(this@MainActivity, activityClass))
    }


}

