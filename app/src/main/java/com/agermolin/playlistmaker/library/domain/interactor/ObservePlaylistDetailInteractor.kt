package com.agermolin.playlistmaker.library.domain.interactor

import com.agermolin.playlistmaker.library.domain.model.PlaylistDetailResult
import com.agermolin.playlistmaker.library.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

interface IObservePlaylistDetailInteractor {
    fun observePlaylistDetail(playlistId: Long): Flow<PlaylistDetailResult>
}

class ObservePlaylistDetailInteractor(
    private val playlistRepository: PlaylistRepository,
) : IObservePlaylistDetailInteractor {

    override fun observePlaylistDetail(playlistId: Long): Flow<PlaylistDetailResult> =
        playlistRepository.observePlaylistDetail(playlistId)
}
