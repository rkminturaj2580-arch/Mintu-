package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.MusicAppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileAdminScreen(
    viewModel: MusicAppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val wallet by viewModel.wallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val activeProvider by viewModel.activeProvider.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isNetworkConnected by viewModel.isNetworkConnected.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()
    val showPrivacyPolicyDialog by viewModel.showPrivacyPolicyDialog.collectAsState()
    val showDeleteAccountDialog by viewModel.showDeleteAccountDialog.collectAsState()

    var showSupabaseDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var lyriaKeyInput by remember { mutableStateOf("") }

    var authEmailInput by remember { mutableStateOf("") }
    var authNameInput by remember { mutableStateOf("") }

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
            // Profile Header with Live Auth State
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isLoggedIn) userAccount.name else "Guest Creator",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isLoggedIn) userAccount.email else "Not logged in (Local Mode)",
                                fontSize = 12.sp,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Member since: ${userAccount.joinDate}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                authEmailInput = userAccount.email
                                authNameInput = userAccount.name
                                viewModel.showAuthDialog.value = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
                        ) {
                            Text(
                                text = if (isLoggedIn) "Switch Account / Login" else "Login / Sign Up",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isLoggedIn) {
                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TextMuted)
                            ) {
                                Text("Logout", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Network Status Banner
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isNetworkConnected) Color(0x1F10B981) else Color(0x26EF4444),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isNetworkConnected) MintGreen.copy(alpha = 0.5f) else Color(0xFFEF4444).copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isNetworkConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = if (isNetworkConnected) MintGreen else Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isNetworkConnected) "Network Connected: Online Mode Ready" else "No Internet: Local Studio Synthesis Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNetworkConnected) MintGreen else Color(0xFFEF4444)
                        )
                        Text(
                            text = if (isNetworkConnected) "Cloud APIs and on-device synthesis both operational" else "Songs will generate offline using built-in synthesizer",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Wallet & Credits Dashboard
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Credit Balance",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${wallet.balance}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            onClick = { viewModel.claimDailyCredits() },
                            shape = RoundedCornerShape(16.dp),
                            color = AmberGold.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CardGiftcard, null, tint = AmberGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Claim +25 Daily",
                                    color = AmberGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.addAdminCredits(50) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
                        ) {
                            Text("+50 Credits", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.addAdminCredits(150) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
                        ) {
                            Text("+150 VIP", color = NeonPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // AI Provider Engine Configuration
        item {
            Text(
                text = "AI Music Provider Engine Configuration",
                fontSize = 16.sp,
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
                    viewModel.availableProviders.forEach { provider ->
                        val isSelected = provider.id == activeProvider.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setActiveProvider(provider.id) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NeonCyan,
                                    unselectedColor = TextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = provider.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = provider.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showApiKeyDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                    ) {
                        Icon(Icons.Default.Key, null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configure Lyria / Gemini API Key", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Google Play Store & Legal Compliance Section
        item {
            Text(
                text = "Google Play Store & User Data Privacy",
                fontSize = 16.sp,
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.showPrivacyPolicyDialog.value = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                        ) {
                            Icon(Icons.Default.Policy, null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Privacy Policy", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.showDeleteAccountDialog.value = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                        ) {
                            Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delete Account", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Supabase & Cloud Architecture Hub
        item {
            Text(
                text = "Backend & Supabase Database Architecture",
                fontSize = 16.sp,
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
                    Text(
                        text = "Complete Supabase PostgreSQL Schema & REST API Specifications ready to export.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GradientButton(
                        text = "View & Copy Supabase SQL Schema",
                        icon = Icons.Default.Storage,
                        onClick = { showSupabaseDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        gradient = ElectricGradient,
                        testTag = "view_supabase_schema_button"
                    )
                }
            }
        }

        // Credit Transactions Log
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Credit Transaction History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${transactions.size} Records",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        if (transactions.isEmpty()) {
            item {
                Text("No transactions logged yet", color = TextMuted, fontSize = 12.sp)
            }
        } else {
            items(transactions.take(10)) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.description,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                            Text(
                                text = dateStr,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = if (tx.amount >= 0) "+${tx.amount}" else "${tx.amount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (tx.amount >= 0) AmberGold else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }

    // Login / Sign-up Dialog
    if (showAuthDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAuthDialog.value = false },
            title = { Text("Account Login & Sign Up", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Enter your creator name and email address to manage your songs, credits and cloud syncing.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = authNameInput,
                        onValueChange = { authNameInput = it },
                        placeholder = { Text("Your Name (e.g. Mintu Raj)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = authEmailInput,
                        onValueChange = { authEmailInput = it },
                        placeholder = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.login(authEmailInput, authNameInput)
                    }
                ) {
                    Text("Save & Log In", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAuthDialog.value = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showPrivacyPolicyDialog.value = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Privacy Policy & Data Safety", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(360.dp)) {
                    item {
                        Text(
                            text = """
MINTU RAJ AI MUSIC — PRIVACY POLICY
Effective Date: November 2026

1. DATA COLLECTION & MINIMIZATION
Mintu Raj AI Music collects only minimal data necessary to generate and store your musical creations:
- Account profile info (Name, Email for account identification)
- User-authored song prompts, titles, and lyrics
- Music audio files generated by the app (stored locally on your device in app-private storage)

2. ZERO THIRD-PARTY AD TRACKING
We do not sell, rent, or trade your personal data. We do not use third-party behavioral advertisement trackers.

3. DATA SAFETY & PLAY STORE COMPLIANCE
- All data stored in Room SQLite is local to your device unless explicitly synced to your configured Supabase database.
- Audio files generated via on-device synthesis are kept private in your app's directory.
- No sensitive permissions (such as precise location or camera) are requested.

4. USER RIGHTS & ACCOUNT DELETION
Under Google Play Developer Policies and global privacy regulations (GDPR/CCPA), users maintain full control of their data.
You can delete your account, generated songs, drafts, and all associated records anytime using the 'Delete Account' button in the Profile tab.

5. CONTACT
For privacy inquiries or technical questions: rkminturaj2580@gmail.com
                            """.trimIndent(),
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.showPrivacyPolicyDialog.value = false }) {
                    Text("I Understand", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteAccountDialog.value = false },
            title = {
                Text("Delete Account & All Data?", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "This will permanently erase all your generated songs, custom lyrics drafts, credit history, and personal account info. This action cannot be undone.",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAccountAndData() }
                ) {
                    Text("Yes, Delete Everything", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteAccountDialog.value = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Google Lyria API Key Dialog
    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("Configure Google Lyria API Key", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Enter your Google Lyria or Gemini API Key to enable cloud-based neural music generation. Leave empty to keep on-device synthesis.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = lyriaKeyInput,
                        onValueChange = { lyriaKeyInput = it },
                        placeholder = { Text("AIzaSy...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveLyriaApiKey(lyriaKeyInput)
                        showApiKeyDialog = false
                    }
                ) {
                    Text("Save Key", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Supabase SQL Architecture Dialog
    if (showSupabaseDialog) {
        val sqlContent = remember { getSupabaseSchemaSql() }
        AlertDialog(
            onDismissRequest = { showSupabaseDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Supabase PostgreSQL DDL", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Supabase SQL", sqlContent)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "SQL schema copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, "Copy", tint = NeonCyan)
                    }
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(380.dp)) {
                    item {
                        Text(
                            text = sqlContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFFA5F3FC),
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSupabaseDialog = false }) {
                    Text("Close", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

fun getSupabaseSchemaSql(): String {
    return """
-- ==========================================
-- MINTU RAJ AI MUSIC - SUPABASE DATABASE SCHEMA
-- ==========================================

-- 1. Profiles Table
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT NOT NULL,
    display_name TEXT,
    avatar_url TEXT,
    is_premium BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. Credit Wallets Table
CREATE TABLE IF NOT EXISTS public.credit_wallets (
    user_id UUID PRIMARY KEY REFERENCES public.profiles(id) ON DELETE CASCADE,
    balance INT DEFAULT 150 NOT NULL,
    last_daily_claim TIMESTAMPTZ,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. Credit Transactions
CREATE TABLE IF NOT EXISTS public.credit_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    type TEXT NOT NULL,
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. Songs Table
CREATE TABLE IF NOT EXISTS public.songs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    lyrics TEXT,
    style TEXT NOT NULL,
    voice_type TEXT NOT NULL,
    mood TEXT NOT NULL,
    instruments TEXT[],
    duration_seconds INT NOT NULL,
    bpm INT NOT NULL,
    audio_url TEXT NOT NULL,
    is_favorite BOOLEAN DEFAULT FALSE,
    is_instrumental BOOLEAN DEFAULT FALSE,
    provider TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. Lyrics Drafts Table
CREATE TABLE IF NOT EXISTS public.lyrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    language TEXT NOT NULL,
    mood TEXT NOT NULL,
    full_text TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. Generation Jobs Table
CREATE TABLE IF NOT EXISTS public.generation_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    prompt TEXT NOT NULL,
    status TEXT NOT NULL,
    progress FLOAT DEFAULT 0.0,
    result_song_id UUID REFERENCES public.songs(id),
    provider TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 7. Playlists & Playlist Songs
CREATE TABLE IF NOT EXISTS public.playlists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.playlist_songs (
    playlist_id UUID REFERENCES public.playlists(id) ON DELETE CASCADE,
    song_id UUID REFERENCES public.songs(id) ON DELETE CASCADE,
    added_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (playlist_id, song_id)
);

-- Row Level Security (RLS) Policies
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.credit_wallets ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.songs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lyrics ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users access own profile" ON public.profiles FOR ALL USING (auth.uid() = id);
CREATE POLICY "Users access own wallet" ON public.credit_wallets FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users access own songs" ON public.songs FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Users access own lyrics" ON public.lyrics FOR ALL USING (auth.uid() = user_id);
    """.trimIndent()
}
