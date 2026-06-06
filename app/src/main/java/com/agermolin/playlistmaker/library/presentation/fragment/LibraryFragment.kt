package com.agermolin.playlistmaker.library.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.core.presentation.theme.PlaylistMakerTheme
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.library.presentation.screen.LibraryScreen
import com.google.gson.Gson

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides this@LibraryFragment) {
                PlaylistMakerTheme {
                    LibraryScreen(
                        onTrackClick = ::navigateToPlayer,
                        onPlaylistClick = ::navigateToPlaylistDetail,
                        onNewPlaylistClick = ::navigateToNewPlaylist,
                    )
                }
            }
        }
    }

    private fun navigateToPlayer(track: Track) {
        findNavController().navigate(
            R.id.playerFragment,
            bundleOf(Constants.TRACK to Gson().toJson(track)),
        )
    }

    private fun navigateToPlaylistDetail(playlist: Playlist) {
        findNavController().navigate(
            R.id.playlistDetailFragment,
            bundleOf(Constants.PLAYLIST_ID to playlist.id),
        )
    }

    private fun navigateToNewPlaylist() {
        findNavController().navigate(R.id.newPlaylistFragment)
    }
}
