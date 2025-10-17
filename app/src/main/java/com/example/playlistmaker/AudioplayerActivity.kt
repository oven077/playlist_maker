package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        findViewById<TextView>(R.id.player_toolbar).setOnClickListener {
            finish()
        }

        val trackJson = intent.getStringExtra("TRACK")
        if (trackJson != null) {
            try {
                val track = Gson().fromJson(trackJson, Track::class.java)
                showTrackInfo(track)
            } catch (e: Exception) {
                finish()
            }
        } else {
            finish()
        }
    }

    private fun showTrackInfo(track: Track) {
        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName

        // Правильное форматирование времени
        try {
            val timeMillis = track.trackTimeMillis.toIntOrNull() ?: 0
            val minutes = timeMillis / 60000
            val seconds = (timeMillis % 60000) / 1000
            findViewById<TextView>(R.id.trackTime).text = String.format("%d:%02d", minutes, seconds)
        } catch (e: Exception) {
            findViewById<TextView>(R.id.trackTime).text = "0:00"
        }

        findViewById<TextView>(R.id.album_name).text = track.collectionName

        // Правильное форматирование года
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            val date = inputFormat.parse(track.releaseDate)
            findViewById<TextView>(R.id.release_date_data).text = outputFormat.format(date)
        } catch (e: Exception) {
            // Если не удалось распарсить, показываем как есть
            findViewById<TextView>(R.id.release_date_data).text = track.releaseDate
        }

        findViewById<TextView>(R.id.primary_genre_name).text = track.primaryGenreName
        findViewById<TextView>(R.id.country_data).text = track.country

        val imageView = findViewById<ImageView>(R.id.track_icon)
        if (track.artworkUrl100.isNotEmpty()) {
            Glide.with(this)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder_512)
                .into(imageView)
        }
    }
}