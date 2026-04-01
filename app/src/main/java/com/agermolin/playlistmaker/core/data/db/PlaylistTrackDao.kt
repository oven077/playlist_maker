package com.agermolin.playlistmaker.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: PlaylistTrackEntity): Long

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<PlaylistTrackEntity>
}
