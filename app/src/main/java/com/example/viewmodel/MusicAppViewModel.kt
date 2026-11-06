package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.LyricsEngine
import com.example.audio.AudioSynthesizer
import com.example.audio.MusicPlayerManager
import com.example.audio.RepeatMode
import com.example.data.AppDatabase
import com.example.data.CreditTransactionEntity
import com.example.data.LyricsDraftEntity
import com.example.data.MusicRepository
import com.example.data.PlaylistEntity
import com.example.model.CreditTransaction
import com.example.model.CreditWallet
import com.example.model.GenerationJob
import com.example.model.LyricsLanguage
import com.example.model.LyricsStructure
import com.example.model.MusicInstrument
import com.example.model.MusicStyle
import com.example.model.Song
import com.example.model.SongMood
import com.example.model.StemTrack
import com.example.model.VoiceType
import com.example.provider.MusicProvider
import com.example.provider.SongGenerationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

enum class AppScreen {
    HOME, CREATE, LYRICS, EXPLORE, STUDIO, LIBRARY, PROFILE
}

class MusicAppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = MusicRepository(application, database)
    val playerManager = MusicPlayerManager(application)

    // Current Screen Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Screen Backstack for BackHandler
    private val screenBackStack = mutableListOf(AppScreen.HOME)

    // Data streams from Room
    val allSongs: StateFlow<List<Song>> = repository.allSongs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val favoriteSongs: StateFlow<List<Song>> = repository.favoriteSongs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val instrumentalSongs: StateFlow<List<Song>> = repository.instrumentalSongs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allLyrics: StateFlow<List<LyricsDraftEntity>> = repository.allLyrics.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allPlaylists: StateFlow<List<PlaylistEntity>> = repository.allPlaylists.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val transactions: StateFlow<List<CreditTransactionEntity>> = repository.allTransactions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Wallet and Providers
    val wallet: StateFlow<CreditWallet> = repository.wallet
    val activeProvider: StateFlow<MusicProvider> = repository.activeProvider
    val availableProviders: List<MusicProvider> = repository.availableProviders
    val currentJob: StateFlow<GenerationJob?> = repository.currentJob
    val recentJobs: StateFlow<List<GenerationJob>> = repository.recentJobs

    // Player State
    val currentSong: StateFlow<Song?> = playerManager.currentSong
    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying
    val currentPositionMs: StateFlow<Int> = playerManager.currentPositionMs
    val durationMs: StateFlow<Int> = playerManager.durationMs
    val playbackSpeed: StateFlow<Float> = playerManager.playbackSpeed
    val repeatMode: StateFlow<RepeatMode> = playerManager.repeatMode
    val isShuffled: StateFlow<Boolean> = playerManager.isShuffled
    val waveformAmplitudes: StateFlow<List<Float>> = playerManager.waveformAmplitudes

    // Full Player Modal Sheet
    private val _isPlayerExpanded = MutableStateFlow(false)
    val isPlayerExpanded: StateFlow<Boolean> = _isPlayerExpanded.asStateFlow()

    // ----------------------------------------------------
    // Create Song Screen State
    // ----------------------------------------------------
    val createTitle = MutableStateFlow("")
    val createDescription = MutableStateFlow("")
    val createLyrics = MutableStateFlow("")
    val createStyle = MutableStateFlow(MusicStyle.BOLLYWOOD_2026)
    val createVoice = MutableStateFlow(VoiceType.MALE)
    val createMood = MutableStateFlow(SongMood.ROMANTIC)
    val createInstruments = MutableStateFlow(
        listOf(MusicInstrument.SYNTHESIZER, MusicInstrument.TABLA, MusicInstrument.PIANO)
    )
    val createDuration = MutableStateFlow(30)
    val createBpm = MutableStateFlow(118)
    val isGenerating = MutableStateFlow(false)
    val generateVariations = MutableStateFlow(true)
    val generationError = MutableStateFlow<String?>(null)

    // ----------------------------------------------------
    // AI Lyrics Writer Screen State
    // ----------------------------------------------------
    val lyricsTitle = MutableStateFlow("")
    val lyricsPrompt = MutableStateFlow("")
    val lyricsLanguage = MutableStateFlow(LyricsLanguage.HINDI)
    val lyricsMood = MutableStateFlow(SongMood.ROMANTIC)
    val currentLyricsStructure = MutableStateFlow<LyricsStructure?>(null)
    val editableLyricsText = MutableStateFlow("")

    // ----------------------------------------------------
    // Audio Studio State
    // ----------------------------------------------------
    val studioSelectedSong = MutableStateFlow<Song?>(null)
    val studioStems = MutableStateFlow(
        listOf(
            StemTrack("Vocals / Lead", isMuted = false, isSolo = false),
            StemTrack("Harmony & Chords", isMuted = false, isSolo = false),
            StemTrack("Bassline", isMuted = false, isSolo = false),
            StemTrack("Drums & Percussion", isMuted = false, isSolo = false)
        )
    )
    val trimStartFraction = MutableStateFlow(0f)
    val trimEndFraction = MutableStateFlow(1f)
    val studioActionMessage = MutableStateFlow<String?>(null)

    // Admin & Cloud Docs Dialog State
    val showSupabaseArchitectureDialog = MutableStateFlow(false)
    val showAdminConfigDialog = MutableStateFlow(false)
    val lyriaApiKeyInput = MutableStateFlow("")

    // Auth, Account & Privacy Policy States
    val userAccount = MutableStateFlow(com.example.model.UserAccount())
    val isLoggedIn = MutableStateFlow(true)
    val showAuthDialog = MutableStateFlow(false)
    val showPrivacyPolicyDialog = MutableStateFlow(false)
    val showDeleteAccountDialog = MutableStateFlow(false)
    val isNetworkConnected = MutableStateFlow(true)

    init {
        checkNetworkStatus()
        // Generate an initial lyric draft preview
        generateLyrics()
    }

    fun checkNetworkStatus(): Boolean {
        return try {
            val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
            val caps = cm?.getNetworkCapabilities(cm.activeNetwork)
            val connected = caps != null && (
                caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)
            )
            isNetworkConnected.value = connected
            connected
        } catch (_: Exception) {
            isNetworkConnected.value = true
            true
        }
    }

    fun login(email: String, name: String) {
        userAccount.value = userAccount.value.copy(
            email = email.ifBlank { "user@minturaj.ai" },
            name = name.ifBlank { "Mintu Music Creator" }
        )
        isLoggedIn.value = true
        showAuthDialog.value = false
        Toast.makeText(getApplication(), "Logged in as ${userAccount.value.name}!", Toast.LENGTH_SHORT).show()
    }

    fun logout() {
        isLoggedIn.value = false
        Toast.makeText(getApplication(), "Logged out. Switched to Guest Mode.", Toast.LENGTH_SHORT).show()
    }

    fun deleteAccountAndData() {
        viewModelScope.launch {
            try {
                allSongs.value.forEach { song ->
                    repository.deleteSong(song.id)
                }
                allLyrics.value.forEach { draft ->
                    repository.deleteLyricsDraft(draft.id)
                }
                allPlaylists.value.forEach { p ->
                    repository.deletePlaylist(p.id)
                }
                userAccount.value = com.example.model.UserAccount(name = "Guest User", email = "guest@minturaj.ai")
                isLoggedIn.value = false
                showDeleteAccountDialog.value = false
                Toast.makeText(getApplication(), "Account & all personal data deleted per Play Store policy.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Error during account deletion: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (_isPlayerExpanded.value) {
            _isPlayerExpanded.value = false
            return true
        }
        if (screenBackStack.size > 1) {
            screenBackStack.removeAt(screenBackStack.size - 1)
            _currentScreen.value = screenBackStack.last()
            return true
        }
        return false
    }

    fun setPlayerExpanded(expanded: Boolean) {
        _isPlayerExpanded.value = expanded
    }

    fun playSong(song: Song, playlist: List<Song>? = null) {
        playerManager.playSong(song, playlist)
        studioSelectedSong.value = song
    }

    fun togglePlayPause() = playerManager.togglePlayPause()
    fun seekTo(posMs: Int) = playerManager.seekTo(posMs)
    fun setPlaybackSpeed(speed: Float) = playerManager.setSpeed(speed)
    fun toggleRepeat() = playerManager.toggleRepeat()
    fun toggleShuffle() = playerManager.toggleShuffle()
    fun playNext() = playerManager.playNext()
    fun playPrevious() = playerManager.playPrevious()

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song.id, song.isFavorite)
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            repository.deleteSong(song.id)
            if (currentSong.value?.id == song.id) {
                playerManager.togglePlayPause()
            }
        }
    }

    fun claimDailyCredits() {
        viewModelScope.launch {
            val success = repository.claimDailyBonus()
            val msg = if (success) "Claimed +25 Daily Free Credits!" else "Daily bonus already claimed for today! Come back in 24 hrs."
            Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun addAdminCredits(amount: Int) {
        viewModelScope.launch {
            repository.addCreditsAdmin(amount)
            Toast.makeText(getApplication(), "Granted +$amount Credits successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setActiveProvider(providerId: String) {
        repository.setActiveProvider(providerId)
    }

    fun saveLyriaApiKey(key: String) {
        repository.setLyriaApiKey(key)
        Toast.makeText(getApplication(), "Lyria API Key updated successfully!", Toast.LENGTH_SHORT).show()
    }

    // ----------------------------------------------------
    // Song Creation Execution
    // ----------------------------------------------------
    fun startSongGeneration() {
        val title = createTitle.value.ifBlank { "${createStyle.value.displayName} Song" }
        val prompt = createDescription.value
        val lyrics = createLyrics.value.ifBlank { "Instrumental and vocal composition generated by Mintu Raj AI." }

        val request = SongGenerationRequest(
            title = title,
            prompt = prompt,
            lyrics = lyrics,
            style = createStyle.value,
            voiceType = createVoice.value,
            mood = createMood.value,
            instruments = createInstruments.value,
            durationSeconds = createDuration.value,
            bpm = createBpm.value,
            generateVariations = generateVariations.value
        )

        isGenerating.value = true
        generationError.value = null

        viewModelScope.launch {
            val result = repository.generateSong(request)
            isGenerating.value = false

            result.fold(
                onSuccess = { (mainSong, varSong) ->
                    Toast.makeText(getApplication(), "Song Created: ${mainSong.title}", Toast.LENGTH_SHORT).show()
                    // Auto play newly created song
                    playSong(mainSong, allSongs.value)
                    navigateTo(AppScreen.HOME)
                },
                onFailure = { error ->
                    generationError.value = error.message
                    Toast.makeText(getApplication(), error.message ?: "Generation error", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    fun cancelActiveJob(jobId: String) {
        viewModelScope.launch {
            repository.cancelJob(jobId)
            isGenerating.value = false
            Toast.makeText(getApplication(), "Generation canceled & credits refunded", Toast.LENGTH_SHORT).show()
        }
    }

    // ----------------------------------------------------
    // AI Lyrics Methods
    // ----------------------------------------------------
    fun generateLyrics() {
        val structure = LyricsEngine.generateLyrics(
            title = lyricsTitle.value,
            themePrompt = lyricsPrompt.value,
            language = lyricsLanguage.value,
            mood = lyricsMood.value
        )
        currentLyricsStructure.value = structure
        editableLyricsText.value = structure.toFullText()
        if (lyricsTitle.value.isBlank()) {
            lyricsTitle.value = structure.title
        }
    }

    fun rewriteLyricsLine(currentLine: String) {
        val newLine = LyricsEngine.rewriteLine(currentLine, lyricsMood.value)
        editableLyricsText.value = editableLyricsText.value.replaceFirst(currentLine, newLine)
        Toast.makeText(getApplication(), "Line rewritten with AI!", Toast.LENGTH_SHORT).show()
    }

    fun saveLyricsDraft() {
        viewModelScope.launch {
            val title = lyricsTitle.value.ifBlank { "Untitled Lyrics" }
            repository.saveLyricsDraft(
                title = title,
                language = lyricsLanguage.value,
                mood = lyricsMood.value,
                text = editableLyricsText.value
            )
            Toast.makeText(getApplication(), "Lyrics saved to Library Drafts!", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendLyricsToSongGenerator() {
        createTitle.value = lyricsTitle.value
        createLyrics.value = editableLyricsText.value
        createMood.value = lyricsMood.value
        when (lyricsLanguage.value) {
            LyricsLanguage.BHOJPURI -> createStyle.value = MusicStyle.BHOJPURI
            LyricsLanguage.PUNJABI -> createStyle.value = MusicStyle.PUNJABI
            LyricsLanguage.TAMIL -> createStyle.value = MusicStyle.TAMIL
            LyricsLanguage.BHAKTI -> createStyle.value = MusicStyle.BHAKTI_BHAJAN
            LyricsLanguage.ENGLISH -> createStyle.value = MusicStyle.POP
            LyricsLanguage.HINDI -> {
                createStyle.value = if (lyricsMood.value == SongMood.ROMANTIC) {
                    MusicStyle.HINDI_ROMANTIC
                } else if (lyricsMood.value == SongMood.SAD) {
                    MusicStyle.HINDI_SAD
                } else {
                    MusicStyle.BOLLYWOOD_2026
                }
            }
        }
        navigateTo(AppScreen.CREATE)
        Toast.makeText(getApplication(), "Lyrics loaded into Song Generator!", Toast.LENGTH_SHORT).show()
    }

    // ----------------------------------------------------
    // Audio Studio Operations
    // ----------------------------------------------------
    fun toggleStemMute(index: Int) {
        val list = studioStems.value.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(isMuted = !list[index].isMuted)
            studioStems.value = list
            studioActionMessage.value = "${list[index].name} ${if (list[index].isMuted) "Muted" else "Unmuted"}"
        }
    }

    fun toggleStemSolo(index: Int) {
        val list = studioStems.value.toMutableList()
        if (index in list.indices) {
            val current = list[index].isSolo
            list.forEachIndexed { i, s ->
                list[i] = s.copy(isSolo = if (i == index) !current else false)
            }
            studioStems.value = list
            studioActionMessage.value = if (!current) "Soloing ${list[index].name}" else "Solo turned off"
        }
    }

    fun trimAudio() {
        val song = studioSelectedSong.value ?: return
        val sourceFile = File(song.audioPath)
        val targetFile = File(sourceFile.parentFile, "trimmed_${System.currentTimeMillis()}_${sourceFile.name}")

        viewModelScope.launch {
            studioActionMessage.value = "Processing audio trim..."
            val success = AudioSynthesizer.trimAudio(
                context = getApplication(),
                sourceFile = sourceFile,
                targetFile = targetFile,
                startFraction = trimStartFraction.value,
                endFraction = trimEndFraction.value
            )
            if (success) {
                val newDuration = ((trimEndFraction.value - trimStartFraction.value) * song.durationSeconds).toInt().coerceAtLeast(5)
                val trimmedSong = song.copy(
                    id = UUID.randomUUID().toString(),
                    title = "${song.title} (Trimmed)",
                    durationSeconds = newDuration,
                    audioPath = targetFile.absolutePath,
                    variationTag = "Trimmed"
                )
                database.songDao().insertSong(com.example.data.SongEntity.fromSong(trimmedSong))
                studioSelectedSong.value = trimmedSong
                playSong(trimmedSong)
                studioActionMessage.value = "Audio trimmed and saved as new track!"
            } else {
                studioActionMessage.value = "Failed to trim audio."
            }
        }
    }

    fun extendAudio(additionalSeconds: Int = 15) {
        val song = studioSelectedSong.value ?: return
        viewModelScope.launch {
            studioActionMessage.value = "Extending track by +${additionalSeconds}s..."
            val extendedSong = repository.synthesizerProvider.extendSong(
                context = getApplication(),
                originalSong = song,
                additionalSeconds = additionalSeconds
            )
            database.songDao().insertSong(com.example.data.SongEntity.fromSong(extendedSong))
            studioSelectedSong.value = extendedSong
            playSong(extendedSong)
            studioActionMessage.value = "Track successfully extended to ${extendedSong.durationSeconds}s!"
        }
    }

    fun remixTrack(newStyle: MusicStyle) {
        val song = studioSelectedSong.value ?: return
        viewModelScope.launch {
            studioActionMessage.value = "Remixing into ${newStyle.displayName}..."
            val remixedId = UUID.randomUUID().toString()
            val file = AudioSynthesizer.synthesizeSong(
                context = getApplication(),
                songId = remixedId,
                style = newStyle,
                mood = song.mood,
                voiceType = song.voiceType,
                instruments = song.instruments,
                durationSeconds = song.durationSeconds
            )
            val remixedSong = song.copy(
                id = remixedId,
                title = "${song.title} (${newStyle.displayName} Remix)",
                style = newStyle,
                bpm = newStyle.bpm,
                audioPath = file.absolutePath,
                variationTag = "Remix"
            )
            database.songDao().insertSong(com.example.data.SongEntity.fromSong(remixedSong))
            studioSelectedSong.value = remixedSong
            playSong(remixedSong)
            studioActionMessage.value = "Remixed into ${newStyle.displayName}!"
        }
    }

    fun shareAudio(song: Song) {
        val context = getApplication<Application>()
        val file = File(song.audioPath)
        if (!file.exists()) {
            Toast.makeText(context, "Audio file not found on disk", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/wav"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Listen to ${song.title} - Mintu Raj AI Music")
                putExtra(Intent.EXTRA_TEXT, "Created on Mintu Raj AI Music: ${song.title} (${song.style.displayName})")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Track").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Unable to share audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
