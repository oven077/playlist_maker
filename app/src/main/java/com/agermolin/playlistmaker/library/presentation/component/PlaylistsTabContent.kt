package com.agermolin.playlistmaker.library.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.presentation.component.EmptyPlaceholder
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayMedium
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.library.presentation.viewmodel.PlaylistsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsTabContent(
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistsViewModel = koinViewModel(),
) {
    val playlists by viewModel.playlists.observeAsState(emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Button(
            onClick = onNewPlaylistClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.button_background_color),
                contentColor = colorResource(R.color.button_text_color),
            ),
        ) {
            Text(
                text = stringResource(R.string.new_playlist),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = YsDisplayMedium,
                ),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (playlists.isEmpty()) {
                EmptyPlaceholder(
                    message = stringResource(R.string.no_playlists),
                    topPadding = 24.dp,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                ) {
                    items(
                        items = playlists,
                        key = { it.id },
                    ) { playlist ->
                        PlaylistGridItem(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist) },
                        )
                    }
                }
            }
        }
    }
}
