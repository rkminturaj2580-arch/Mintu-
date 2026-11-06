package com.example.audio

import android.content.Context
import com.example.model.MusicInstrument
import com.example.model.MusicStyle
import com.example.model.SongMood
import com.example.model.VoiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object AudioSynthesizer {

    private const val SAMPLE_RATE = 44100
    private const val CHANNELS = 2
    private const val BITS_PER_SAMPLE = 16

    /**
     * Synthesizes a real playable 44.1kHz 16-bit stereo WAV music track.
     */
    suspend fun synthesizeSong(
        context: Context,
        songId: String,
        style: MusicStyle,
        mood: SongMood,
        voiceType: VoiceType,
        instruments: List<MusicInstrument>,
        durationSeconds: Int = 30,
        stemType: String? = null // null = Master mix, or "melody", "chords", "bass", "drums"
    ): File = withContext(Dispatchers.IO) {
        val audioDir = File(context.filesDir, "audio").apply { if (!exists()) mkdirs() }
        val filename = if (stemType == null) "song_$songId.wav" else "stem_${songId}_$stemType.wav"
        val outputFile = File(audioDir, filename)

        val totalSamples = SAMPLE_RATE * durationSeconds
        val bpm = style.bpm
        val samplesPerBeat = (SAMPLE_RATE * 60) / bpm
        val samplesPer16th = samplesPerBeat / 4

        val rootFreq = getRootFrequency(style, mood)
        val scale = getScaleIntervals(style, mood)
        val chordProgression = getChordProgression(style, mood)

        val buffer = ShortArray(totalSamples * CHANNELS)

        for (sampleIdx in 0 until totalSamples) {
            val t = sampleIdx.toDouble() / SAMPLE_RATE
            val beatIdx = sampleIdx / samplesPerBeat
            val barIdx = (beatIdx / 4) % chordProgression.size
            val chordRootDegree = chordProgression[barIdx]
            val chordRootFreq = rootFreq * Math.pow(2.0, chordRootDegree / 12.0)

            var leftSample = 0.0
            var rightSample = 0.0

            val includeMelody = stemType == null || stemType == "melody" || stemType == "vocals"
            val includeChords = stemType == null || stemType == "chords"
            val includeBass = stemType == null || stemType == "bass"
            val includeDrums = stemType == null || stemType == "drums"

            // 1. Chords / Harmony Pad
            if (includeChords) {
                val chordNotes = listOf(0.0, 4.0, 7.0, 11.0)
                var chordMix = 0.0
                for (offset in chordNotes) {
                    val f = chordRootFreq * Math.pow(2.0, offset / 12.0)
                    chordMix += sin(2.0 * PI * f * t) * 0.12
                    chordMix += sin(4.0 * PI * f * t) * 0.04
                }
                leftSample += chordMix
                rightSample += chordMix
            }

            // 2. Bassline
            if (includeBass) {
                val bassFreq = chordRootFreq * 0.5
                val bassEnv = 1.0 - ((sampleIdx % samplesPerBeat).toDouble() / samplesPerBeat) * 0.4
                val bassSample = (sin(2.0 * PI * bassFreq * t) + 0.3 * sin(4.0 * PI * bassFreq * t)) * 0.28 * bassEnv
                leftSample += bassSample * 0.95
                rightSample += bassSample * 0.95
            }

            // 3. Lead Melody / Vocal Formant
            if (includeMelody && voiceType != VoiceType.INSTRUMENTAL) {
                val noteIdx = ((sampleIdx / samplesPer16th) % 8)
                val scaleDegree = scale[noteIdx % scale.size]
                val noteFreq = chordRootFreq * 2.0 * Math.pow(2.0, scaleDegree / 12.0)

                val noteTime = (sampleIdx % samplesPer16th).toDouble() / samplesPer16th
                val attack = (noteTime / 0.1).coerceIn(0.0, 1.0)
                val decay = exp(-noteTime * 2.2)
                val noteEnv = attack * decay

                // Vibrato & Formant shaping
                val vibrato = sin(2.0 * PI * 5.5 * t) * 0.015
                val melWave = sin(2.0 * PI * (noteFreq * (1.0 + vibrato)) * t) +
                        0.4 * sin(4.0 * PI * noteFreq * t) +
                        0.2 * sin(6.0 * PI * noteFreq * t)

                val vocalSample = melWave * noteEnv * 0.32
                // Stereo panning for lush feel
                leftSample += vocalSample * 0.8
                rightSample += vocalSample * 1.0
            } else if (includeMelody) {
                // Solo instrument (Flute / Piano / Sitar)
                val noteIdx = ((sampleIdx / (samplesPer16th * 2)) % scale.size)
                val noteFreq = chordRootFreq * 2.0 * Math.pow(2.0, scale[noteIdx] / 12.0)
                val noteTime = (sampleIdx % (samplesPer16th * 2)).toDouble() / (samplesPer16th * 2)
                val noteEnv = exp(-noteTime * 2.5)
                val instSample = sin(2.0 * PI * noteFreq * t) * noteEnv * 0.28
                leftSample += instSample
                rightSample += instSample
            }

            // 4. Rhythm & Percussion (Dholak, Tabla, Kick, Snare, Hihat)
            if (includeDrums) {
                val posInBeat = sampleIdx % samplesPerBeat
                val beatInBar = beatIdx % 4

                // Kick / Dholak Bass on beats 0 and 2
                if (beatInBar == 0 || beatInBar == 2) {
                    val kickTime = posInBeat.toDouble() / SAMPLE_RATE
                    if (kickTime < 0.25) {
                        val kickPitch = 140.0 * exp(-kickTime * 25.0) + 48.0
                        val kickEnv = exp(-kickTime * 14.0)
                        val kick = sin(2.0 * PI * kickPitch * kickTime) * kickEnv * 0.5
                        leftSample += kick
                        rightSample += kick
                    }
                }

                // Snare / Tabla Chaati on beats 1 and 3
                if (beatInBar == 1 || beatInBar == 3) {
                    val snareTime = posInBeat.toDouble() / SAMPLE_RATE
                    if (snareTime < 0.2) {
                        val noise = ((Math.random() * 2.0) - 1.0)
                        val snareTone = sin(2.0 * PI * 220.0 * snareTime)
                        val snareEnv = exp(-snareTime * 18.0)
                        val snare = (noise * 0.6 + snareTone * 0.4) * snareEnv * 0.35
                        leftSample += snare
                        rightSample += snare
                    }
                }

                // Hi-Hat / Ghungroo sizzle on every 16th note
                val posIn16th = sampleIdx % samplesPer16th
                val hatTime = posIn16th.toDouble() / SAMPLE_RATE
                if (hatTime < 0.05) {
                    val hatNoise = ((Math.random() * 2.0) - 1.0) * exp(-hatTime * 65.0) * 0.12
                    leftSample += hatNoise * 0.7
                    rightSample += hatNoise * 1.1
                }
            }

            // Master Limiter / Soft Clipping
            leftSample = softClip(leftSample)
            rightSample = softClip(rightSample)

            val leftShort = (leftSample * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
            val rightShort = (rightSample * 32767.0).toInt().coerceIn(-32768, 32767).toShort()

            buffer[sampleIdx * 2] = leftShort
            buffer[sampleIdx * 2 + 1] = rightShort
        }

        writeWavFile(outputFile, buffer, SAMPLE_RATE, CHANNELS, BITS_PER_SAMPLE)
        outputFile
    }

    private fun softClip(x: Double): Double {
        return when {
            x > 0.9 -> 0.9 + 0.1 * (1.0 - exp(-(x - 0.9) * 5.0))
            x < -0.9 -> -0.9 - 0.1 * (1.0 - exp(-(-x - 0.9) * 5.0))
            else -> x
        }
    }

    private fun getRootFrequency(style: MusicStyle, mood: SongMood): Double {
        return when (style) {
            MusicStyle.BOLLYWOOD_2026 -> 261.63 // C4
            MusicStyle.HINDI_ROMANTIC -> 220.00 // A3
            MusicStyle.HINDI_SAD -> 196.00 // G3
            MusicStyle.BHOJPURI -> 293.66 // D4
            MusicStyle.BHAKTI_BHAJAN -> 246.94 // B3
            MusicStyle.DEVOTIONAL -> 261.63 // C4
            MusicStyle.CHHATH_PUJA -> 277.18 // C#4
            MusicStyle.SHIVA_BHAJAN -> 196.00 // G3
            MusicStyle.PUNJABI -> 329.63 // E4
            MusicStyle.TAMIL -> 293.66 // D4
            MusicStyle.POP -> 261.63 // C4
            MusicStyle.ROCK -> 220.00 // A3
            MusicStyle.HIP_HOP -> 174.61 // F3
            MusicStyle.RAP -> 164.81 // E3
            MusicStyle.EDM -> 246.94 // B3
            MusicStyle.CLASSICAL -> 220.00 // A3
            MusicStyle.FOLK -> 261.63 // C4
        }
    }

    private fun getScaleIntervals(style: MusicStyle, mood: SongMood): List<Double> {
        return when (mood) {
            SongMood.SAD, SongMood.EMOTIONAL -> listOf(0.0, 2.0, 3.0, 5.0, 7.0, 8.0, 10.0) // Natural Minor
            SongMood.SPIRITUAL -> listOf(0.0, 1.0, 4.0, 5.0, 7.0, 8.0, 11.0) // Bhairav Raga scale
            SongMood.ENERGETIC, SongMood.HAPPY -> listOf(0.0, 2.0, 4.0, 7.0, 9.0, 12.0) // Major Pentatonic
            else -> listOf(0.0, 2.0, 4.0, 5.0, 7.0, 9.0, 11.0) // Major
        }
    }

    private fun getChordProgression(style: MusicStyle, mood: SongMood): List<Double> {
        return when (mood) {
            SongMood.SAD, SongMood.EMOTIONAL -> listOf(0.0, -4.0, -2.0, -5.0) // i - VI - VII - v
            SongMood.SPIRITUAL -> listOf(0.0, 5.0, 7.0, 0.0)
            SongMood.HAPPY, SongMood.ENERGETIC -> listOf(0.0, 5.0, 9.0, 7.0) // I - IV - vi - V
            else -> listOf(0.0, 7.0, 9.0, 5.0) // I - V - vi - IV pop progression
        }
    }

    private fun writeWavFile(
        file: File,
        audioData: ShortArray,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val subChunk2Size = audioData.size * 2
        val chunkSize = 36 + subChunk2Size

        FileOutputStream(file).use { out ->
            val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
            header.put("RIFF".toByteArray())
            header.putInt(chunkSize)
            header.put("WAVE".toByteArray())
            header.put("fmt ".toByteArray())
            header.putInt(16) // SubChunk1Size for PCM
            header.putShort(1.toShort()) // AudioFormat 1 = PCM
            header.putShort(channels.toShort())
            header.putInt(sampleRate)
            header.putInt(byteRate)
            header.putShort(blockAlign.toShort())
            header.putShort(bitsPerSample.toShort())
            header.put("data".toByteArray())
            header.putInt(subChunk2Size)

            out.write(header.array())

            val pcmBytes = ByteBuffer.allocate(audioData.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (sample in audioData) {
                pcmBytes.putShort(sample)
            }
            out.write(pcmBytes.array())
        }
    }

    /**
     * Trims an audio file to the specified start and end fraction (0.0 to 1.0).
     */
    suspend fun trimAudio(
        context: Context,
        sourceFile: File,
        targetFile: File,
        startFraction: Float,
        endFraction: Float
    ): Boolean = withContext(Dispatchers.IO) {
        if (!sourceFile.exists()) return@withContext false
        try {
            RandomAccessFile(sourceFile, "r").use { raf ->
                val totalBytes = raf.length()
                if (totalBytes <= 44) return@withContext false
                val dataBytes = totalBytes - 44
                val startByte = 44 + ((dataBytes * startFraction.coerceIn(0f, 1f)).toLong() and -4L)
                val endByte = 44 + ((dataBytes * endFraction.coerceIn(0f, 1f)).toLong() and -4L)
                val trimmedDataLen = (endByte - startByte).coerceAtLeast(0)

                FileOutputStream(targetFile).use { out ->
                    val header = ByteArray(44)
                    raf.seek(0)
                    raf.readFully(header)

                    val bb = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
                    bb.position(4)
                    bb.putInt((36 + trimmedDataLen).toInt())
                    bb.position(40)
                    bb.putInt(trimmedDataLen.toInt())

                    out.write(header)

                    raf.seek(startByte)
                    val buffer = ByteArray(8192)
                    var remaining = trimmedDataLen
                    while (remaining > 0) {
                        val toRead = remaining.coerceAtMost(buffer.size.toLong()).toInt()
                        val read = raf.read(buffer, 0, toRead)
                        if (read <= 0) break
                        out.write(buffer, 0, read)
                        remaining -= read
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
