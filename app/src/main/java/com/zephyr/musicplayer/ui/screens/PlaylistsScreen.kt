package com.zephyr.musicplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zephyr.musicplayer.data.Song
import com.zephyr.musicplayer.ui.components.SectionHeader
import com.zephyr.musicplayer.ui.screens.dialogs.CreatePlaylistDialog
import com.zephyr.musicplayer.ui.screens.dialogs.DeleteConfirmDialog
import com.zephyr.musicplayer.viewmodel.MusicViewModel

@Composable
fun PlaylistsScreen(
    viewModel: MusicViewModel,
    contentPadding: PaddingValues,
    onSongClick: (Song, List<Song>) -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val playlistContents by viewModel.playlistContents.collectAsState()

    var showCreate by remember { mutableStateOf(false) }
    var deletePlaylistId by remember { mutableStateOf<String?>(null) }
    var expandedPlaylistId by remember { mutableStateOf<String?>(null) }

    if (showCreate) {
        CreatePlaylistDialog(
            viewModel = viewModel,
            onDismiss = { showCreate = false },
            onCreated = { showCreate = false }
        )
    }
    deletePlaylistId?.let { id ->
        val meta = playlists.firstOrNull { it.id == id }
        DeleteConfirmDialog(
            title = "Delete playlist",
            message = "Delete \"${meta?.name ?: ""}\"? This cannot be undone.",
            onDismiss = { deletePlaylistId = null },
            onConfirm = {
                viewModel.deletePlaylist(id)
                deletePlaylistId = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding()
        )
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Playlists",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Auto-curated + your custom playlists",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }
        }

        // Smart Playlists (Favorites + Recent)
        item {
            SectionHeader(
                title = "Smart Playlists",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            val favoritesCount = viewModel.favoriteSongs.value.size
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SmartPlaylistMini(
                        title = "Favorites",
                        subtitle = "$favoritesCount songs",
                        icon = Icons.Filled.Favorite,
                        onClick = {
                            val favs = viewModel.favoriteSongs.value
                            if (favs.isNotEmpty()) onSongClick(favs.first(), favs)
                        }
                    )
                }
                item {
                    SmartPlaylistMini(
                        title = "Recently Played",
                        subtitle = "Last 15 songs",
                        icon = Icons.Filled.History,
                        onClick = {
                            val recent = songs.take(15)
                            if (recent.isNotEmpty()) onSongClick(recent.first(), recent)
                        }
                    )
                }
                item {
                    SmartPlaylistMini(
                        title = "Quick Mix",
                        subtitle = "${songs.size} songs",
                        icon = Icons.Filled.Star,
                        onClick = {
                            val mix = songs.shuffled().take(20).ifEmpty { songs }
                            if (mix.isNotEmpty()) onSongClick(mix.first(), mix)
                        }
                    )
                }
            }
        }

        // Custom playlists section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(title = "Your Playlists", modifier = Modifier)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { showCreate = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Filled.Add, contentDescription = "New playlist", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        if (playlists.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No custom playlists yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(8.dp))
                        Text("Tap the + button above to create one.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
                    }
                }
            }
        } else {
            items(playlists) { meta ->
                val songsInList = viewModel.songsInPlaylist(meta.id)
                CustomPlaylistCard(
                    name = meta.name,
                    songCount = songsInList.size,
                    artUri = songsInList.firstOrNull()?.artUri,
                    isExpanded = expandedPlaylistId == meta.id,
                    onPlay = {
                        if (songsToPlay_safe(songsInList)) {
                            onSongClick(songsInList.first(), songsInList)
                        }
                    },
                    onToggleExpand = {
                        expandedPlaylistId = if (expandedPlaylistId == meta.id) null else meta.id
                    },
                    onDelete = { deletePlaylistId = meta.id },
                    songsInList = songsInList,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onSongClick = { song ->
                        onSongClick(song, songsInList)
                    },
                    onRemoveSong = { song ->
                        viewModel.removeSongFromPlaylist(meta.id, song)
                    }
                )
            }
        }
    }
}

private fun songsToPlay_safe(songs: List<Song>): Boolean = songs.isNotEmpty()

@Composable
private fun SmartPlaylistMini(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun CustomPlaylistCard(
    name: String,
    songCount: Int,
    artUri: Any?,
    isExpanded: Boolean,
    onPlay: () -> Unit,
    onToggleExpand: () -> Unit,
    onDelete: () -> Unit,
    songsInList: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onRemoveSong: (Song) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncAlbumArt(artUri = artUri, size = 56.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("$songCount songs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                IconButton(onClick = onPlay) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onToggleExpand) {
                    Icon(Icons.Filled.MoreHoriz, contentDescription = "Expand", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            if (isExpanded && songsInList.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                songsInList.take(10).forEach { song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSongClick(song) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncAlbumArt(artUri = song.artUri, size = 36.dp)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(song.title, style = MaterialTheme.typography.bodyMedium, color = if (currentSong?.id == song.id && isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(song.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(onClick = { onRemoveSong(song) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove from playlist", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Delete playlist", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AsyncAlbumArt(artUri: Any?, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceTint),
        contentAlignment = Alignment.Center
    ) {
        if (artUri != null) {
            coil.compose.AsyncImage(
                model = artUri,
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val iconSize = androidx.compose.ui.unit.Dp(size.value / 2)
            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.size(iconSize))
        }
    }
}
