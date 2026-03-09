package com.agermolin.playlistmaker.player.presentation.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import androidx.core.content.ContextCompat
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.databinding.FragmentPlayerBinding
import com.agermolin.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = requireNotNull(_binding)

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

        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.playTrack.isEnabled = false
        binding.playTrack.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.buttonAddToFavorites.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        val trackJson = requireArguments().getString(Constants.TRACK)
        if (trackJson.isNullOrBlank()) {
            findNavController().popBackStack()
            return
        }

        val track = Gson().fromJson(trackJson, Track::class.java)
        initTrackInfo(track)
        observeViewModel()
    }

    private fun initTrackInfo(track: Track) {
        viewModel.initTrack(track)

        Glide
            .with(binding.trackIcon)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder_512)
            .centerCrop()
            .transform(
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius_8))
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

                val iconRes = if (state.isPlaying) R.drawable.pause else R.drawable.play
                binding.playTrack.setImageResource(iconRes)

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

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

