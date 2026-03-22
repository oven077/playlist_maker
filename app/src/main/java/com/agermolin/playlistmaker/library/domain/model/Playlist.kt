package com.agermolin.playlistmaker.library.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverImagePath: String?,
    val trackCount: Int,
)
