package com.example.atunes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.atunes.service.PlaybackController
import com.example.atunes.ui.navigation.AppNavGraph
import com.example.atunes.ui.theme.ATunesTheme
import com.example.atunes.ui.theme.AccentRed
import com.example.atunes.ui.theme.BackgroundPrimary
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val android.content.Context.dataStore by preferencesDataStore(name = "settings")
private val DARK_THEME_KEY     = booleanPreferencesKey("dark_theme")
private val LIBRARY_INDEXED_KEY = booleanPreferencesKey("library_indexed")
private val SELECTED_FOLDER_KEY = stringPreferencesKey("selected_folder")
private val CROSSFADE_KEY       = booleanPreferencesKey("crossfade")
private val GAPLESS_KEY         = booleanPreferencesKey("gapless")

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val scope = rememberCoroutineScope()

            // ── Persisted state ────────────────────────
            val darkTheme by dataStore.data
                .map { prefs -> prefs[DARK_THEME_KEY] ?: true }
                .collectAsStateWithLifecycle(initialValue = true)

            val libraryIndexed by dataStore.data
                .map { prefs -> prefs[LIBRARY_INDEXED_KEY] ?: false }
                .collectAsStateWithLifecycle(initialValue = false)

            val selectedFolder by dataStore.data
                .map { prefs -> prefs[SELECTED_FOLDER_KEY] }
                .collectAsStateWithLifecycle(initialValue = null)

            val crossfade by dataStore.data
                .map { prefs -> prefs[CROSSFADE_KEY] ?: false }
                .collectAsStateWithLifecycle(initialValue = false)

            val gapless by dataStore.data
                .map { prefs -> prefs[GAPLESS_KEY] ?: true }
                .collectAsStateWithLifecycle(initialValue = true)

            // ── Permission state ──────────────────────────────────────────
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_AUDIO
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

            var permissionGranted by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(this, permission) ==
                            PackageManager.PERMISSION_GRANTED
                )
            }
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted -> permissionGranted = granted }

            // ── Folder picker ─────────────────────────────────────────────
            val folderLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocumentTree()
            ) { uri ->
                uri?.let {
                    // Extract relative path from SAF URI (best effort)
                    val path = it.path?.substringAfterLast(":") ?: ""
                    scope.launch {
                        dataStore.edit { prefs ->
                            prefs[SELECTED_FOLDER_KEY] = path
                        }
                    }
                }
            }

            ATunesTheme(darkTheme = darkTheme) {
                if (!permissionGranted) {
                    PermissionScreen(
                        onRequestPermission = { permissionLauncher.launch(permission) }
                    )
                } else {
                    AppNavGraph(
                        isDark = darkTheme,
                        onThemeToggle = {
                            scope.launch {
                                dataStore.edit { prefs ->
                                    prefs[DARK_THEME_KEY] = !(prefs[DARK_THEME_KEY] ?: true)
                                }
                            }
                        },
                        isFirstLaunch = !libraryIndexed,
                        selectedFolder = selectedFolder,
                        onSelectFolder = { folderLauncher.launch(null) },
                        crossfade = crossfade,
                        gapless = gapless,
                        onCrossfadeToggle = { enabled ->
                            scope.launch {
                                dataStore.edit { prefs -> prefs[CROSSFADE_KEY] = enabled }
                            }
                        },
                        onGaplessToggle = { enabled ->
                            scope.launch {
                                dataStore.edit { prefs -> prefs[GAPLESS_KEY] = enabled }
                            }
                        },
                        onOnboardingComplete = {
                            scope.launch {
                                dataStore.edit { prefs ->
                                    prefs[LIBRARY_INDEXED_KEY] = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        PlaybackController.connect(this)
    }

    override fun onStop() {
        super.onStop()
        // Keep controller alive so background playback continues
        // Only disconnect on destroy
    }

    override fun onDestroy() {
        PlaybackController.disconnect()
        super.onDestroy()
    }
}

@Composable
private fun PermissionScreen(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Allow Access to Music",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "ATunes needs access to your audio files to build your local library. No data ever leaves your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text("Grant Permission", color = Color.White)
            }
        }
    }
}