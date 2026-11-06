package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MusicStyle
import com.example.model.Song
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MusicAppViewModel

@Composable
fun StudioScreen(
    viewModel: MusicAppViewModel,
    modifier: Modifier = Modifier
) {
    val allSongs by viewModel.allSongs.collectAsState()
    val selectedSong by viewModel.studioSelectedSong.collectAsState()
    val stems by viewModel.studioStems.collectAsState()
    val trimStart by viewModel.trimStartFraction.collectAsState()
    val trimEnd by viewModel.trimEndFraction.collectAsState()
    val actionMessage by viewModel.studioActionMessage.collectAsState()

    val currentSong = selectedSong ?: allSongs.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "Professional Audio Studio",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Stem separation, audio trimming, extension & style remixing.",
                    fontSize = 12.sp,
                    color = NeonCyan
                )
            }
        }

        // Active Loaded Track Card
        item {
            if (currentSong != null) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.GraphicEq, null, tint = Color.White)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentSong.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${currentSong.style.displayName} • ${currentSong.durationSeconds}s • ${currentSong.bpm} BPM",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        // Play loaded song
                        IconButton(
                            onClick = { viewModel.playSong(currentSong) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.PlayArrow, "Play", tint = NeonCyan)
                        }
                    }
                }
            } else {
                Text("Please create or pick a song from Library to use Studio.", color = TextMuted)
            }
        }

        // Status Toast / Notification Card
        actionMessage?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // 1. Stem Separation Section (Vocals, Harmony, Bass, Drums)
        item {
            Text(
                text = "1. Multitrack Stem Separation",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Isolate or mute specific musical stems in real-time.",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        items(stems.size) { index ->
            val stem = stems[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Default.Mic
                                1 -> Icons.Default.MusicNote
                                2 -> Icons.Default.Timeline
                                else -> Icons.Default.Tune
                            },
                            contentDescription = null,
                            tint = if (stem.isSolo) AmberGold else if (stem.isMuted) TextMuted else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stem.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (stem.isMuted) TextMuted else TextPrimary
                            )
                            Text(
                                text = if (stem.isMuted) "Muted" else if (stem.isSolo) "Solo Active" else "Active in Mix",
                                fontSize = 10.sp,
                                color = if (stem.isMuted) TextMuted else if (stem.isSolo) AmberGold else NeonCyan
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Solo Button
                        Surface(
                            onClick = { viewModel.toggleStemSolo(index) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (stem.isSolo) AmberGold else Color.White.copy(alpha = 0.08f),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "SOLO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stem.isSolo) Color.Black else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Mute Button
                        Surface(
                            onClick = { viewModel.toggleStemMute(index) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (stem.isMuted) NeonPink else Color.White.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = "MUTE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stem.isMuted) Color.White else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Audio Trimming Section
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "2. Audio Trimmer",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Start: ${(trimStart * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "End: ${(trimEnd * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    RangeSlider(
                        value = trimStart..trimEnd,
                        onValueChange = { range ->
                            viewModel.trimStartFraction.value = range.start
                            viewModel.trimEndFraction.value = range.endInclusive
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonPurple
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GradientButton(
                        text = "Trim & Save Track",
                        icon = Icons.Default.ContentCut,
                        onClick = { viewModel.trimAudio() },
                        modifier = Modifier.fillMaxWidth(),
                        gradient = ElectricGradient,
                        testTag = "studio_trim_audio_button"
                    )
                }
            }
        }

        // 3. Audio Extension Section
        item {
            Text(
                text = "3. Audio Extension (+15s / +30s)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GradientButton(
                    text = "Extend +15s",
                    onClick = { viewModel.extendAudio(15) },
                    modifier = Modifier.weight(1f),
                    testTag = "studio_extend_15s_button"
                )
                GradientButton(
                    text = "Extend +30s",
                    onClick = { viewModel.extendAudio(30) },
                    modifier = Modifier.weight(1f),
                    gradient = ElectricGradient,
                    testTag = "studio_extend_30s_button"
                )
            }
        }

        // 4. Music Style Remix Section
        item {
            Text(
                text = "4. Music Remix & Cross-Genre Rework",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Convert current melody into a brand new musical genre arrangement.",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf(
                    MusicStyle.BOLLYWOOD_2026,
                    MusicStyle.BHOJPURI,
                    MusicStyle.BHAKTI_BHAJAN,
                    MusicStyle.PUNJABI,
                    MusicStyle.EDM,
                    MusicStyle.HIP_HOP
                )) { targetStyle ->
                    Surface(
                        onClick = { viewModel.remixTrack(targetStyle) },
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Remix → ${targetStyle.displayName}",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // 5. Export and Share Options
        item {
            currentSong?.let { song ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.shareAudio(song) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold)
                    ) {
                        Icon(Icons.Default.Download, null, tint = AmberGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export WAV", color = AmberGold, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.shareAudio(song) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                    ) {
                        Icon(Icons.Default.Share, null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Track", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
