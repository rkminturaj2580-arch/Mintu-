package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JobStatus
import com.example.model.MusicStyle
import com.example.model.Song
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MusicAppViewModel

@Composable
fun HomeScreen(
    viewModel: MusicAppViewModel,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.wallet.collectAsState()
    val allSongs by viewModel.allSongs.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentJob by viewModel.currentJob.collectAsState()
    val isNetworkConnected by viewModel.isNetworkConnected.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Studio Header & Credit Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MINTU RAJ AI MUSIC",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Your Imagination, Your Music.",
                        fontSize = 12.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Credit Balance Pill with Claim Action
                Surface(
                    onClick = { viewModel.claimDailyCredits() },
                    shape = RoundedCornerShape(20.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("credit_balance_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${wallet.balance} Credits",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Offline Network Indicator
        if (!isNetworkConnected) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x33EF4444),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notice: Running in Offline Mode. Built-in Studio Synthesizer is active.",
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // Active Generation Progress Card (if job running)
        currentJob?.let { job ->
            if (job.status == JobStatus.PROCESSING) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderAlpha = 0.35f
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Generating: ${job.title}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "${(job.progress * 100).toInt()}%",
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { job.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NeonPurple,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Provider: ${job.providerName}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Cancel",
                                    fontSize = 12.sp,
                                    color = NeonPink,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.cancelActiveJob(job.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hero Studio Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_studio_banner"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2E1065), Color(0xFF1E1B4B), Color(0xFF0F172A))
                            )
                        )
                        .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonPurple.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI MUSIC ENGINE V2.0",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Create Full Songs in Any Style",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Produce Bollywood, Bhojpuri, Bhakti, Punjabi & EDM tracks with complete vocals & instruments.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            GradientButton(
                                text = "Create Song",
                                icon = Icons.Default.Add,
                                onClick = { viewModel.navigateTo(AppScreen.CREATE) },
                                modifier = Modifier.weight(1f),
                                testTag = "home_create_song_button"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            GradientButton(
                                text = "AI Lyrics",
                                icon = Icons.Default.Mic,
                                onClick = { viewModel.navigateTo(AppScreen.LYRICS) },
                                gradient = ElectricGradient,
                                modifier = Modifier.weight(1f),
                                testTag = "home_ai_lyrics_button"
                            )
                        }
                    }
                }
            }
        }

        // Quick Studio Tools Grid
        item {
            Text(
                text = "Creative Studio Tools",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickToolCard(
                    title = "Stem Studio",
                    subtitle = "Vocal / Beats Split",
                    icon = Icons.Default.Tune,
                    gradient = Brush.linearGradient(listOf(NeonPurple, DeepViolet)),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.STUDIO) }
                )
                QuickToolCard(
                    title = "Instrumentals",
                    subtitle = "Beats & Ragas",
                    icon = Icons.Default.Piano,
                    gradient = Brush.linearGradient(listOf(NeonCyan, ElectricBlue)),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.createVoice.value = com.example.model.VoiceType.INSTRUMENTAL
                        viewModel.navigateTo(AppScreen.CREATE)
                    }
                )
            }
        }

        // Music Categories Row
        item {
            Text(
                text = "Explore Genres & Styles",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MusicStyle.values()) { style ->
                    Surface(
                        onClick = {
                            viewModel.createStyle.value = style
                            viewModel.navigateTo(AppScreen.CREATE)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Text(
                            text = style.displayName,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Recently Created Songs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recently Created",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "View All (${allSongs.size})",
                    fontSize = 12.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.LIBRARY) }
                )
            }
        }

        if (allSongs.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No songs created yet",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(allSongs.take(5)) { song ->
                SongRowItem(
                    song = song,
                    isCurrent = currentSong?.id == song.id,
                    isPlaying = isPlaying && currentSong?.id == song.id,
                    onPlay = { viewModel.playSong(song, allSongs) },
                    onFavorite = { viewModel.toggleFavorite(song) }
                )
            }
        }

        // Favorites Preview (if any)
        if (favoriteSongs.isNotEmpty()) {
            item {
                Text(
                    text = "Favorite Tracks",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            items(favoriteSongs.take(3)) { favSong ->
                SongRowItem(
                    song = favSong,
                    isCurrent = currentSong?.id == favSong.id,
                    isPlaying = isPlaying && currentSong?.id == favSong.id,
                    onPlay = { viewModel.playSong(favSong, favoriteSongs) },
                    onFavorite = { viewModel.toggleFavorite(favSong) }
                )
            }
        }
    }
}

@Composable
fun QuickToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun SongRowItem(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) DarkSurfaceElevated else CardGlassBg
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCurrent) NeonCyan.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play / Pause Button with circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrent) PrimaryGradient
                        else Brush.linearGradient(listOf(NeonPurple.copy(alpha = 0.3f), ElectricBlue.copy(alpha = 0.3f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isCurrent) NeonCyan else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${song.style.displayName} • ${song.durationSeconds}s",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    song.variationTag?.let { tag ->
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = AmberGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Favorite Icon
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) NeonPink else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
