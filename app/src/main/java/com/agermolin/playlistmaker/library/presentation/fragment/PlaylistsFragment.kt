package com.agermolin.playlistmaker.library.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.databinding.FragmentPlaylistsBinding
import com.agermolin.playlistmaker.library.presentation.adapter.PlaylistsAdapter
import com.agermolin.playlistmaker.library.presentation.decoration.GridSpacingItemDecoration
import com.agermolin.playlistmaker.library.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = requireNotNull(_binding)

    private val adapter = PlaylistsAdapter { playlist ->
        // Destination id, не action из libraryFragment — иначе при currentDestination != libraryFragment будет crash
        findNavController().navigate(
            R.id.playlistDetailFragment,
            bundleOf(Constants.PLAYLIST_ID to playlist.id),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanCount = 2
        binding.playlistsRecycler.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.playlistsRecycler.adapter = adapter
        if (binding.playlistsRecycler.itemDecorationCount == 0) {
            val spacing = resources.getDimensionPixelSize(R.dimen.padding_8)
            binding.playlistsRecycler.addItemDecoration(
                GridSpacingItemDecoration(spanCount, spacing),
            )
        }

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { list ->
            val items = list.orEmpty()
            val hasPlaylists = items.isNotEmpty()
            binding.playlistsRecycler.isVisible = hasPlaylists
            binding.playlistsPlaceholder.isVisible = !hasPlaylists
            adapter.submitList(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playlistsRecycler.adapter = null
        _binding = null
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }
}
