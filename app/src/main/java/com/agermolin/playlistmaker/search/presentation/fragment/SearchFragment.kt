package com.agermolin.playlistmaker.search.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.Constants
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.databinding.FragmentSearchBinding
import com.agermolin.playlistmaker.search.presentation.adapter.SearchRecyclerAdapter
import com.agermolin.playlistmaker.search.presentation.viewmodel.SearchScreenState
import com.agermolin.playlistmaker.search.presentation.viewmodel.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = requireNotNull(_binding)

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    private lateinit var searchAdapter: SearchRecyclerAdapter
    private lateinit var historyAdapter: SearchRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Top-level tab: no back arrow
        binding.searchToolbar.navigationIcon = null

        binding.clearForm.visibility = View.INVISIBLE

        initSearchHistory()
        initRecycler()
        initSearch()
        initInputText()
        initRetry()

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onPause() {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        super.onPause()
    }

    private fun initSearch() {
        binding.inputSearchForm.requestFocus()

        binding.clearForm.setOnClickListener {
            if (binding.inputSearchForm.text.isNotEmpty()) {
                binding.inputSearchForm.text.clear()
                binding.clearForm.visibility = View.INVISIBLE
                viewModel.clearSearch()

                val imm = requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(binding.inputSearchForm.windowToken, 0)
            }
        }

        binding.inputSearchForm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.inputSearchForm.text.toString().trim()
                viewModel.getTracks(query)
                true
            } else false
        }
    }

    private fun initInputText() {
        binding.inputSearchForm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearForm.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                viewModel.searchDebounce(s?.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.inputSearchForm.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputSearchForm.text.isEmpty()) {
                viewModel.clearSearch()
            } else if (!hasFocus && binding.historyList.visibility == View.VISIBLE) {
                binding.historyList.visibility = View.GONE
            }
        }
    }

    private fun initRecycler() {
        searchAdapter = SearchRecyclerAdapter(
            items = tracks,
            onTrackClick = { track ->
                viewModel.onTrackClicked(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    bundleOf(Constants.TRACK to Gson().toJson(track))
                )
            },
        )
        binding.recyclerView.adapter = searchAdapter
    }

    private fun initRetry() {
        binding.buttonRetry.setOnClickListener {
            val query = binding.inputSearchForm.text.toString().trim()
            viewModel.getTracks(query)
        }
    }

    private fun initSearchHistory() {
        historyAdapter = SearchRecyclerAdapter(
            items = historyTracks,
            onTrackClick = { track ->
                viewModel.onTrackClicked(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    bundleOf(Constants.TRACK to Gson().toJson(track))
                )
            },
        )
        binding.recyclerViewHistory.adapter = historyAdapter

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> {
                binding.searchProgressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.placeholderNothingWasFound.visibility = View.GONE
                binding.placeholderCommunicationsProblem.visibility = View.GONE
                binding.historyList.visibility = View.GONE
            }

            is SearchScreenState.Success -> {
                binding.searchProgressBar.visibility = View.GONE
                binding.placeholderNothingWasFound.visibility = View.GONE
                binding.placeholderCommunicationsProblem.visibility = View.GONE
                binding.historyList.visibility = View.GONE

                if (state.tracks.isNotEmpty()) {
                    tracks.clear()
                    tracks.addAll(state.tracks)
                    searchAdapter.notifyDataSetChanged()
                    binding.recyclerView.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility = View.GONE
                }
            }

            is SearchScreenState.ShowHistory -> {
                binding.searchProgressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.placeholderNothingWasFound.visibility = View.GONE
                binding.placeholderCommunicationsProblem.visibility = View.GONE

                historyTracks.clear()
                historyTracks.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()
                binding.historyList.visibility =
                    if (state.tracks.isNotEmpty()) View.VISIBLE else View.GONE
            }

            is SearchScreenState.Error -> {
                binding.searchProgressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.placeholderNothingWasFound.visibility = View.GONE
                binding.placeholderCommunicationsProblem.visibility = View.VISIBLE
                binding.historyList.visibility = View.GONE
            }

            is SearchScreenState.NothingFound -> {
                binding.searchProgressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.placeholderNothingWasFound.visibility = View.VISIBLE
                binding.placeholderCommunicationsProblem.visibility = View.GONE
                binding.historyList.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

