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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zephyr.musicplayer.data.Song
import com.zephyr.musicplayer.ui.components.SongItem
import com.zephyr.musicplayer.viewmodel.MusicViewModel

@Composable
fun LibraryScreen(
    viewModel: MusicViewModel,
    contentPadding: PaddingValues,
    onSongClick: (Song, List<Song>) -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val prefs by viewModel.preferences.collectAsState(initial = null)

    var sortMode by remember { mutableStateOf(SortMode.TITLE) }
    var localQuery by remember(query) { mutableStateOf(query) }

    val filteredSongs by remember(songs, localQuery, sortMode) {
        derivedStateOf {
            val filtered = if (localQuery.isBlank()) songs
                else songs.filter {
                    it.title.contains(localQuery, ignoreCase = true) ||
                    it.artist.contains(localQuery, ignoreCase = true) ||
                    it.album.contains(localQuery, ignoreCase = true)
                }
            when (sortMode) {
                SortMode.TITLE -> filtered.sortedBy { it.title }
                SortMode.ARTIST -> filtered.sortedBy { it.artist }
                SortMode.ALBUM -> filtered.sortedBy { it.album }
                SortMode.DATE -> filtered.sortedByDescending { it.dateAdded }
                SortMode.DURATION -> filtered.sortedBy { it.duration }
            }
        }
    }

    val spacingExtra = prefs?.listSpacing ?: 0

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
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
                    text = "Library",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${songs.size} songs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }
            IconButton(onClick = {
                viewModel.toggleShuffle()
                if (songs.isNotEmpty()) {
                    onSongClick(songs.random(), songs)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = "Shuffle",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = {
                sortMode = when (sortMode) {
                    SortMode.TITLE -> SortMode.ARTIST
                    SortMode.ARTIST -> SortMode.ALBUM
                    SortMode.ALBUM -> SortMode.DATE
                    SortMode.DATE -> SortMode.DURATION
                    SortMode.DURATION -> SortMode.TITLE
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = "Sort: ${sortMode.name}",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Search
        OutlinedTextField(
            value = localQuery,
            onValueChange = {
                localQuery = it
                viewModel.setSearchQuery(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            placeholder = { Text("Search songs, artists, albums") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )

        Text(
            text = "Sorted by ${sortMode.name.lowercase()}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + 12.dp)
        ) {
            if (filteredSongs.isEmpty()) {
                item {
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
                                text = if (localQuery.isBlank()) "No songs available" else "No results for \"$localQuery\"",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(filteredSongs) { _, song ->
                    SongItem(
                        song = song,
                        onClick = { onSongClick(song, filteredSongs) },
                        isCurrentlyPlaying = currentSong?.id == song.id && isPlaying,
                        showAlbumArt = prefs?.showAlbumArtInList ?: true
                    )
                    if (spacingExtra > 0) Spacer(Modifier.height(spacingExtra.dp))
                }
            }
        }
    }
}

enum class SortMode { TITLE, ARTIST, ALBUM, DATE, DURATION }
