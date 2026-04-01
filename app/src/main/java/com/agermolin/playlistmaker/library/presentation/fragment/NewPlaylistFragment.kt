package com.agermolin.playlistmaker.library.presentation.fragment

import android.content.Intent
import android.net.Uri
import android.graphics.Outline
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.agermolin.playlistmaker.library.presentation.viewmodel.NewPlaylistViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding: FragmentNewPlaylistBinding get() = requireNotNull(_binding)

    private val pickVisualMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            } catch (_: SecurityException) {
            }
        }
        viewModel.setCoverImageUri(uri)
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            tryNavigateBack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.updatePadding(bottom = imeBottom)
            insets
        }

        binding.newPlaylistToolbar.setNavigationOnClickListener {
            tryNavigateBack()
        }

        binding.playlistCoverPlaceholder.setOnClickListener {
            pickVisualMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        }

        binding.inputPlaylistName.doAfterTextChanged { text ->
            viewModel.onPlaylistNameChanged(text?.toString().orEmpty())
        }

        binding.inputPlaylistDescription.doAfterTextChanged { text ->
            viewModel.onDescriptionChanged(text?.toString().orEmpty())
        }

        viewModel.canCreatePlaylist.observe(viewLifecycleOwner) { canCreate ->
            binding.buttonCreatePlaylist.isEnabled = canCreate
        }

        viewModel.coverImageUri.observe(viewLifecycleOwner) { uri ->
            bindCoverImage(uri)
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { name ->
            if (name == null) return@observe
            val message = getString(R.string.playlist_created_toast, name)
            val content = requireActivity().findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(content, message, Snackbar.LENGTH_SHORT)
            val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)
            if (bottomNav.isVisible) {
                snackbar.setAnchorView(bottomNav)
            }
            snackbar.show()
            viewModel.consumeSaveSuccess()
            findNavController().navigateUp()
        }

        viewModel.saveError.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                R.string.playlist_save_error,
                Toast.LENGTH_SHORT,
            ).show()
        }

        binding.buttonCreatePlaylist.setOnClickListener {
            viewModel.createPlaylist()
        }

        viewModel.onPlaylistNameChanged(binding.inputPlaylistName.text?.toString().orEmpty())
        viewModel.onDescriptionChanged(binding.inputPlaylistDescription.text?.toString().orEmpty())

        binding.playlistCoverPlaceholder.post { applyPlaylistCoverOutline() }
    }

    private fun applyPlaylistCoverOutline() {
        val radius = resources.getDimension(R.dimen.new_playlist_cover_corner_radius)
        binding.playlistCoverPlaceholder.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        binding.playlistCoverPlaceholder.clipToOutline = true
    }

    private fun tryNavigateBack() {
        if (!viewModel.hasUnsavedChanges()) {
            findNavController().navigateUp()
            return
        }
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_PlaylistMaker_LightAlertDialog)
            .setTitle(R.string.discard_new_playlist_title)
            .setMessage(R.string.discard_new_playlist_message)
            .setNegativeButton(R.string.discard_new_playlist_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.discard_new_playlist_confirm) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun bindCoverImage(uri: Uri?) {
        val radiusPx = resources.getDimensionPixelSize(R.dimen.new_playlist_cover_corner_radius)
        if (uri == null) {
            Glide.with(this).clear(binding.playlistCoverPreview)
            binding.playlistCoverPreview.setImageDrawable(null)
            binding.playlistCoverPreview.isVisible = false
            binding.playlistCoverPlaceholderIcon.isVisible = true
            binding.playlistCoverPlaceholder.setBackgroundResource(R.drawable.bg_playlist_cover_placeholder)
            binding.playlistCoverPlaceholder.post { applyPlaylistCoverOutline() }
            return
        }
        binding.playlistCoverPreview.isVisible = true
        binding.playlistCoverPlaceholderIcon.isVisible = false
        binding.playlistCoverPlaceholder.setBackgroundResource(android.R.color.transparent)
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(radiusPx)))
            .into(binding.playlistCoverPreview)

        binding.playlistCoverPlaceholder.post { applyPlaylistCoverOutline() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
