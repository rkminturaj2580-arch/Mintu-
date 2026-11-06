package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LyricsLanguage
import com.example.model.SongMood
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MusicAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsScreen(
    viewModel: MusicAppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val title by viewModel.lyricsTitle.collectAsState()
    val prompt by viewModel.lyricsPrompt.collectAsState()
    val language by viewModel.lyricsLanguage.collectAsState()
    val mood by viewModel.lyricsMood.collectAsState()
    val lyricsText by viewModel.editableLyricsText.collectAsState()
    val lyricsStructure by viewModel.currentLyricsStructure.collectAsState()

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
                    text = "AI Lyrics Writer",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Craft multi-lingual songs with verse, chorus and rhyming structures.",
                    fontSize = 12.sp,
                    color = NeonCyan
                )
            }
        }

        // Language Selector
        item {
            Text(
                text = "Select Language",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LyricsLanguage.values().forEach { lang ->
                    val isSelected = lang == language
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.lyricsLanguage.value = lang
                            viewModel.generateLyrics()
                        },
                        label = { Text(lang.displayName, fontSize = 11.sp) },
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

        // Mood Selector
        item {
            Text(
                text = "Song Mood / Theme",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(SongMood.ROMANTIC, SongMood.SAD, SongMood.SPIRITUAL, SongMood.ENERGETIC).forEach { m ->
                    val isSelected = m == mood
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.lyricsMood.value = m
                            viewModel.generateLyrics()
                        },
                        label = { Text(m.displayName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPink,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Title and Theme Prompt
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.lyricsTitle.value = it },
                placeholder = { Text("Song Title / Topic", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = prompt,
                onValueChange = { viewModel.lyricsPrompt.value = it },
                placeholder = { Text("Describe specific story, metaphors or poetic ideas...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        // Action Buttons Row: Regenerate & Rewrite
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GradientButton(
                    text = "Regenerate Full Lyrics",
                    icon = Icons.Default.Refresh,
                    onClick = { viewModel.generateLyrics() },
                    modifier = Modifier.weight(1f),
                    gradient = ElectricGradient,
                    testTag = "regenerate_lyrics_button"
                )
                OutlinedButton(
                    onClick = {
                        val lines = lyricsText.lines().filter { it.isNotBlank() && !it.startsWith("[") }
                        if (lines.isNotEmpty()) {
                            viewModel.rewriteLyricsLine(lines.random())
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rewrite Line", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Editable Lyrics Board
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Song Structure ([Intro], [Verse], [Chorus]...)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Row {
                            // Copy button
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Lyrics", lyricsText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Lyrics copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, "Copy", tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                            // Save draft button
                            IconButton(onClick = { viewModel.saveLyricsDraft() }) {
                                Icon(Icons.Default.BookmarkBorder, "Save Draft", tint = NeonPurple, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = lyricsText,
                        onValueChange = { viewModel.editableLyricsText.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .testTag("lyrics_editor_area"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = NeonCyan
                        )
                    )
                }
            }
        }

        // Direct 1-Click Send To Song Generator
        item {
            GradientButton(
                text = "Send Lyrics Directly to Song Generator →",
                icon = Icons.Default.MusicNote,
                onClick = { viewModel.sendLyricsToSongGenerator() },
                modifier = Modifier.fillMaxWidth(),
                testTag = "send_lyrics_to_generator_button"
            )
        }
    }
}
