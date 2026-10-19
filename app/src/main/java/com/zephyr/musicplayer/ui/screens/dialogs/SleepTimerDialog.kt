package com.zephyr.musicplayer.ui.screens.dialogs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zephyr.musicplayer.viewmodel.MusicViewModel

data class SleepTimerOption(val label: String, val ms: Long)

private val timerOptions = listOf(
    SleepTimerOption("5 min", 5 * 60_000L),
    SleepTimerOption("10 min", 10 * 60_000L),
    SleepTimerOption("15 min", 15 * 60_000L),
    SleepTimerOption("30 min", 30 * 60_000L),
    SleepTimerOption("45 min", 45 * 60_000L),
    SleepTimerOption("1 hour", 60 * 60_000L)
)

@Composable
fun SleepTimerDialog(
    viewModel: MusicViewModel,
    onDismiss: () -> Unit
) {
    val remaining by viewModel.sleepTimerRemainingMs.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        dismissButton = {
            if (remaining != null) {
                TextButton(onClick = {
                    viewModel.cancelSleepTimer()
                    onDismiss()
                }) { Text("Cancel timer") }
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Bedtime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(0.dp))
                Text("  Sleep Timer", fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column {
                if (remaining != null) {
                    val min = (remaining!! / 60_000).toInt()
                    val sec = ((remaining!! % 60_000) / 1000).toInt()
                    Text(
                        "Timer running: ${min}m ${sec}s",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Playback will pause when the timer reaches zero.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        "Choose how long until playback stops.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timerOptions.forEach { option ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                onClick = {
                                    viewModel.startSleepTimer(option.ms)
                                    onDismiss()
                                }
                            ) {
                                Text(
                                    option.label,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
