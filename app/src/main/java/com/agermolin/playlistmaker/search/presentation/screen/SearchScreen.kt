package com.agermolin.playlistmaker.search.presentation.screen

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.core.presentation.component.ConnectionErrorPlaceholder
import com.agermolin.playlistmaker.core.presentation.component.PlaylistMakerTopBar
import com.agermolin.playlistmaker.core.presentation.component.TrackListItem
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayMedium
import com.agermolin.playlistmaker.search.presentation.component.SearchTextField
import com.agermolin.playlistmaker.search.presentation.viewmodel.SearchScreenState
import com.agermolin.playlistmaker.search.presentation.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    onTrackClick: (Track) -> Unit,
    onHideKeyboard: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val screenState by viewModel.screenState.observeAsState(SearchScreenState.Success(emptyList()))
    var query by remember { mutableStateOf("") }
    var isSearchFieldFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.screen_background)),
    ) {
        PlaylistMakerTopBar(title = stringResource(R.string.search))

        SearchTextField(
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery
                viewModel.searchDebounce(newQuery)
            },
            onSearch = {
                viewModel.getTracks(query.trim())
            },
            onClear = {
                if (query.isNotEmpty()) {
                    query = ""
                    viewModel.clearSearch()
                    onHideKeyboard()
                }
            },
            onFocusChanged = { hasFocus ->
                isSearchFieldFocused = hasFocus
                if (hasFocus && query.isEmpty()) {
                    viewModel.clearSearch()
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 24.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (val state = screenState) {
                is SearchScreenState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 160.dp),
                        color = colorResource(R.color.progress_indicator_color),
                    )
                }

                is SearchScreenState.Success -> {
                    if (state.tracks.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                items = state.tracks,
                                key = { it.trackId },
                            ) { track ->
                                TrackListItem(
                                    track = track,
                                    onClick = {
                                        viewModel.onTrackClicked(track)
                                        onTrackClick(track)
                                    },
                                )
                            }
                        }
                    }
                }

                is SearchScreenState.ShowHistory -> {
                    if (state.tracks.isNotEmpty() && isSearchFieldFocused) {
                        SearchHistoryContent(
                            tracks = state.tracks,
                            onTrackClick = { track ->
                                viewModel.onTrackClicked(track)
                                onTrackClick(track)
                            },
                            onClearHistory = { viewModel.clearHistory() },
                        )
                    }
                }

                is SearchScreenState.Error -> {
                    ConnectionErrorPlaceholder(
                        onRetry = { viewModel.getTracks(query.trim()) },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is SearchScreenState.NothingFound -> {
                    NothingFoundPlaceholder(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
private fun SearchHistoryContent(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.search_history),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 20.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = YsDisplayMedium,
            ),
            color = MaterialTheme.colorScheme.onSecondary,
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(
                items = tracks,
                key = { it.trackId },
            ) { track ->
                TrackListItem(
                    track = track,
                    onClick = { onTrackClick(track) },
                )
            }
        }

        Button(
            onClick = onClearHistory,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.button_background_color),
                contentColor = colorResource(R.color.button_text_color),
            ),
        ) {
            Text(
                text = stringResource(R.string.clear_history),
                modifier = Modifier.padding(15.dp),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = YsDisplayMedium,
                ),
            )
        }
    }
}

@Composable
private fun NothingFoundPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.nothingfound),
            contentDescription = stringResource(R.string.nothing_found),
            modifier = Modifier.size(120.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified,
        )
        Text(
            text = stringResource(R.string.nothing_found),
            modifier = Modifier.padding(top = 20.dp),
            textAlign = TextAlign.Center,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                fontFamily = YsDisplayMedium,
            ),
            color = colorResource(R.color.text_color_primary),
        )
    }
}
