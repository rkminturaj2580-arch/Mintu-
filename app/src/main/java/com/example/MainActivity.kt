package com.example
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FullPlayerBottomSheet
import com.example.ui.components.PersistentMiniPlayer
import com.example.ui.screens.CreateSongScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.LyricsScreen
import com.example.ui.screens.ProfileAdminScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MintuMusicTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MusicAppViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MusicAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MintuMusicTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MusicAppViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val isShuffled by viewModel.isShuffled.collectAsState()
    val isPlayerExpanded by viewModel.isPlayerExpanded.collectAsState()
    val waveformAmplitudes by viewModel.waveformAmplitudes.collectAsState()

    // Handle back button presses gracefully
    BackHandler {
        if (!viewModel.handleBack()) {
            // Let system handle default back
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        containerColor = DarkBg,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface.copy(alpha = 0.95f))
                    .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            ) {
                // Persistent mini player anchored immediately above the bottom nav
                if (currentSong != null) {
                    PersistentMiniPlayer(
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        currentPosMs = currentPosMs,
                        durationMs = durationMs,
                        onTogglePlay = { viewModel.togglePlayPause() },
                        onExpand = { viewModel.setPlayerExpanded(true) },
                        onNext = { viewModel.playNext() }
                    )
                }

                // Bottom Navigation Bar
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("studio_bottom_nav")
                ) {
                    val navItems = listOf(
                        NavItem(AppScreen.HOME, "Home", Icons.Default.Home),
                        NavItem(AppScreen.CREATE, "Create", Icons.Default.AddCircle),
                        NavItem(AppScreen.LYRICS, "Lyrics", Icons.Default.Mic),
                        NavItem(AppScreen.EXPLORE, "Explore", Icons.Default.Explore),
                        NavItem(AppScreen.STUDIO, "Studio", Icons.Default.Tune),
                        NavItem(AppScreen.LIBRARY, "Library", Icons.Default.LibraryMusic),
                        NavItem(AppScreen.PROFILE, "Profile", Icons.Default.Person)
                    )

                    navItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) NeonCyan else TextMuted
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                    color = if (isSelected) NeonCyan else TextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = NeonPurple.copy(alpha = 0.25f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 700.dp)
                    .align(Alignment.TopCenter)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                        AppScreen.CREATE -> CreateSongScreen(viewModel = viewModel)
                        AppScreen.LYRICS -> LyricsScreen(viewModel = viewModel)
                        AppScreen.EXPLORE -> ExploreScreen(viewModel = viewModel)
                        AppScreen.STUDIO -> StudioScreen(viewModel = viewModel)
                        AppScreen.LIBRARY -> LibraryScreen(viewModel = viewModel)
                        AppScreen.PROFILE -> ProfileAdminScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Full Player Modal Sheet
    if (isPlayerExpanded && currentSong != null) {
        FullPlayerBottomSheet(
            song = currentSong,
            isPlaying = isPlaying,
            currentPosMs = currentPosMs,
            durationMs = durationMs,
            playbackSpeed = playbackSpeed,
            repeatMode = repeatMode,
            isShuffled = isShuffled,
            waveformAmplitudes = waveformAmplitudes,
            onDismiss = { viewModel.setPlayerExpanded(false) },
            onTogglePlay = { viewModel.togglePlayPause() },
            onSeek = { viewModel.seekTo(it) },
            onNext = { viewModel.playNext() },
            onPrevious = { viewModel.playPrevious() },
            onSpeedChange = { viewModel.setPlaybackSpeed(it) },
            onToggleRepeat = { viewModel.toggleRepeat() },
            onToggleShuffle = { viewModel.toggleShuffle() },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onShare = { viewModel.shareAudio(it) }
        )
    }
}

private data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)
