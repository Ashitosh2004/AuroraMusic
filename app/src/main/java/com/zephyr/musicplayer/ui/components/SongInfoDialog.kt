package com.zephyr.musicplayer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zephyr.musicplayer.data.Song

/**
 * Simple dialog showing track details.
 */
@Composable
fun SongInfoDialog(
    song: Song?,
    onDismiss: () -> Unit
) {
    if (song == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
            title = { Text("No song selected") },
            text = { Text("Play a song first to see its details.") }
        )
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        title = { Text("Song Info", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                InfoLine("Title", song.title)
                InfoLine("Artist", song.artist)
                InfoLine("Album", song.album)
                InfoLine("Duration", song.formattedDuration)
                InfoLine("Album ID", song.albumId.toString())
                InfoLine("File", song.data.substringAfterLast('/'))
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Aurora Music • Offline",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        }
    )
}

@Composable
private fun InfoLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
