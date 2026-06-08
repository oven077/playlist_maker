package com.agermolin.playlistmaker.library.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.entity.Track
import com.agermolin.playlistmaker.core.presentation.component.PlaylistMakerTopBar
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayMedium
import com.agermolin.playlistmaker.library.domain.model.Playlist
import com.agermolin.playlistmaker.library.presentation.component.FavoritesTabContent
import com.agermolin.playlistmaker.library.presentation.component.PlaylistsTabContent
import kotlinx.coroutines.launch

private const val PAGE_FAVORITES = 0
private const val PAGE_PLAYLISTS = 1

@Composable
fun LibraryScreen(
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = PAGE_FAVORITES,
        pageCount = { 2 },
    )
    val coroutineScope = rememberCoroutineScope()

    val tabTitles = listOf(
        stringResource(R.string.favorites_tracks),
        stringResource(R.string.playlists),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.screen_background)),
    ) {
        PlaylistMakerTopBar(title = stringResource(R.string.library))

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = colorResource(R.color.screen_background),
            contentColor = colorResource(R.color.library_tab_text_selected),
            indicator = { tabPositions ->
                if (pagerState.currentPage < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = colorResource(R.color.library_tab_text_selected),
                    )
                }
            },
            divider = {},
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = YsDisplayMedium,
                            ),
                            color = colorResource(R.color.library_tab_text_selected),
                        )
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                PAGE_FAVORITES -> FavoritesTabContent(onTrackClick = onTrackClick)
                PAGE_PLAYLISTS -> PlaylistsTabContent(
                    onPlaylistClick = onPlaylistClick,
                    onNewPlaylistClick = onNewPlaylistClick,
                )
            }
        }
    }
}
