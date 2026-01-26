package com.example.playlistmaker.library.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.library.presentation.adapter.MediaLibraryPagerAdapter
import com.example.playlistmaker.library.presentation.viewmodel.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryActivity : AppCompatActivity() {

    private val viewModel: LibraryViewModel by viewModel()

    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.libraryToolbar.setNavigationOnClickListener {
            finish()
        }

        val pagerAdapter = MediaLibraryPagerAdapter(this)
        binding.libraryViewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.libraryTabs, binding.libraryViewPager) { tab, position ->
            tab.setText(
                when (position) {
                    MediaLibraryPagerAdapter.PAGE_FAVORITES -> R.string.favorites_tracks
                    MediaLibraryPagerAdapter.PAGE_PLAYLISTS -> R.string.playlists
                    else -> error("Unknown tab position: $position")
                }
            )
        }.attach()
    }
}

