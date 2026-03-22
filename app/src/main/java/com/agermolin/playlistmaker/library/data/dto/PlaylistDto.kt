package com.agermolin.playlistmaker.library.data.dto

data class PlaylistDto(
    val id: Long,
    val name: String,
    val description: String,
    val coverImagePath: String?,
    val trackIdsJson: String,
    val trackCount: Int,
)
