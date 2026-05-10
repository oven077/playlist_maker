package com.agermolin.playlistmaker.player.presentation.fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import androidx.core.content.ContextCompat
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.databinding.FragmentPlayerBinding
import com.agermolin.playlistmaker.player.presentation.adapter.PlayerPlaylistPickerAdapter
import com.agermolin.playlistmaker.player.presentation.viewmodel.AddTrackToPlaylistUiEvent
import com.agermolin.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.agermolin.playlistmaker.player.service.PlayerService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()
    private val gson = Gson()

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = requireNotNull(_binding)

    private lateinit var playlistsBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var playerServiceBound = false
    private var trackJson: String? = null

    private val activityLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            viewModel.onUiStarted()
        }

        override fun onStop(owner: LifecycleOwner) {
            viewModel.onUiStopped(canShowNotification())
        }
    }

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (!isGranted) {
            viewModel.onUiStarted()
        }
    }

    private val playerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? PlayerService.PlayerBinder ?: return
            viewModel.attachService(binder.getService())
            playerServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerServiceBound = false
            viewModel.detachService()
        }
    }

    private val playlistPickerAdapter = PlayerPlaylistPickerAdapter { playlist ->
        viewModel.onPlaylistPicked(playlist)
    }

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlaylistBottomSheet()

        trackJson = requireArguments().getString(Constants.TRACK)
        if (trackJson.isNullOrBlank()) {
            findNavController().popBackStack()
            return
        }

        val track = gson.fromJson(trackJson, Track::class.java)
        bindPlayerService(trackJson!!)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onScreenClosed()
                    findNavController().popBackStack()
                }
            },
        )

        binding.playerToolbar.setNavigationOnClickListener {
            viewModel.onScreenClosed()
            findNavController().popBackStack()
        }

        binding.playTrack.isEnabled = false
        binding.playTrack.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.buttonAddToFavorites.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.buttonAddToPlaylist.setOnClickListener {
            playlistsBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.buttonBottomSheetNewPlaylist.setOnClickListener {
            playlistsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        binding.playerPlaylistsRecycler.adapter = playlistPickerAdapter
        binding.playerPlaylistsRecycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel.playlists.observe(viewLifecycleOwner) { list ->
            playlistPickerAdapter.submitList(list)
        }

        viewModel.addTrackToPlaylistEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { uiEvent ->
                when (uiEvent) {
                    is AddTrackToPlaylistUiEvent.Added -> {
                        playlistsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.track_added_to_playlist, uiEvent.playlistName),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                    is AddTrackToPlaylistUiEvent.AlreadyInPlaylist -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.track_already_in_playlist, uiEvent.playlistName),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }

        initTrackInfo(track)
        observeViewModel()
        requireActivity().lifecycle.addObserver(activityLifecycleObserver)
        requestNotificationPermissionIfNeeded()
    }

    private fun setupPlaylistBottomSheet() {
        playlistsBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
        }

        playlistsBottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.playerPlaylistsOverlay.visibility = View.GONE
                        }
                        else -> {
                            binding.playerPlaylistsOverlay.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            },
        )

        binding.playerPlaylistsOverlay.setOnClickListener {
            playlistsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun initTrackInfo(track: Track) {
        viewModel.initTrack(track)

        Glide
            .with(binding.trackIcon)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder_512)
            .centerCrop()
            .transform(
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius_8)),
            )
            .into(binding.trackIcon)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.primaryGenreName.text = track.primaryGenreName
        binding.countryData.text = track.country
        binding.trackTime.text = timeFormat.format(track.trackTimeMillis.toLong())
        binding.progress.text = Constants.CURRENT_TIME_ZERO

        val release = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(track.releaseDate)
        release?.let {
            val formatted = SimpleDateFormat("yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(it)
            binding.releaseDateData.text = formatted
        }

        if (track.collectionName.isNotEmpty()) {
            binding.albumName.text = track.collectionName
            binding.albumName.visibility = View.VISIBLE
            binding.album.visibility = View.VISIBLE
        } else {
            binding.albumName.visibility = View.GONE
            binding.album.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            state.track?.let {
                binding.playTrack.isEnabled = state.isPrepared || state.wasPrepared

                binding.playTrack.setPlaying(state.isPlaying)

                binding.progress.text = timeFormat.format(state.currentPosition.toLong())

                val favoriteIconRes = if (it.isFavorite) R.drawable.favorites_filled else R.drawable.favorites
                binding.buttonAddToFavorites.setImageResource(favoriteIconRes)
                val favoriteTint = if (it.isFavorite) {
                    ContextCompat.getColor(requireContext(), R.color.favorite_heart_color)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.white)
                }
                binding.buttonAddToFavorites.imageTintList = ColorStateList.valueOf(favoriteTint)
            }
        }
    }

    override fun onDestroyView() {
        requireActivity().lifecycle.removeObserver(activityLifecycleObserver)
        if (playerServiceBound) {
            requireContext().unbindService(playerServiceConnection)
            playerServiceBound = false
        }
        viewModel.detachService()
        super.onDestroyView()
        _binding = null
    }

    private fun bindPlayerService(trackJson: String) {
        val serviceIntent = Intent(requireContext(), PlayerService::class.java).apply {
            putExtra(Constants.TRACK, trackJson)
        }
        requireContext().bindService(
            serviceIntent,
            playerServiceConnection,
            Context.BIND_AUTO_CREATE,
        )
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (canShowNotification()) return
        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun canShowNotification(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }
}
