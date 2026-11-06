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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Song
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MusicAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: MusicAppViewModel,
    modifier: Modifier = Modifier
) {
    val allSongs by viewModel.allSongs.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val instrumentalSongs by viewModel.instrumentalSongs.collectAsState()
    val allLyrics by viewModel.allLyrics.collectAsState()
    val allPlaylists by viewModel.allPlaylists.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var selectedTab by remember { mutableStateOf("All Tracks") }
    val tabs = listOf("All Tracks", "Favorites", "Instrumentals", "Lyrics Drafts", "Playlists")

    var showNewPlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var newPlaylistDesc by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Music Library",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${allSongs.size} Songs • ${allLyrics.size} Lyric Drafts",
                        fontSize = 12.sp,
                        color = NeonCyan
                    )
                }

                if (selectedTab == "Playlists") {
                    Surface(
                        onClick = { showNewPlaylistDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = NeonPurple.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Playlist", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Sub Navigation Tabs
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tabs) { tab ->
                    val isSelected = tab == selectedTab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
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

        when (selectedTab) {
            "All Tracks" -> {
                if (allSongs.isEmpty()) {
                    item { EmptyLibraryCard("No songs generated yet. Head to Create to make one!") }
                } else {
                    items(allSongs) { song ->
                        LibrarySongItem(
                            song = song,
                            isCurrent = currentSong?.id == song.id,
                            isPlaying = isPlaying && currentSong?.id == song.id,
                            onPlay = { viewModel.playSong(song, allSongs) },
                            onFavorite = { viewModel.toggleFavorite(song) },
                            onDelete = { viewModel.deleteSong(song) },
                            onShare = { viewModel.shareAudio(song) }
                        )
                    }
                }
            }
            "Favorites" -> {
                if (favoriteSongs.isEmpty()) {
                    item { EmptyLibraryCard("No favorites added yet. Tap the heart on any song!") }
                } else {
                    items(favoriteSongs) { song ->
                        LibrarySongItem(
                            song = song,
                            isCurrent = currentSong?.id == song.id,
                            isPlaying = isPlaying && currentSong?.id == song.id,
                            onPlay = { viewModel.playSong(song, favoriteSongs) },
                            onFavorite = { viewModel.toggleFavorite(song) },
                            onDelete = { viewModel.deleteSong(song) },
                            onShare = { viewModel.shareAudio(song) }
                        )
                    }
                }
            }
            "Instrumentals" -> {
                if (instrumentalSongs.isEmpty()) {
                    item { EmptyLibraryCard("No instrumentals generated yet.") }
                } else {
                    items(instrumentalSongs) { song ->
                        LibrarySongItem(
                            song = song,
                            isCurrent = currentSong?.id == song.id,
                            isPlaying = isPlaying && currentSong?.id == song.id,
                            onPlay = { viewModel.playSong(song, instrumentalSongs) },
                            onFavorite = { viewModel.toggleFavorite(song) },
                            onDelete = { viewModel.deleteSong(song) },
                            onShare = { viewModel.shareAudio(song) }
                        )
                    }
                }
            }
            "Lyrics Drafts" -> {
                if (allLyrics.isEmpty()) {
                    item { EmptyLibraryCard("No saved lyrics drafts. Use AI Lyrics writer to create one!") }
                } else {
                    items(allLyrics) { draft ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = draft.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Row {
                                        IconButton(onClick = {
                                            viewModel.lyricsTitle.value = draft.title
                                            viewModel.editableLyricsText.value = draft.fullText
                                            viewModel.navigateTo(AppScreen.LYRICS)
                                        }) {
                                            Icon(Icons.Default.Edit, "Edit", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(onClick = {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                viewModel.repository.deleteLyricsDraft(draft.id)
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                                Text(
                                    text = "${draft.language} • ${draft.mood}",
                                    fontSize = 12.sp,
                                    color = NeonPink
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = draft.fullText.take(120) + "...",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            "Playlists" -> {
                if (allPlaylists.isEmpty()) {
                    item { EmptyLibraryCard("No playlists yet. Tap 'New Playlist' to organize your music.") }
                } else {
                    items(allPlaylists) { playlist ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(NeonPurple.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Folder, null, tint = NeonPurple)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(playlist.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(playlist.description, fontSize = 12.sp, color = TextSecondary)
                                    }
                                }
                                IconButton(onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.repository.deletePlaylist(playlist.id)
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New Playlist Modal Dialog
    if (showNewPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showNewPlaylistDialog = false },
            title = { Text("Create New Playlist", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        placeholder = { Text("Playlist Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPlaylistDesc,
                        onValueChange = { newPlaylistDesc = it },
                        placeholder = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.repository.createPlaylist(newPlaylistName, newPlaylistDesc)
                            }
                            newPlaylistName = ""
                            newPlaylistDesc = ""
                            showNewPlaylistDialog = false
                        }
                    }
                ) {
                    Text("Create", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPlaylistDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun LibrarySongItem(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) DarkSurfaceElevated else DarkSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCurrent) NeonCyan.copy(alpha = 0.5f) else DarkSurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCurrent) PrimaryGradient
                        else androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(NeonPurple.copy(alpha = 0.25f), NeonPurple.copy(alpha = 0.25f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isCurrent) NeonCyan else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${song.style.displayName} • ${song.durationSeconds}s",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Share
            IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Share, "Share", tint = TextSecondary, modifier = Modifier.size(18.dp))
            }

            // Favorite
            IconButton(onClick = onFavorite, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) NeonPink else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Delete
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun EmptyLibraryCard(message: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.LibraryMusic, null, tint = NeonPurple, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(message, color = TextSecondary, fontSize = 13.sp)
        }
    }
}
