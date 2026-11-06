package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SongEntity::class,
        LyricsDraftEntity::class,
        CreditTransactionEntity::class,
        PlaylistEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun lyricsDao(): LyricsDao
    abstract fun creditDao(): CreditDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mintu_ai_music.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
