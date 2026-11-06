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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MusicStyle
import com.example.model.Song
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MusicAppViewModel

@Composable
fun ExploreScreen(
    viewModel: MusicAppViewModel,
    modifier: Modifier = Modifier
) {
    val allSongs by viewModel.allSongs.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filterTabs = listOf("All", "Bollywood", "Devotional & Bhakti", "Bhojpuri", "Punjabi", "Pop & EDM")

    val filteredSongs = remember(allSongs, selectedFilter) {
        when (selectedFilter) {
            "Bollywood" -> allSongs.filter {
                it.style == MusicStyle.BOLLYWOOD_2026 || it.style == MusicStyle.HINDI_ROMANTIC || it.style == MusicStyle.HINDI_SAD
            }
            "Devotional & Bhakti" -> allSongs.filter {
                it.style == MusicStyle.BHAKTI_BHAJAN || it.style == MusicStyle.DEVOTIONAL || it.style == MusicStyle.SHIVA_BHAJAN || it.style == MusicStyle.CHHATH_PUJA
            }
            "Bhojpuri" -> allSongs.filter { it.style == MusicStyle.BHOJPURI }
            "Punjabi" -> allSongs.filter { it.style == MusicStyle.PUNJABI }
            "Pop & EDM" -> allSongs.filter { it.style == MusicStyle.POP || it.style == MusicStyle.EDM || it.style == MusicStyle.HIP_HOP }
            else -> allSongs
        }
    }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Explore & Discover",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Trending AI music community releases & audio tracks",
                        fontSize = 12.sp,
                        color = NeonCyan
                    )
                }
            }
        }

        // Category Filter Tabs
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterTabs) { tab ->
                    val isSelected = tab == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = tab },
                        label = { Text(tab, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Featured Spotlight Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4C1D95), Color(0xFF1E3A8A), Color(0xFF0E7490))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "GLOBAL AI HIT OF THE WEEK",
                                color = AmberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Chand Sitare (Bollywood 2026)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Lush vocal harmonies, 808 sub bass & acoustic Indian sitar fusion",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // Song List
        item {
            Text(
                text = "$selectedFilter Releases (${filteredSongs.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (filteredSongs.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No songs found in this category", color = TextSecondary)
                    }
                }
            }
        } else {
            items(filteredSongs) { song ->
                SongRowItem(
                    song = song,
                    isCurrent = currentSong?.id == song.id,
                    isPlaying = isPlaying && currentSong?.id == song.id,
                    onPlay = { viewModel.playSong(song, filteredSongs) },
                    onFavorite = { viewModel.toggleFavorite(song) }
                )
            }
        }
    }
}
