package com.example.atunes.ui.nowplaying

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.atunes.service.NowPlayingState
import com.example.atunes.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun NowPlayingScreen(
    vm: NowPlayingViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()

    // Update position every second
    LaunchedEffect(state.isPlaying) {
        while (state.isPlaying) {
            delay(1000)
            vm.updatePosition()
        }
    }

    // Continuous rotation — pauses when not playing
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val continuousAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing)
        ),
        label = "spin"
    )

    var frozenAngle by remember { mutableFloatStateOf(0f) }
    val displayAngle = if (state.isPlaying) continuousAngle else frozenAngle

    LaunchedEffect(state.isPlaying) {
        if (!state.isPlaying) frozenAngle = continuousAngle
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCC2010),
                        Color(0xFF8B0000),
                        Color(0xFF300000)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top bar ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        contentDescription = "More",
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.weight(0.5f))

            // ── Spinning Vinyl Record ─────────────────────────────────────
            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Vinyl disc
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .rotate(displayAngle)
                        .clip(CircleShape)
                        .background(VinylBlack)
                        .drawBehind {
                            // Grooves
                            for (i in 1..12) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.03f + i * 0.005f),
                                    radius = size.minDimension / 2 * (0.38f + i * 0.04f),
                                    style = Stroke(width = 0.8.dp.toPx())
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Center label with album art
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(AccentRedLight, AccentRedDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        state.track?.let { track ->
                            AsyncImage(
                                model = Uri.parse(
                                    "content://media/external/audio/albumart/${track.albumId}"
                                ),
                                contentDescription = track.album,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    // Center spindle hole
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(VinylBlack)
                    )
                }
            }

            Spacer(Modifier.weight(0.5f))

            // ── Track info ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = state.track?.title ?: "No track playing",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.track?.artist ?: "—",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = { state.track?.let { vm.toggleLike(it) } }) {
                    Icon(
                        imageVector = if (state.track?.isLiked == true)
                            Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (state.track?.isLiked == true) Color.White else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Scrub bar ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Slider(
                    value = state.progress,
                    onValueChange = { frac ->
                        vm.seekTo((frac * state.durationMs).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatMs(state.positionMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        formatMs(state.durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Transport controls ────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(onClick = vm::toggleShuffle) {
                    Icon(
                        Icons.Rounded.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (state.isShuffle) Color.White else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                // Previous
                IconButton(
                    onClick = vm::skipPrevious,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                // Play/Pause — large button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { vm.togglePlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                        tint = AccentRed,
                        modifier = Modifier.size(40.dp)
                    )
                }
                // Next
                IconButton(
                    onClick = vm::skipNext,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                // Repeat
                IconButton(onClick = vm::cycleRepeat) {
                    Icon(
                        imageVector = when (state.repeatMode) {
                            1    -> Icons.Rounded.RepeatOne
                            2    -> Icons.Rounded.Repeat
                            else -> Icons.Rounded.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (state.repeatMode > 0) Color.White else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Bottom icon row ───────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf<Pair<ImageVector, String>>(
                    Icons.Rounded.VolumeUp to "Output",
                    Icons.Rounded.Equalizer to "Equalizer",
                    Icons.Rounded.QueueMusic to "Queue",
                    Icons.Rounded.Share to "Share"
                ).forEach { (icon, desc) ->
                    IconButton(onClick = {}) {
                        Icon(
                            icon, contentDescription = desc,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
