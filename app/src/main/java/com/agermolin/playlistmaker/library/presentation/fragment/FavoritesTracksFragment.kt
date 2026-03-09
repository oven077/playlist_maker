package com.agermolin.playlistmaker.library.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.agermolin.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.agermolin.playlistmaker.library.presentation.viewmodel.FavoritesTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {

    private val viewModel: FavoritesTracksViewModel by viewModel()

    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding: FragmentFavoritesTracksBinding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): FavoritesTracksFragment = FavoritesTracksFragment()
    }
}

