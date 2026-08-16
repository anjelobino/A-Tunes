package com.example.atunes.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.atunes.ui.components.ThemeToggle
import com.example.atunes.ui.theme.AccentRed

@Composable
fun SettingsScreen(
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    selectedFolder: String? = null,
    onSelectFolder: () -> Unit = {},
    onRescan: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .statusBarsPadding()
            )
        }

        // ── Library Sources ───────────────────────────────────────────────
        item {
            SettingsSection(title = "Library") {
                SettingsRow(
                    icon = Icons.Rounded.FolderOpen,
                    title = "Library Sources",
                    subtitle = if (selectedFolder.isNullOrBlank()) "All music on device" else "Folder: $selectedFolder",
                    onClick = onSelectFolder
                )
                SettingsRow(
                    icon = Icons.Rounded.Refresh,
                    title = "Rescan Library",
                    subtitle = "Re-index all tracks",
                    onClick = onRescan
                )
            }
        }

        // ── Appearance ────────────────────────────────────────────────────
        item {
            SettingsSection(title = "Appearance") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.DarkMode, contentDescription = null,
                        tint = AccentRed, modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Dark Mode", style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground)
                        Text("Toggle between dark and light theme",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    ThemeToggle(isDark = isDark, onToggle = onThemeToggle)
                }
            }
        }

        // ── Playback ──────────────────────────────────────────────────────
        item {
            SettingsSection(title = "Playback") {
                var crossfade by remember { mutableStateOf(false) }
                var gapless by remember { mutableStateOf(true) }

                SettingsToggleRow(
                    icon = Icons.Rounded.Tune,
                    title = "Crossfade",
                    subtitle = "Fade between tracks",
                    checked = crossfade,
                    onCheckedChange = { crossfade = it }
                )
                SettingsToggleRow(
                    icon = Icons.Rounded.MusicNote,
                    title = "Gapless Playback",
                    subtitle = "No silence between tracks",
                    checked = gapless,
                    onCheckedChange = { gapless = it }
                )
            }
        }

        // ── Storage ───────────────────────────────────────────────────────
        item {
            SettingsSection(title = "Storage") {
                SettingsRow(
                    icon = Icons.Rounded.Storage,
                    title = "Clear Thumbnail Cache",
                    subtitle = "Free up space",
                    onClick = {}
                )
            }
        }

        // ── About ─────────────────────────────────────────────────────────
        item {
            SettingsSection(title = "About") {
                SettingsRow(
                    icon = Icons.Rounded.Info,
                    title = "ATunes",
                    subtitle = "Version 1.2 — Developed by Anjelo",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = AccentRed,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = AccentRed, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = AccentRed, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentRed
            )
        )
    }
}
