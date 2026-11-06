package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MusicInstrument
import com.example.model.MusicStyle
import com.example.model.SongMood
import com.example.model.VoiceType
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MusicAppViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateSongScreen(
    viewModel: MusicAppViewModel,
    modifier: Modifier = Modifier
) {
    val title by viewModel.createTitle.collectAsState()
    val description by viewModel.createDescription.collectAsState()
    val lyrics by viewModel.createLyrics.collectAsState()
    val selectedStyle by viewModel.createStyle.collectAsState()
    val selectedVoice by viewModel.createVoice.collectAsState()
    val selectedMood by viewModel.createMood.collectAsState()
    val selectedInstruments by viewModel.createInstruments.collectAsState()
    val duration by viewModel.createDuration.collectAsState()
    val bpm by viewModel.createBpm.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generateVariations by viewModel.generateVariations.collectAsState()
    val activeProvider by viewModel.activeProvider.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val generationError by viewModel.generationError.collectAsState()

    var showAdvanced by remember { mutableStateOf(false) }

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
                        text = "AI Song Generator",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Engine: ${activeProvider.displayName}",
                        fontSize = 12.sp,
                        color = NeonCyan
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonPurple.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
                ) {
                    Text(
                        text = "Cost: 10 Credits",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Song Title
        item {
            Text(
                text = "Song Title",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.createTitle.value = it },
                placeholder = { Text("e.g. Chand Ke Paar (Bollywood 2026)", color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_song_title_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCyan
                ),
                singleLine = true
            )
        }

        // Song Description Prompt
        item {
            Text(
                text = "Song Prompt & Musical Idea",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.createDescription.value = it },
                placeholder = {
                    Text(
                        "Describe rhythm, tempo, emotion, e.g. Upbeat modern Bollywood dance number with punchy 808 kicks, sitar hooks and catchy vocal drop...",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .testTag("create_song_prompt_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCyan
                )
            )
        }

        // Lyrics Section (Custom or from AI Lyrics writer)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lyrics / Vocal Phrases",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Open AI Lyrics Writer →",
                    fontSize = 12.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.LYRICS) }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = lyrics,
                onValueChange = { viewModel.createLyrics.value = it },
                placeholder = {
                    Text(
                        "Paste custom lyrics or leave empty to auto-compose lyrics in the selected style...",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("create_song_lyrics_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCyan
                )
            )
        }

        // Music Style Selection
        item {
            Text(
                text = "Music Style & Genre (${MusicStyle.values().size} Available)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MusicStyle.values().forEach { style ->
                    val isSelected = style == selectedStyle
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.createStyle.value = style
                            viewModel.createBpm.value = style.bpm
                        },
                        label = { Text(style.displayName, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) NeonPurple else DarkSurfaceBorder
                        )
                    )
                }
            }
        }

        // Voice Type Selection
        item {
            Text(
                text = "Voice Arrangement",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VoiceType.values().forEach { voice ->
                    val isSelected = voice == selectedVoice
                    Surface(
                        onClick = { viewModel.createVoice.value = voice },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NeonPurple.copy(alpha = 0.3f) else DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeonPurple else DarkSurfaceBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = voice.displayName.replace(" Vocal", ""),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Mood Selection
        item {
            Text(
                text = "Song Mood",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SongMood.values().forEach { mood ->
                    val isSelected = mood == selectedMood
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.createMood.value = mood },
                        label = { Text(mood.displayName, fontSize = 12.sp) },
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

        // Instruments Multi-Select
        item {
            Text(
                text = "Featured Instruments (Select multiple)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MusicInstrument.values().forEach { inst ->
                    val isSelected = selectedInstruments.contains(inst)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val currentList = selectedInstruments.toMutableList()
                            if (isSelected) currentList.remove(inst) else currentList.add(inst)
                            viewModel.createInstruments.value = currentList
                        },
                        label = { Text(inst.displayName, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Advanced Controls Toggle
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showAdvanced = !showAdvanced }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Advanced Studio Controls (BPM, Duration, Variations)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    Icon(
                        imageVector = if (showAdvanced) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }
        }

        // Advanced Panel Content
        if (showAdvanced) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Duration Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Track Duration", fontSize = 13.sp, color = TextPrimary)
                            Text("${duration}s", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                        Slider(
                            value = duration.toFloat(),
                            onValueChange = { viewModel.createDuration.value = it.toInt() },
                            valueRange = 15f..60f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonPurple
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // BPM Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tempo (BPM)", fontSize = 13.sp, color = TextPrimary)
                            Text("${bpm} BPM", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                        }
                        Slider(
                            value = bpm.toFloat(),
                            onValueChange = { viewModel.createBpm.value = it.toInt() },
                            valueRange = 60f..160f,
                            colors = SliderDefaults.colors(
                                thumbColor = AmberGold,
                                activeTrackColor = NeonPink
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Variations Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Generate Two Variations", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("Produces Master + Variation B", fontSize = 11.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = generateVariations,
                                onCheckedChange = { viewModel.generateVariations.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonCyan,
                                    checkedTrackColor = NeonPurple
                                )
                            )
                        }
                    }
                }
            }
        }

        // Error message if any
        generationError?.let { err ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF450A0A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(err, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        // Generate Song Action Button
        item {
            if (isGenerating) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Synthesizing Multitrack Audio...",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            } else {
                GradientButton(
                    text = "Generate Song (10 Credits)",
                    icon = Icons.Default.AutoAwesome,
                    onClick = { viewModel.startSongGeneration() },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "generate_song_submit_button"
                )
            }
        }
    }
}
