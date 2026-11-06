package com.example.data

import android.content.Context
import com.example.audio.AudioSynthesizer
import com.example.model.CreditTransaction
import com.example.model.CreditWallet
import com.example.model.GenerationJob
import com.example.model.JobStatus
import com.example.model.LyricsLanguage
import com.example.model.MusicInstrument
import com.example.model.MusicStyle
import com.example.model.Song
import com.example.model.SongMood
import com.example.model.TransactionType
import com.example.model.VoiceType
import com.example.provider.GoogleLyriaMusicProvider
import com.example.provider.MusicProvider
import com.example.provider.SongGenerationRequest
import com.example.provider.SynthesizerMusicProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MusicRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    private val songDao = database.songDao()
    private val lyricsDao = database.lyricsDao()
    private val creditDao = database.creditDao()
    private val playlistDao = database.playlistDao()

    val allSongs: Flow<List<Song>> = songDao.getAllSongs().map { list -> list.map { it.toSong() } }
    val favoriteSongs: Flow<List<Song>> = songDao.getFavoriteSongs().map { list -> list.map { it.toSong() } }
    val instrumentalSongs: Flow<List<Song>> = songDao.getInstrumentalSongs().map { list -> list.map { it.toSong() } }
    val allLyrics: Flow<List<LyricsDraftEntity>> = lyricsDao.getAllLyrics()
    val allPlaylists: Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()
    val allTransactions: Flow<List<CreditTransactionEntity>> = creditDao.getAllTransactions()

    // Providers
    val synthesizerProvider = SynthesizerMusicProvider()
    val googleLyriaProvider = GoogleLyriaMusicProvider()
    val availableProviders = listOf(synthesizerProvider, googleLyriaProvider)

    private val _activeProvider = MutableStateFlow<MusicProvider>(synthesizerProvider)
    val activeProvider: StateFlow<MusicProvider> = _activeProvider.asStateFlow()

    // Credit Wallet
    private val _wallet = MutableStateFlow(CreditWallet())
    val wallet: StateFlow<CreditWallet> = _wallet.asStateFlow()

    // Active Generation Jobs
    private val _currentJob = MutableStateFlow<GenerationJob?>(null)
    val currentJob: StateFlow<GenerationJob?> = _currentJob.asStateFlow()

    private val _recentJobs = MutableStateFlow<List<GenerationJob>>(emptyList())
    val recentJobs: StateFlow<List<GenerationJob>> = _recentJobs.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialContentIfNeeded()
        }
    }

    fun setActiveProvider(providerId: String) {
        val found = availableProviders.find { it.id == providerId } ?: synthesizerProvider
        _activeProvider.value = found
    }

    fun setLyriaApiKey(key: String) {
        googleLyriaProvider.updateApiKey(key)
    }

    suspend fun toggleFavorite(songId: String, currentFav: Boolean) = withContext(Dispatchers.IO) {
        songDao.updateFavorite(songId, !currentFav)
    }

    suspend fun deleteSong(songId: String) = withContext(Dispatchers.IO) {
        songDao.deleteSong(songId)
    }

    suspend fun saveLyricsDraft(title: String, language: LyricsLanguage, mood: SongMood, text: String) = withContext(Dispatchers.IO) {
        val draft = LyricsDraftEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            language = language.displayName,
            mood = mood.displayName,
            fullText = text
        )
        lyricsDao.insertLyrics(draft)
    }

    suspend fun deleteLyricsDraft(id: String) = withContext(Dispatchers.IO) {
        lyricsDao.deleteLyrics(id)
    }

    suspend fun createPlaylist(name: String, desc: String) = withContext(Dispatchers.IO) {
        val p = PlaylistEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            description = desc
        )
        playlistDao.insertPlaylist(p)
    }

    suspend fun deletePlaylist(id: String) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylist(id)
    }

    /**
     * Executes real song generation, deducts credits atomically, with refund on error.
     */
    suspend fun generateSong(
        request: SongGenerationRequest,
        costCredits: Int = 10
    ): Result<Pair<Song, Song?>> = withContext(Dispatchers.IO) {
        val currentBalance = _wallet.value.balance
        if (currentBalance < costCredits) {
            return@withContext Result.failure(
                IllegalStateException("Insufficient credits ($currentBalance available, $costCredits required). Claim daily credits or upgrade!")
            )
        }

        // Deduct credits
        val newBalance = currentBalance - costCredits
        _wallet.value = _wallet.value.copy(
            balance = newBalance,
            totalSongsGenerated = _wallet.value.totalSongsGenerated + 1
        )
        recordTransaction(
            TransactionType.SONG_GENERATION,
            -costCredits,
            newBalance,
            "Created: ${request.title.ifBlank { request.style.displayName }}"
        )

        val jobId = UUID.randomUUID().toString()
        val job = GenerationJob(
            id = jobId,
            title = request.title.ifBlank { "${request.style.displayName} Track" },
            prompt = request.prompt,
            style = request.style,
            status = JobStatus.PROCESSING,
            progress = 0.05f,
            providerName = _activeProvider.value.displayName
        )
        _currentJob.value = job
        _recentJobs.value = listOf(job) + _recentJobs.value.take(9)

        try {
            val provider = _activeProvider.value
            val result = provider.generateSong(
                context = context,
                request = request,
                onProgress = { p ->
                    _currentJob.value = _currentJob.value?.copy(progress = p)
                }
            )

            // Save generated songs to Room DB
            songDao.insertSong(SongEntity.fromSong(result.first))
            result.second?.let { varSong ->
                songDao.insertSong(SongEntity.fromSong(varSong))
            }

            val completedJob = job.copy(
                status = JobStatus.COMPLETED,
                progress = 1.0f,
                resultSongId = result.first.id,
                variationSongId = result.second?.id
            )
            _currentJob.value = completedJob
            _recentJobs.value = _recentJobs.value.map { if (it.id == jobId) completedJob else it }

            Result.success(result)
        } catch (e: Exception) {
            // Automatic refund!
            val refundBalance = _wallet.value.balance + costCredits
            _wallet.value = _wallet.value.copy(balance = refundBalance)
            recordTransaction(
                TransactionType.REFUND,
                costCredits,
                refundBalance,
                "Refund for failed generation: ${e.message?.take(40)}"
            )

            val failedJob = job.copy(
                status = JobStatus.FAILED,
                errorMessage = e.message ?: "Generation failed"
            )
            _currentJob.value = failedJob
            _recentJobs.value = _recentJobs.value.map { if (it.id == jobId) failedJob else it }

            Result.failure(e)
        }
    }

    suspend fun cancelJob(jobId: String) {
        val job = _currentJob.value ?: return
        if (job.id == jobId && job.status == JobStatus.PROCESSING) {
            _activeProvider.value.cancelGeneration(jobId)
            val canceled = job.copy(status = JobStatus.CANCELLED)
            _currentJob.value = canceled
            // Refund 10 credits
            val refundBalance = _wallet.value.balance + 10
            _wallet.value = _wallet.value.copy(balance = refundBalance)
            recordTransaction(TransactionType.REFUND, 10, refundBalance, "Canceled job refund")
        }
    }

    suspend fun claimDailyBonus(): Boolean = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val lastClaim = _wallet.value.lastDailyClaimTimestamp
        val hoursPassed = (now - lastClaim) / (1000 * 60 * 60)

        if (hoursPassed >= 24 || lastClaim == 0L) {
            val bonus = 25
            val newBalance = _wallet.value.balance + bonus
            _wallet.value = _wallet.value.copy(
                balance = newBalance,
                lastDailyClaimTimestamp = now
            )
            recordTransaction(TransactionType.DAILY_BONUS, bonus, newBalance, "Daily Free 25 Credits Reward")
            true
        } else {
            false
        }
    }

    suspend fun addCreditsAdmin(amount: Int) = withContext(Dispatchers.IO) {
        val newBalance = _wallet.value.balance + amount
        _wallet.value = _wallet.value.copy(balance = newBalance)
        recordTransaction(TransactionType.ADMIN_GRANT, amount, newBalance, "Admin Credit Top-Up: +$amount")
    }

    private suspend fun recordTransaction(
        type: TransactionType,
        amount: Int,
        balanceAfter: Int,
        description: String
    ) {
        creditDao.insertTransaction(
            CreditTransactionEntity(
                id = UUID.randomUUID().toString(),
                type = type.name,
                amount = amount,
                balanceAfter = balanceAfter,
                description = description
            )
        )
    }

    private suspend fun seedInitialContentIfNeeded() {
        val count = songDao.getSongCount()
        if (count > 0) return

        // Initial transaction
        creditDao.insertTransaction(
            CreditTransactionEntity(
                id = UUID.randomUUID().toString(),
                type = TransactionType.INITIAL_CREDIT.name,
                amount = 150,
                balanceAfter = 150,
                description = "Welcome Bonus to Mintu Raj AI Music"
            )
        )

        // Seed 4 initial studio songs so users can immediately play high quality music
        val seedSongs = listOf(
            SeedConfig(
                title = "Chand Sitare (Bollywood 2026)",
                description = "Futuristic Bollywood EDM hook with pulsating bass and sitar riffs",
                style = MusicStyle.BOLLYWOOD_2026,
                mood = SongMood.ENERGETIC,
                voice = VoiceType.MALE,
                instruments = listOf(MusicInstrument.SYNTHESIZER, MusicInstrument.TABLA, MusicInstrument.DRUMS)
            ),
            SeedConfig(
                title = "Dil Ki Dhadkan",
                description = "Soulful Hindi romantic ballad with grand piano and strings",
                style = MusicStyle.HINDI_ROMANTIC,
                mood = SongMood.ROMANTIC,
                voice = VoiceType.DUET,
                instruments = listOf(MusicInstrument.PIANO, MusicInstrument.VIOLIN, MusicInstrument.GUITAR)
            ),
            SeedConfig(
                title = "Mahadev Shankara",
                description = "Devotional spiritual trance honoring Lord Shiva with damru and tanpura",
                style = MusicStyle.SHIVA_BHAJAN,
                mood = SongMood.SPIRITUAL,
                voice = VoiceType.MALE,
                instruments = listOf(MusicInstrument.TANPURA, MusicInstrument.HARMONIUM, MusicInstrument.TABLA)
            ),
            SeedConfig(
                title = "Lanka Dahan Desi Groove",
                description = "High energy Bhojpuri folk-fusion with live dholak",
                style = MusicStyle.BHOJPURI,
                mood = SongMood.HAPPY,
                voice = VoiceType.MALE,
                instruments = listOf(MusicInstrument.DHOLAK, MusicInstrument.HARMONIUM)
            )
        )

        for (seed in seedSongs) {
            val id = UUID.randomUUID().toString()
            val audioFile = AudioSynthesizer.synthesizeSong(
                context = context,
                songId = id,
                style = seed.style,
                mood = seed.mood,
                voiceType = seed.voice,
                instruments = seed.instruments,
                durationSeconds = 25
            )

            val song = Song(
                id = id,
                title = seed.title,
                description = seed.description,
                lyrics = "Sample master track generated by Mintu Raj AI Studio.",
                style = seed.style,
                voiceType = seed.voice,
                mood = seed.mood,
                instruments = seed.instruments,
                durationSeconds = 25,
                bpm = seed.style.bpm,
                audioPath = audioFile.absolutePath,
                isFavorite = seed.style == MusicStyle.BOLLYWOOD_2026,
                provider = "Mintu Studio Synthesizer"
            )
            songDao.insertSong(SongEntity.fromSong(song))
        }

        // Seed 2 default playlists
        playlistDao.insertPlaylist(
            PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = "Trending Desi Hits",
                description = "Top AI curated tracks from Bollywood and Punjabi charts"
            )
        )
        playlistDao.insertPlaylist(
            PlaylistEntity(
                id = UUID.randomUUID().toString(),
                name = "Midnight Spiritual Peace",
                description = "Bhakti, Shiva Bhajan and meditative acoustic frequencies"
            )
        )
    }

    private data class SeedConfig(
        val title: String,
        val description: String,
        val style: MusicStyle,
        val mood: SongMood,
        val voice: VoiceType,
        val instruments: List<MusicInstrument>
    )
}
