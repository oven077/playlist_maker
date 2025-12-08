package com.example.playlistmaker.presentation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.di.Creator
import com.example.playlistmaker.domain.interactor.GetDarkThemeInteractor


class MainActivity : AppCompatActivity() {
    
    private lateinit var getDarkThemeInteractor: GetDarkThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        initDependencies()
        
        val isDarkThemeEnabled = getDarkThemeInteractor.execute()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: MainActivity создана $this")


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

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"MainActivity старт $this")
    }


    companion object {
        private const val TAG = "asd"
    }

}
