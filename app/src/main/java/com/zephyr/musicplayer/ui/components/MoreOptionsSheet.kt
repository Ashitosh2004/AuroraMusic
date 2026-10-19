package com.zephyr.musicplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class MoreOptionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

/**
 * Bottom sheet that opens when the "3-dots" button is tapped on the Now Playing screen.
 * Shows playback-related options. Each tap dismisses the sheet and runs the action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsSheet(
    onDismiss: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onViewAlbum: () -> Unit,
    onViewArtist: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSleepTimer: () -> Unit,
    onEqualizer: () -> Unit,
    onPlaybackSpeed: () -> Unit,
    onShare: () -> Unit,
    onDetails: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(3.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
            ) {
                Spacer(Modifier.width(36.dp).height(4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "More Options",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            val options = listOf(
                MoreOptionItem("Add to queue", "Play after current song", Icons.Filled.QueueMusic) { onAddToQueue() },
                MoreOptionItem("Add to playlist", "Save to a custom playlist", Icons.Filled.PlaylistAdd) { onAddToPlaylist() },
                MoreOptionItem("Go to album", "View all songs on this album", Icons.Filled.Album) { onViewAlbum() },
                MoreOptionItem("Go to artist", "Browse this artist's music", Icons.Filled.Person) { onViewArtist() },
                MoreOptionItem("Add to favorites", "Star this track", Icons.Filled.Favorite) { onToggleFavorite() },
                MoreOptionItem("Sleep timer", "Stop playback after a delay", Icons.Filled.Bedtime) { onSleepTimer() },
                MoreOptionItem("Equalizer", "Adjust audio frequencies", Icons.Filled.Equalizer) { onEqualizer() },
                MoreOptionItem("Playback speed", "0.5x to 2.0x", Icons.Filled.Speed) { onPlaybackSpeed() },
                MoreOptionItem("Share", "Send this track to a friend", Icons.Filled.Share) { onShare() },
                MoreOptionItem("Song info", "Title, artist, album, format", Icons.Filled.Info) { onDetails() }
            )
            options.forEach { item ->
                MoreOptionRow(item)
            }
        }
    }
}

@Composable
private fun MoreOptionRow(item: MoreOptionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { item.onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(38.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
    }
}

