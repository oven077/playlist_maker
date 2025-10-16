package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.example.playlistmaker.model.Track
import java.text.SimpleDateFormat
import java.util.*

class AudioplayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK = "TRACK" // Добавьте свою константу
    }

    private lateinit var toolbar: Toolbar
    private lateinit var trackName: TextView
    private lateinit var trackTime: TextView
    private lateinit var artistName: TextView
    private lateinit var albumIcon: ImageView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        initToolbar()
        initTrackInfo()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.player_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initTrackInfo() {
        val trackJson = intent.getStringExtra(TRACK)
        if (trackJson == null) {
            finish()
            return
        }

        val track = try {
            Gson().fromJson(trackJson, Track::class.java)
        } catch (e: Exception) {
            finish()
            return
        }

        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        albumIcon = findViewById(R.id.track_icon)
        collectionName = findViewById(R.id.album_name)
        releaseDate = findViewById(R.id.release_date_data)
        primaryGenreName = findViewById(R.id.primary_genre_name)
        country = findViewById(R.id.country_data)

        // Безопасная загрузка изображения
        if (track.artworkUrl100.isNotEmpty()) {
            try {
                val imageUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
                Glide.with(albumIcon)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_512)
                    .centerCrop()
                    .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius_8)))
                    .into(albumIcon)
            } catch (e: Exception) {
                albumIcon.setImageResource(R.drawable.placeholder_512)
            }
        } else {
            albumIcon.setImageResource(R.drawable.placeholder_512)
        }

        trackName.text = track.trackName
        artistName.text = track.artistName
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        // Безопасное форматирование времени
        try {
            val timeMillis = track.trackTimeMillis.toIntOrNull() ?: 0
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
        } catch (e: Exception) {
            trackTime.text = "0:00"
        }

        // Безопасное форматирование даты
        try {
            if (track.releaseDate.isNotEmpty()) {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(track.releaseDate)
                if (date != null) {
                    val formatDatesString = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                    releaseDate.text = formatDatesString
                } else {
                    releaseDate.text = "Unknown Year"
                }
            } else {
                releaseDate.text = "Unknown Year"
            }
        } catch (e: Exception) {
            releaseDate.text = "Unknown Year"
        }

        // Безопасная обработка альбома
        if (track.collectionName.isNotEmpty()) {
            collectionName.text = track.collectionName
            collectionName.visibility = View.VISIBLE
        } else {
            collectionName.visibility = View.GONE
        }
    }
}