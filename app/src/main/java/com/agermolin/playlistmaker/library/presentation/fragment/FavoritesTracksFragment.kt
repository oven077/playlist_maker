package com.agermolin.playlistmaker.library.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.agermolin.playlistmaker.library.presentation.viewmodel.FavoritesTracksScreenState
import com.agermolin.playlistmaker.library.presentation.viewmodel.FavoritesTracksViewModel
import com.agermolin.playlistmaker.search.presentation.adapter.SearchRecyclerAdapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {

    private val viewModel: FavoritesTracksViewModel by viewModel()

    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding: FragmentFavoritesTracksBinding get() = requireNotNull(_binding)

    private val tracks = ArrayList<Track>()
    private lateinit var adapter: SearchRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Показываем заглушку по умолчанию, пока LiveData не эмитит
        binding.favoritesPlaceholderContainer.visibility = View.VISIBLE
        binding.favoritesRecyclerView.visibility = View.GONE
        initRecycler()
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun initRecycler() {
        adapter = SearchRecyclerAdapter(
            items = tracks,
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_libraryFragment_to_playerFragment,
                    bundleOf(Constants.TRACK to Gson().toJson(track))
                )
            },
        )
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun render(state: FavoritesTracksScreenState) {
        when (state) {
            is FavoritesTracksScreenState.Empty -> {
                binding.favoritesPlaceholderContainer.visibility = View.VISIBLE
                binding.favoritesRecyclerView.visibility = View.GONE
            }
            is FavoritesTracksScreenState.Content -> {
                binding.favoritesPlaceholderContainer.visibility = View.GONE
                binding.favoritesRecyclerView.visibility = View.VISIBLE
                tracks.clear()
                tracks.addAll(state.tracks)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): FavoritesTracksFragment = FavoritesTracksFragment()
    }
}

