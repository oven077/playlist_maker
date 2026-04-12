package com.agermolin.playlistmaker.library.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.library.presentation.viewmodel.PlaylistDetailViewModel
import com.agermolin.playlistmaker.search.presentation.adapter.SearchRecyclerAdapter
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.util.Locale

class PlaylistDetailFragment : Fragment() {

    private val viewModel: PlaylistDetailViewModel by viewModel {
        parametersOf(requireArguments().getLong(Constants.PLAYLIST_ID))
    }

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding: FragmentPlaylistDetailBinding get() = requireNotNull(_binding)

    private val tracks = ArrayList<Track>()
    private lateinit var adapter: SearchRecyclerAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var currentContent: PlaylistDetailResult.Content? = null

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
        setupActions()

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailResult.NotFound -> findNavController().popBackStack()
                is PlaylistDetailResult.Content -> renderContent(state)
                null -> Unit
            }
        }

        viewModel.playlistDeleted.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun renderContent(content: PlaylistDetailResult.Content) {
        currentContent = content
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

    private fun setupActions() {
        binding.playlistDetailShare.setOnClickListener {
            sharePlaylist()
        }
        binding.playlistDetailMore.setOnClickListener {
            showPlaylistMenuBottomSheet()
        }
    }

    private fun sharePlaylist() {
        val content = currentContent ?: return
        if (content.tracks.isEmpty()) {
            Toast.makeText(requireContext(), R.string.playlist_share_empty_error, Toast.LENGTH_SHORT).show()
            return
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, buildShareText(content.playlist, content.tracks))
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun showPlaylistMenuBottomSheet() {
        val content = currentContent ?: return
        val sheetDialog = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_playlist_menu, null)
        sheetDialog.setContentView(sheetView)

        val coverView = sheetView.findViewById<ImageView>(R.id.menu_playlist_cover)
        val nameView = sheetView.findViewById<TextView>(R.id.menu_playlist_name)
        val countView = sheetView.findViewById<TextView>(R.id.menu_playlist_count)
        nameView.text = content.playlist.name
        countView.text = resources.getQuantityString(
            R.plurals.playlist_track_count,
            content.playlist.trackCount,
            content.playlist.trackCount,
        )
        bindMenuCover(coverView, content.playlist.coverImagePath)

        sheetView.findViewById<View>(R.id.menu_action_share).setOnClickListener {
            sheetDialog.dismiss()
            sharePlaylist()
        }
        sheetView.findViewById<View>(R.id.menu_action_edit).setOnClickListener {
            sheetDialog.dismiss()
            findNavController().navigate(
                R.id.action_playlistDetailFragment_to_newPlaylistFragment,
                bundleOf(Constants.EDIT_PLAYLIST_ID to content.playlist.id),
            )
        }
        sheetView.findViewById<View>(R.id.menu_action_delete).setOnClickListener {
            sheetDialog.dismiss()
            showDeletePlaylistDialog()
        }

        sheetDialog.show()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_PlaylistMaker_LightAlertDialog)
            .setTitle(R.string.delete_playlist_title)
            .setMessage(R.string.delete_playlist_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteCurrentPlaylist()
            }
            .show()
    }

    private fun buildShareText(playlist: Playlist, playlistTracks: List<Track>): String {
        val tracksCountLine = resources.getQuantityString(
            R.plurals.playlist_track_count,
            playlistTracks.size,
            playlistTracks.size,
        )
        return buildString {
            appendLine(playlist.name)
            appendLine(playlist.description)
            appendLine(tracksCountLine)
            playlistTracks.forEachIndexed { index, track ->
                appendLine(
                    "${index + 1}. ${track.artistName} - ${track.trackName} (${formatTrackDuration(track.trackTimeMillis)})",
                )
            }
        }.trimEnd()
    }

    private fun formatTrackDuration(trackTimeMillis: Int): String {
        val totalSeconds = trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun bindMenuCover(imageView: ImageView, path: String?) {
        val file = path?.let { File(it) }
        if (file != null && file.exists() && file.length() > 0L) {
            Glide.with(this)
                .load(file)
                .centerCrop()
                .into(imageView)
        } else {
            Glide.with(imageView).clear(imageView)
            imageView.setImageResource(R.drawable.playlist_grid_cover_placeholder)
        }
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
