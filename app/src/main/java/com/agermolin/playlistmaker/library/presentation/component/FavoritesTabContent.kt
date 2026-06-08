package com.agermolin.playlistmaker.library.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.core.presentation.component.EmptyPlaceholder
import com.agermolin.playlistmaker.core.presentation.component.TrackListItem
import com.agermolin.playlistmaker.library.presentation.viewmodel.FavoritesTracksScreenState
import com.agermolin.playlistmaker.library.presentation.viewmodel.FavoritesTracksViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesTabContent(
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesTracksViewModel = koinViewModel(),
) {
    val screenState by viewModel.screenState.observeAsState(FavoritesTracksScreenState.Empty)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        when (val state = screenState) {
            is FavoritesTracksScreenState.Empty -> {
                EmptyPlaceholder(
                    message = stringResource(R.string.media_library_empty),
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }

            is FavoritesTracksScreenState.Content -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = state.tracks,
                        key = { it.trackId },
                    ) { track ->
                        TrackListItem(
                            track = track,
                            onClick = { onTrackClick(track) },
                        )
                    }
                }
            }
        }
    }
}
