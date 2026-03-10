package com.agermolin.playlistmaker.library.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agermolin.playlistmaker.library.presentation.fragment.FavoritesTracksFragment
import com.agermolin.playlistmaker.library.presentation.fragment.PlaylistsFragment

class MediaLibraryPagerAdapter : FragmentStateAdapter {

    constructor(activity: FragmentActivity) : super(activity)
    constructor(fragment: Fragment) : super(fragment)

    override fun getItemCount(): Int = PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            PAGE_FAVORITES -> FavoritesTracksFragment.newInstance()
            PAGE_PLAYLISTS -> PlaylistsFragment.newInstance()
            else -> error("Unknown page position: $position")
        }
    }

    companion object {
        const val PAGE_FAVORITES = 0
        const val PAGE_PLAYLISTS = 1
        const val PAGE_COUNT = 2
    }
}

