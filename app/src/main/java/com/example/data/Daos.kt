package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY createdAt DESC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE isInstrumental = 1 ORDER BY createdAt DESC")
    fun getInstrumentalSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    suspend fun getSongById(id: String): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("UPDATE songs SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: String, isFav: Boolean)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: String)

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getSongCount(): Int
}

@Dao
interface LyricsDao {
    @Query("SELECT * FROM lyrics_drafts ORDER BY createdAt DESC")
    fun getAllLyrics(): Flow<List<LyricsDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyrics(draft: LyricsDraftEntity)

    @Query("DELETE FROM lyrics_drafts WHERE id = :id")
    suspend fun deleteLyrics(id: String)
}

@Dao
interface CreditDao {
    @Query("SELECT * FROM credit_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CreditTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CreditTransactionEntity)
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: String)
}
