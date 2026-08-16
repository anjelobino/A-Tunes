package com.example.atunes.ui.search

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.atunes.ui.theme.*

@Composable
fun SearchScreen(
    vm: SearchViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val query by vm.query.collectAsStateWithLifecycle()
    val results by vm.results.collectAsStateWithLifecycle()
    val recentSearches by vm.recentSearches.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.statusBarsPadding())

        Text(
            text = "Search",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // ── Search bar ────────────────────────────────────────────────────
        OutlinedTextField(
            value = query,
            onValueChange = vm::onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text(
                    "Artists, songs, albums…",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(Icons.Rounded.Search, contentDescription = "Search",
                    tint = if (query.isNotEmpty()) AccentRed else MaterialTheme.colorScheme.onSurfaceVariant)
            },
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotEmpty()) {
                    IconButton(onClick = { vm.onQueryChanged("") }) {
                        Icon(Icons.Rounded.Clear, contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = AccentRed,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                vm.onSearchSubmitted(query)
                focusManager.clearFocus()
            })
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            if (query.isBlank()) {
                // ── Recent searches ───────────────────────────────────────
                if (recentSearches.isNotEmpty()) {
                    item {
                        Text(
                            "Recent Searches",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    items(recentSearches.size) { i ->
                        val q = recentSearches[i]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vm.onQueryChanged(q) }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.History, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(14.dp))
                            Text(
                                q, style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Rounded.NorthWest, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp))
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.Search, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(56.dp))
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Search your local library",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // ── Live results ──────────────────────────────────────────
                if (results.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No results for \"$query\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            "${results.size} results",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                    itemsIndexed(results) { idx, track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vm.playTrack(results, idx) }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = Uri.parse("content://media/external/audio/albumart/${track.albumId}"),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    track.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "${track.artist} · ${track.album}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(Icons.Rounded.PlayArrow, contentDescription = "Play",
                                tint = AccentRed, modifier = Modifier.size(20.dp))
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 82.dp),
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}
