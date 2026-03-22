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
