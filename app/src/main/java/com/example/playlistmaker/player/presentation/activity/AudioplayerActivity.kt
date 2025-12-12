package com.example.playlistmaker.player.presentation.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.core.Constants
import com.example.playlistmaker.core.entity.Track
import com.example.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var trackName: TextView
    private lateinit var trackDuration: TextView
    private lateinit var artistName: TextView
    private lateinit var albumIcon: ImageView
    private lateinit var collectionName: TextView
    private lateinit var collectionTitle: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var trackProgress: TextView
    private lateinit var playButton: FloatingActionButton

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]

        initToolbar()
        initViews()
        initTrackInfo()
        observeViewModel()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.player_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackDuration = findViewById(R.id.trackTime)
        albumIcon = findViewById(R.id.track_icon)
        collectionName = findViewById(R.id.album_name)
        collectionTitle = findViewById(R.id.album)
        releaseDate = findViewById(R.id.release_date_data)
        primaryGenreName = findViewById(R.id.primary_genre_name)
        country = findViewById(R.id.country_data)
        trackProgress = findViewById(R.id.progress)
        playButton = findViewById(R.id.play_track)

        playButton.isEnabled = false
        playButton.setOnClickListener {
            viewModel.togglePlayback()
        }
    }

    private fun initTrackInfo() {
        val trackJson = intent.getStringExtra(Constants.TRACK)
        val track = Gson().fromJson(trackJson, Track::class.java)

        viewModel.initTrack(track)

        Glide
            .with(albumIcon)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder_512)
            .centerCrop()
            .transform(
                RoundedCorners(
                    resources.getDimensionPixelSize(R.dimen.corner_radius_8)
                )
            )
            .into(albumIcon)

        trackName.text = track.trackName
        artistName.text = track.artistName
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        trackDuration.text = timeFormat.format(track.trackTimeMillis.toLong())
        trackProgress.text = Constants.CURRENT_TIME_ZERO

        val release = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(track.releaseDate)
        release?.let {
            val formatted = SimpleDateFormat("yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(it)
            releaseDate.text = formatted
        }

        if (track.collectionName.isNotEmpty()) {
            collectionName.text = track.collectionName
            collectionName.visibility = View.VISIBLE
            collectionTitle.visibility = View.VISIBLE
        } else {
            collectionName.visibility = View.GONE
            collectionTitle.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this) { state ->
            state.track?.let { track ->
                playButton.isEnabled = state.isPrepared || state.wasPrepared
                
                val iconRes = if (state.isPlaying) {
                    R.drawable.pause
                } else {
                    R.drawable.play
                }
                playButton.setImageResource(iconRes)
                
                trackProgress.text = timeFormat.format(state.currentPosition.toLong())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}

