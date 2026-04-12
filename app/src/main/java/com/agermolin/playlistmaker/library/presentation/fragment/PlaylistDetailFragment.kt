package com.agermolin.playlistmaker.library.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.agermolin.playlistmaker.library.domain.model.PlaylistDetailResult
import com.agermolin.playlistmaker.library.presentation.viewmodel.PlaylistDetailViewModel
import com.agermolin.playlistmaker.search.presentation.adapter.SearchRecyclerAdapter
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistDetailFragment : Fragment() {

    private val viewModel: PlaylistDetailViewModel by viewModel {
        parametersOf(requireArguments().getLong(Constants.PLAYLIST_ID))
    }

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding: FragmentPlaylistDetailBinding get() = requireNotNull(_binding)

    private val tracks = ArrayList<Track>()
    private lateinit var adapter: SearchRecyclerAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistDetailToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        adapter = SearchRecyclerAdapter(
            items = tracks,
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_playlistDetailFragment_to_playerFragment,
                    bundleOf(Constants.TRACK to Gson().toJson(track)),
                )
            },
            onTrackLongClick = { track ->
                showRemoveTrackDialog(track)
            },
        )
        binding.playlistDetailTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistDetailTracks.adapter = adapter
        setupBottomSheet()

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailResult.NotFound -> findNavController().popBackStack()
                is PlaylistDetailResult.Content -> renderContent(state)
                null -> Unit
            }
        }
    }

    private fun renderContent(content: PlaylistDetailResult.Content) {
        val playlist = content.playlist
        binding.playlistDetailToolbar.title = ""
        binding.playlistDetailName.text = playlist.name
        binding.playlistDetailMetaTrackCount.text = resources.getQuantityString(
            R.plurals.playlist_track_count,
            playlist.trackCount,
            playlist.trackCount,
        )
        val totalMinutes = (content.tracks.sumOf { it.trackTimeMillis.toLong() } / 60_000L).toInt()
        binding.playlistDetailMetaDuration.text = resources.getQuantityString(
            R.plurals.playlist_duration_minutes,
            totalMinutes,
            totalMinutes,
        )

        if (playlist.description.isNotBlank()) {
            binding.playlistDetailDescription.text = playlist.description
            binding.playlistDetailDescription.isVisible = true
        } else {
            binding.playlistDetailDescription.isVisible = false
        }

        bindCover(playlist.coverImagePath)

        val hasTracks = content.tracks.isNotEmpty()
        binding.playlistDetailPlaceholder.isVisible = !hasTracks
        binding.playlistDetailTracks.isVisible = hasTracks
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        if (hasTracks) {
            tracks.clear()
            tracks.addAll(content.tracks)
            adapter.notifyDataSetChanged()
        } else {
            tracks.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistDetailBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
            skipCollapsed = false
            peekHeight = resources.getDimensionPixelSize(R.dimen.playlist_detail_sheet_height)
        }
    }

    private fun showRemoveTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_PlaylistMaker_LightAlertDialog)
            .setTitle(R.string.delete_track_from_playlist_title)
            .setNegativeButton(R.string.dialog_no, null)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.removeTrack(track.trackId)
            }
            .show()
    }

    private fun bindCover(path: String?) {
        val file = path?.let { File(it) }
        if (file != null && file.exists() && file.length() > 0L) {
            Glide.with(this)
                .load(file)
                .centerCrop()
                .into(binding.playlistDetailCover)
        } else {
            Glide.with(binding.playlistDetailCover).clear(binding.playlistDetailCover)
            binding.playlistDetailCover.setImageResource(R.drawable.placeholder_512)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playlistDetailTracks.adapter = null
        _binding = null
    }
}
