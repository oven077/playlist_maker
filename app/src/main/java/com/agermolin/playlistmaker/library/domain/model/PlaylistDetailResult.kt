package com.agermolin.playlistmaker.library.domain.model

import com.agermolin.playlistmaker.core.entity.Track

sealed interface PlaylistDetailResult {
    data object NotFound : PlaylistDetailResult
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
    ) : PlaylistDetailResult
}
