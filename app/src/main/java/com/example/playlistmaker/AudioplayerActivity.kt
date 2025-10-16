package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.model.Track
import com.google.gson.Gson

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
        findViewById<TextView>(R.id.trackTime).text = track.trackTimeMillis
        findViewById<TextView>(R.id.album_name).text = track.collectionName
        findViewById<TextView>(R.id.release_date_data).text = track.releaseDate
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