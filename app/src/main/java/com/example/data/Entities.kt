package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.LyricsLanguage
import com.example.model.MusicInstrument
import com.example.model.MusicStyle
import com.example.model.Song
import com.example.model.SongMood
import com.example.model.VoiceType

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val lyrics: String,
    val style: String,
    val voiceType: String,
    val mood: String,
    val instruments: String, // comma-separated
    val durationSeconds: Int,
    val bpm: Int,
    val audioPath: String,
    val isFavorite: Boolean = false,
    val isInstrumental: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val provider: String = "Studio Synthesizer",
    val variationTag: String? = null,
    val playCount: Int = 0
) {
    fun toSong(): Song {
        val s = try { MusicStyle.valueOf(style) } catch (_: Exception) { MusicStyle.BOLLYWOOD_2026 }
        val v = try { VoiceType.valueOf(voiceType) } catch (_: Exception) { VoiceType.MALE }
        val m = try { SongMood.valueOf(mood) } catch (_: Exception) { SongMood.ROMANTIC }
        val instList = instruments.split(",")
            .mapNotNull { name ->
                try { MusicInstrument.valueOf(name.trim()) } catch (_: Exception) { null }
            }

        return Song(
            id = id,
            title = title,
            description = description,
            lyrics = lyrics,
            style = s,
            voiceType = v,
            mood = m,
            instruments = instList,
            durationSeconds = durationSeconds,
            bpm = bpm,
            audioPath = audioPath,
            isFavorite = isFavorite,
            isInstrumental = isInstrumental,
            createdAt = createdAt,
            provider = provider,
            variationTag = variationTag,
            playCount = playCount
        )
    }

    companion object {
        fun fromSong(song: Song): SongEntity {
            return SongEntity(
                id = song.id,
                title = song.title,
                description = song.description,
                lyrics = song.lyrics,
                style = song.style.name,
                voiceType = song.voiceType.name,
                mood = song.mood.name,
                instruments = song.instruments.joinToString(",") { it.name },
                durationSeconds = song.durationSeconds,
                bpm = song.bpm,
                audioPath = song.audioPath,
                isFavorite = song.isFavorite,
                isInstrumental = song.isInstrumental,
                createdAt = song.createdAt,
                provider = song.provider,
                variationTag = song.variationTag,
                playCount = song.playCount
            )
        }
    }
}

@Entity(tableName = "lyrics_drafts")
data class LyricsDraftEntity(
    @PrimaryKey val id: String,
    val title: String,
    val language: String,
    val mood: String,
    val fullText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "credit_transactions")
data class CreditTransactionEntity(
    @PrimaryKey val id: String,
    val type: String,
    val amount: Int,
    val balanceAfter: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)
