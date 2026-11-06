package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium Music Studio Theme Palette
val DarkBg = Color(0xFF090614)
val DarkSurface = Color(0xFF130E26)
val DarkSurfaceElevated = Color(0xFF1D163B)
val DarkSurfaceBorder = Color(0x33A78BFA)

// Brand Violet & Electric Blue Gradients
val NeonPurple = Color(0xFF8B5CF6)
val NeonViolet = Color(0xFFA855F7)
val DeepViolet = Color(0xFF6D28D9)
val NeonCyan = Color(0xFF06B6D4)
val ElectricBlue = Color(0xFF3B82F6)
val NeonPink = Color(0xFFEC4899)
val AmberGold = Color(0xFFF59E0B)
val MintGreen = Color(0xFF10B981)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val CardGlassBg = Color(0x2E1E163B)
val CardGlassBorder = Color(0x2EFFFFFF)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(NeonPurple, NeonPink)
)

val ElectricGradient = Brush.horizontalGradient(
    colors = listOf(NeonViolet, ElectricBlue, NeonCyan)
)

val StudioCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF241A4A), Color(0xFF110C26))
)

val WaveformGradient = Brush.verticalGradient(
    colors = listOf(NeonCyan, NeonViolet, NeonPink)
)

val GoldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))
)
