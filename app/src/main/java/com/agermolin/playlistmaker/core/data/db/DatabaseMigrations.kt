package com.agermolin.playlistmaker.core.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS playlists (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                coverImagePath TEXT,
                trackIdsJson TEXT NOT NULL,
                trackCount INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS playlist_tracks (
                trackId INTEGER NOT NULL PRIMARY KEY,
                artworkUrl100 TEXT NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                collectionName TEXT NOT NULL,
                releaseDate TEXT NOT NULL,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
                previewUrl TEXT NOT NULL,
                addedAt INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}
