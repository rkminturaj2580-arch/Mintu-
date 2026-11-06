package com.example.model

enum class MusicStyle(val displayName: String, val bpm: Int, val description: String) {
    BOLLYWOOD_2026("Bollywood 2026", 118, "Futuristic modern desi beats with lush synths and brass"),
    HINDI_ROMANTIC("Hindi Romantic", 82, "Acoustic piano, warm strings & soul-touching melody"),
    HINDI_SAD("Hindi Sad", 74, "Deep melancholic violin, bansuri flute and emotive chords"),
    BHOJPURI("Bhojpuri", 130, "High energy dholak, harmonium, and festive energetic groove"),
    BHAKTI_BHAJAN("Bhakti Bhajan", 84, "Spiritual tanpura drone, temple bells, and peaceful harmonium"),
    DEVOTIONAL("Devotional", 88, "Uplifting devotional choir and divine traditional instruments"),
    CHHATH_PUJA("Chhath Puja", 92, "Traditional folk ghata melodies honoring Surya and Chhathi Maiya"),
    SHIVA_BHAJAN("Shiva Bhajan", 108, "Mystic damru rhythm, shankha drone, powerful trance rhythm"),
    PUNJABI("Punjabi", 128, "Punchy dhol, tumbi riffs, and urban desi bass drops"),
    TAMIL("Tamil", 124, "Energetic kuthu percussion and contemporary southern melody"),
    POP("Pop", 116, "Crisp catchy vocal hooks, punchy drums and bright synths"),
    ROCK("Rock", 126, "Overdriven electric guitar riffs and pounding drum kit"),
    HIP_HOP("Hip-Hop", 90, "Deep 808 sub bass, trap hats and smooth rhythmic groove"),
    RAP("Rap", 96, "Fast syncopated flow, hard-hitting kick and street synth hooks"),
    EDM("EDM", 128, "Festival build-ups, pumping sidechain bass and euphoric leads"),
    CLASSICAL("Classical", 80, "Indian classical raga scales, sitar flourishes, and tabla taal"),
    FOLK("Folk", 100, "Acoustic earthen melodies, flute, and traditional folk rhythms")
}

enum class VoiceType(val displayName: String, val iconName: String) {
    MALE("Male Vocal", "mic"),
    FEMALE("Female Vocal", "mic_external_on"),
    DUET("Duet Vocal", "group"),
    INSTRUMENTAL("Instrumental Only", "piano")
}

enum class SongMood(val displayName: String) {
    ROMANTIC("Romantic"),
    EMOTIONAL("Emotional"),
    SAD("Sad"),
    HAPPY("Happy"),
    ENERGETIC("Energetic"),
    SPIRITUAL("Spiritual"),
    MOTIVATIONAL("Motivational"),
    CINEMATIC("Cinematic")
}

enum class MusicInstrument(val displayName: String) {
    PIANO("Piano"),
    GUITAR("Guitar"),
    FLUTE("Flute (Bansuri)"),
    TABLA("Tabla"),
    DHOLAK("Dholak"),
    HARMONIUM("Harmonium"),
    DRUMS("Drums"),
    VIOLIN("Violin"),
    SYNTHESIZER("Synthesizer"),
    TANPURA("Tanpura")
}

enum class LyricsLanguage(val displayName: String, val code: String) {
    HINDI("Hindi", "hi"),
    BHOJPURI("Bhojpuri", "bho"),
    ENGLISH("English", "en"),
    PUNJABI("Punjabi", "pa"),
    TAMIL("Tamil", "ta"),
    BHAKTI("Bhakti / Sanskritized", "sa")
}

data class Song(
    val id: String,
    val title: String,
    val description: String,
    val lyrics: String,
    val style: MusicStyle,
    val voiceType: VoiceType,
    val mood: SongMood,
    val instruments: List<MusicInstrument>,
    val durationSeconds: Int,
    val bpm: Int,
    val audioPath: String,
    val isFavorite: Boolean = false,
    val isInstrumental: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val provider: String = "Studio Synthesizer",
    val variationTag: String? = null,
    val playCount: Int = 0
)

data class LyricsStructure(
    val title: String,
    val language: LyricsLanguage,
    val mood: SongMood,
    val intro: String,
    val verse1: String,
    val preChorus: String,
    val chorus: String,
    val verse2: String,
    val bridge: String,
    val finalChorus: String,
    val outro: String
) {
    fun toFullText(): String {
        return buildString {
            appendLine("[Title: $title]")
            appendLine("[Mood: ${mood.displayName} | Language: ${language.displayName}]")
            appendLine()
            appendLine("[Intro]")
            appendLine(intro)
            appendLine()
            appendLine("[Verse 1]")
            appendLine(verse1)
            appendLine()
            appendLine("[Pre-Chorus]")
            appendLine(preChorus)
            appendLine()
            appendLine("[Chorus]")
            appendLine(chorus)
            appendLine()
            appendLine("[Verse 2]")
            appendLine(verse2)
            appendLine()
            appendLine("[Bridge]")
            appendLine(bridge)
            appendLine()
            appendLine("[Final Chorus]")
            appendLine(finalChorus)
            appendLine()
            appendLine("[Outro]")
            appendLine(outro)
        }
    }
}

enum class JobStatus {
    QUEUED, PROCESSING, COMPLETED, FAILED, CANCELLED
}

data class GenerationJob(
    val id: String,
    val title: String,
    val prompt: String,
    val style: MusicStyle,
    val status: JobStatus,
    val progress: Float, // 0.0 to 1.0
    val resultSongId: String? = null,
    val variationSongId: String? = null,
    val providerName: String,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class CreditWallet(
    val balance: Int = 150,
    val lastDailyClaimTimestamp: Long = 0L,
    val isPremium: Boolean = false,
    val totalSongsGenerated: Int = 0
)

enum class TransactionType {
    INITIAL_CREDIT,
    DAILY_BONUS,
    SONG_GENERATION,
    INSTRUMENTAL_GENERATION,
    VARIATION_GENERATION,
    STEM_SEPARATION,
    AUDIO_EXTENSION,
    REFUND,
    ADMIN_GRANT
}

data class CreditTransaction(
    val id: String,
    val type: TransactionType,
    val amount: Int,
    val balanceAfter: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class StemTrack(
    val name: String,
    val isMuted: Boolean = false,
    val isSolo: Boolean = false,
    val volume: Float = 1.0f
)

data class UserAccount(
    val id: String = "usr_minturaj_01",
    val name: String = "Mintu Raj",
    val email: String = "rkminturaj2580@gmail.com",
    val isPremium: Boolean = true,
    val joinDate: String = "November 2026",
    val songsCreated: Int = 12
)

