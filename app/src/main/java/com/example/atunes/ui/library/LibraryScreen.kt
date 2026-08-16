package com.example.atunes.ui.library

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.atunes.data.model.Track
import com.example.atunes.ui.theme.*

private val tabs = listOf("Playlists", "Liked", "Artists", "Albums", "Folders")

@Composable
fun LibraryScreen(
    vm: LibraryViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val playlists by vm.playlists.collectAsStateWithLifecycle()
    val likedTracks by vm.likedTracks.collectAsStateWithLifecycle()
    val artists by vm.artists.collectAsStateWithLifecycle()
    val albums by vm.albums.collectAsStateWithLifecycle()
    val allTracks by vm.allTracks.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Screen title ──────────────────────────────────────────────────
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .statusBarsPadding()
        )

        // ── Tab bar ───────────────────────────────────────────────────────
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = AccentRed,
            edgePadding = 20.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AccentRed
                )
            }
        ) {
            tabs.forEachIndexed { i, label ->
                Tab(
                    selected = selectedTab == i,
                    onClick = { selectedTab = i },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedTab == i) AccentRed
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

        // ── Tab content ───────────────────────────────────────────────────
        when (selectedTab) {
            0 -> PlaylistsTab(playlists)
            1 -> LikedTab(likedTracks, onPlay = { vm.playTracks(likedTracks, it) })
            2 -> ArtistsTab(artists.map { it.artist })
            3 -> AlbumsTab(allTracks)
            4 -> FoldersTab()
        }
    }
}

@Composable
private fun PlaylistsTab(playlists: List<com.example.atunes.data.model.Playlist>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (playlists.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Rounded.QueueMusic,
                    text = "No playlists yet.\nTap + to create your first one."
                )
            }
        }
        items(playlists) { pl ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(listOf(AccentRed, AccentRedDark))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text = pl.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun LikedTab(tracks: List<Track>, onPlay: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        if (tracks.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Rounded.FavoriteBorder,
                    text = "No liked songs yet.\nTap ♥ on any track to save it here."
                )
            }
        }
        itemsIndexed(tracks) { idx, track ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlay(idx) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${idx + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(28.dp)
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = Uri.parse("content://media/external/audio/albumart/${track.albumId}"),
                        contentDescription = track.album,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ArtistsTab(artists: List<String>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (artists.isEmpty()) {
            item { EmptyState(icon = Icons.Rounded.Person, text = "No artists found in your library.") }
        }
        items(artists) { artist ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Person, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = artist,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Rounded.ChevronRight, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun AlbumsTab(tracks: List<Track>) {
    val albums = tracks.groupBy { it.album }.entries.toList()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums.size) { i ->
            val (name, albumTracks) = albums[i]
            val first = albumTracks.first()
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = Uri.parse("content://media/external/audio/albumart/${first.albumId}"),
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = name, style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${albumTracks.size} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FoldersTab() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        EmptyState(icon = Icons.Rounded.Folder, text = "Folder view coming soon.")
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
