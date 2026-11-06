package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

enum class RepeatMode {
    OFF, ALL, ONE
}

class MusicPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: StateFlow<Boolean> = _isShuffled.asStateFlow()

    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()

    private val _waveformAmplitudes = MutableStateFlow<List<Float>>(List(32) { 0.1f })
    val waveformAmplitudes: StateFlow<List<Float>> = _waveformAmplitudes.asStateFlow()

    fun setPlaylist(songs: List<Song>) {
        _playlist.value = songs
    }

    fun playSong(song: Song, playlistContext: List<Song>? = null) {
        if (playlistContext != null) {
            _playlist.value = playlistContext
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = null

            val file = File(song.audioPath)
            if (!file.exists()) {
                // Synthesize if missing on disk
                return
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener { mp ->
                    _durationMs.value = mp.duration.coerceAtLeast(1)
                    applyPlaybackSpeed(_playbackSpeed.value)
                    mp.start()
                    _isPlaying.value = true
                    startProgressTracking()
                }
                setOnCompletionListener {
                    handleTrackCompletion()
                }
                setOnErrorListener { _, _, _ ->
                    _isPlaying.value = false
                    stopProgressTracking()
                    true
                }
                prepareAsync()
            }
            _currentSong.value = song
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            mp.pause()
            _isPlaying.value = false
            stopProgressTracking()
        } else {
            mp.start()
            _isPlaying.value = true
            startProgressTracking()
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val params = PlaybackParams()
                        params.speed = speed
                        mp.playbackParams = params
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun toggleShuffle() {
        _isShuffled.value = !_isShuffled.value
    }

    fun playNext() {
        val songs = _playlist.value
        if (songs.isEmpty()) return
        val current = _currentSong.value
        val currentIndex = songs.indexOfFirst { it.id == current?.id }

        val nextIndex = if (_isShuffled.value) {
            (0 until songs.size).random()
        } else {
            if (currentIndex in 0 until songs.size - 1) currentIndex + 1 else 0
        }
        playSong(songs[nextIndex])
    }

    fun playPrevious() {
        val songs = _playlist.value
        if (songs.isEmpty()) return
        val current = _currentSong.value
        val currentIndex = songs.indexOfFirst { it.id == current?.id }

        val prevIndex = if (currentIndex > 0) currentIndex - 1 else songs.size - 1
        playSong(songs[prevIndex])
    }

    private fun handleTrackCompletion() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                seekTo(0)
                mediaPlayer?.start()
                _isPlaying.value = true
            }
            RepeatMode.ALL -> {
                playNext()
            }
            RepeatMode.OFF -> {
                val songs = _playlist.value
                val current = _currentSong.value
                val currentIndex = songs.indexOfFirst { it.id == current?.id }
                if (currentIndex in 0 until songs.size - 1) {
                    playNext()
                } else {
                    _isPlaying.value = false
                    _currentPositionMs.value = 0
                    stopProgressTracking()
                }
            }
        }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = scope.launch {
            var step = 0
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _currentPositionMs.value = mp.currentPosition
                        // Generate dynamic bouncing waveform animation frames
                        step++
                        _waveformAmplitudes.value = List(32) { idx ->
                            val wave1 = (kotlin.math.sin((idx + step * 0.4)) * 0.4).toFloat()
                            val wave2 = (kotlin.math.cos((idx * 0.5 + step * 0.6)) * 0.3).toFloat()
                            (0.3f + wave1 + wave2).coerceIn(0.1f, 1.0f)
                        }
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracking()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
