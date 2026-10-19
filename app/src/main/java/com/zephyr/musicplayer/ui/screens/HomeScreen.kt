package com.zephyr.musicplayer.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zephyr.musicplayer.data.MusicRepository
import com.zephyr.musicplayer.data.Song
import com.zephyr.musicplayer.ui.components.AlbumCard
import com.zephyr.musicplayer.ui.components.SectionHeader
import com.zephyr.musicplayer.ui.components.SongItem
import com.zephyr.musicplayer.viewmodel.MusicViewModel

@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    contentPadding: PaddingValues,
    onSongClick: (Song, List<Song>) -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    val recentlyPlayed = remember(songs) { songs.take(10) }
    val recentlyAdded = remember(songs) { songs.sortedByDescending { it.dateAdded }.take(10) }
    val albumsGrouped = remember(songs) { MusicRepository.groupByAlbum(songs).entries.take(20) }
    val artistsGrouped = remember(songs) { MusicRepository.groupByArtist(songs).entries.take(15) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding()
        )
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Aurora Music",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Listen. Discover. Curate.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    )
                }
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (songs.isEmpty()) {
            if (isLoading) {
                items(8) { _ ->
                    com.zephyr.musicplayer.ui.components.ListSkeletonRow()
                }
            } else {
                item {
                    EmptyState(
                        message = "No songs found yet",
                        hint = "Grant storage permission and tap refresh to load your music library."
                    )
                }
            }
        } else {
            // Recently Added
            item {
                SectionHeader(
                    title = "Recently Added",
                    subtitle = "${recentlyAdded.size} new tracks",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(recentlyAdded) { song ->
                        AlbumCard(
                            title = song.title,
                            subtitle = song.artist,
                            artUri = song.artUri,
                            onClick = { onSongClick(song, songs) },
                            cardWidth = 150.dp
                        )
                    }
                }
            }

            // Albums
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(
                    title = "Albums",
                    subtitle = "${albumsGrouped.size} albums available",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(albumsGrouped) { entry ->
                        AlbumCard(
                            title = entry.key,
                            subtitle = "${entry.value.size} songs",
                            artUri = entry.value.first().artUri,
                            onClick = {
                                val first = entry.value.first()
                                onSongClick(first, entry.value)
                            },
                            cardWidth = 140.dp
                        )
                    }
                }
            }

            // Artists
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(
                    title = "Artists",
                    subtitle = "${artistsGrouped.size} artists",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(artistsGrouped) { entry ->
                        AlbumCard(
                            title = entry.key,
                            subtitle = "${entry.value.size} songs",
                            artUri = entry.value.first().artUri,
                            onClick = {
                                val first = entry.value.first()
                                onSongClick(first, entry.value)
                            },
                            cardWidth = 120.dp
                        )
                    }
                }
            }

            // Recommended for you (shuffle-play preview)
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(
                    title = "Quick Picks",
                    subtitle = "Songs you may enjoy",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
            items(recentlyPlayed) { song ->
                SongItem(
                    song = song,
                    onClick = { onSongClick(song, songs) },
                    isCurrentlyPlaying = currentSong?.id == song.id && isPlaying
                )
            }
            item {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    hint: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                textAlign = TextAlign.Center
            )
        }
    }
}
