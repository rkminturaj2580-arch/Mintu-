package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.RepeatMode as AudioRepeatMode
import com.example.model.Song
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CardGlassBg
import com.example.ui.theme.CardGlassBorder
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WaveformGradient

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    borderAlpha: Float = 0.15f,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable(onClick = onClick)
            .background(CardGlassBg, shape)
            .border(1.dp, Color.White.copy(alpha = borderAlpha), shape)
    } else {
        modifier
            .clip(shape)
            .background(CardGlassBg, shape)
            .border(1.dp, Color.White.copy(alpha = borderAlpha), shape)
    }

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    gradient: Brush = PrimaryGradient,
    enabled: Boolean = true,
    testTag: String = "gradient_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.White.copy(alpha = 0.08f)
        ),
        modifier = modifier
            .testTag(testTag)
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) gradient else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray)))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun WaveformVisualizer(
    amplitudes: List<Float>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 28,
    maxHeight: Dp = 48.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bars = amplitudes.take(barCount)
        bars.forEachIndexed { index, amp ->
            val heightFraction = if (isPlaying) amp else 0.15f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 1.5.dp)
                    .height(maxHeight * heightFraction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (index % 3 == 0) WaveformGradient
                        else Brush.verticalGradient(listOf(NeonViolet, ElectricBlue))
                    )
            )
        }
    }
}

@Composable
fun PersistentMiniPlayer(
    currentSong: Song?,
    isPlaying: Boolean,
    currentPosMs: Int,
    durationMs: Int,
    onTogglePlay: () -> Unit,
    onExpand: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentSong == null) return

    val progress = (currentPosMs.toFloat() / durationMs.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("persistent_mini_player")
            .clickable(onClick = onExpand),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated.copy(alpha = 0.96f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            // Track progress line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = NeonCyan,
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art / Disc
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(NeonPurple, NeonPink, ElectricBlue)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Song Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentSong.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${currentSong.style.displayName} • ${currentSong.voiceType.displayName}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Mini animated waveform indicator
                if (isPlaying) {
                    MiniBouncingBars()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Play / Pause Button
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("mini_player_play_pause")
                        .clip(CircleShape)
                        .background(NeonPurple.copy(alpha = 0.25f))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Next Button
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MiniBouncingBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "bouncing_bars")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(280, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(16.dp)
    ) {
        listOf(h1, h2, h3).forEach { fraction ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp * fraction)
                    .clip(RoundedCornerShape(1.dp))
                    .background(NeonCyan)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerBottomSheet(
    song: Song?,
    isPlaying: Boolean,
    currentPosMs: Int,
    durationMs: Int,
    playbackSpeed: Float,
    repeatMode: AudioRepeatMode,
    isShuffled: Boolean,
    waveformAmplitudes: List<Float>,
    onDismiss: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeek: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onShare: (Song) -> Unit
) {
    if (song == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBg,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Player",
                        tint = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "STUDIO MASTER PLAYBACK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = song.provider,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(onClick = { onToggleFavorite(song) }) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (song.isFavorite) NeonPink else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Spinning Studio Disc & Glow Vinyl
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(NeonPurple.copy(alpha = 0.5f), Color(0xFF1E143E), Color.Black)
                        )
                    )
                    .border(2.dp, NeonViolet.copy(alpha = 0.4f), CircleShape)
                    .rotate(if (isPlaying) rotation else 0f),
                contentAlignment = Alignment.Center
            ) {
                // Grooves
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                )
                // Center Label
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title & Info
            Text(
                text = song.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${song.style.displayName} • ${song.mood.displayName} • ${song.bpm} BPM",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Interactive Dynamic Waveform
            WaveformVisualizer(
                amplitudes = waveformAmplitudes,
                isPlaying = isPlaying,
                maxHeight = 44.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Scrubber Slider
            var sliderValue by remember(currentPosMs) { mutableFloatStateOf(currentPosMs.toFloat()) }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { onSeek(sliderValue.toInt()) },
                valueRange = 0f..durationMs.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_seek_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = NeonCyan,
                    activeTrackColor = NeonPurple,
                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                )
            )

            // Timestamps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(currentPosMs),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = formatMs(durationMs),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffled) NeonCyan else TextMuted
                    )
                }

                // Previous
                IconButton(onClick = onPrevious) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Play / Pause Central Action
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(PrimaryGradient)
                        .clickable(onClick = onTogglePlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next
                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Repeat Mode
                IconButton(onClick = onToggleRepeat) {
                    Icon(
                        imageVector = when (repeatMode) {
                            AudioRepeatMode.ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (repeatMode != AudioRepeatMode.OFF) NeonCyan else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Secondary Quick Actions (Speed, Share, Download)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback speed pill
                Surface(
                    onClick = {
                        val nextSpeed = when (playbackSpeed) {
                            0.75f -> 1.0f
                            1.0f -> 1.25f
                            1.25f -> 1.5f
                            else -> 0.75f
                        }
                        onSpeedChange(nextSpeed)
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceBorder.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${playbackSpeed}x Speed",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Share Button
                IconButton(onClick = { onShare(song) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Track",
                        tint = TextSecondary
                    )
                }

                // Download MP3/WAV status
                Surface(
                    onClick = { onShare(song) },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceBorder.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Export WAV",
                            color = AmberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun formatMs(ms: Int): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
