package com.example.atunes.ui.components

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atunes.service.NowPlayingState
import com.example.atunes.ui.theme.AccentRed
import com.example.atunes.ui.theme.AccentRedDark

/**
 * Persistent mini-player docked above the bottom nav bar.
 * Shows current track info, progress bar, and play/pause button.
 */
@Composable
fun MiniPlayer(
    state: NowPlayingState,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = state.track ?: return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        color = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(AccentRed, AccentRedDark)
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            // Progress bar at bottom
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomCenter),
                color = Color.White.copy(alpha = 0.8f),
                trackColor = Color.White.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album art
                AsyncImage(
                    model = track.albumId.let {
                        Uri.parse("content://media/external/audio/albumart/$it")
                    },
                    contentDescription = "Album art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Spacer(Modifier.width(12.dp))

                // Track info
                Column(Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Controls
                IconButton(onClick = onTogglePlayPause) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = onSkipNext) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Skip next",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
