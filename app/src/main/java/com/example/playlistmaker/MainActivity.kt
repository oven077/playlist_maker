package com.example.playlistmaker
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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