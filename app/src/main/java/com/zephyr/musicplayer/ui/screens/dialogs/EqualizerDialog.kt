package com.zephyr.musicplayer.ui.screens.dialogs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.musicplayer.viewmodel.MusicViewModel

enum class EqPreset(val displayName: String, val bands: FloatArray) {
    FLAT("Flat", floatArrayOf(0f, 0f, 0f, 0f, 0f)),
    BASS_BOOST("Bass Boost", floatArrayOf(8f, 6f, 3f, 0f, -1f)),
    TREBLE_BOOST("Treble Boost", floatArrayOf(-1f, 0f, 2f, 5f, 8f)),
    VOCAL("Vocal", floatArrayOf(-2f, 0f, 4f, 4f, 1f)),
    ROCK("Rock", floatArrayOf(4f, 2f, -2f, 3f, 5f)),
    POP("Pop", floatArrayOf(-1f, 2f, 4f, 2f, 0f)),
    JAZZ("Jazz", floatArrayOf(3f, 2f, -1f, 2f, 4f)),
    CLASSICAL("Classical", floatArrayOf(4f, 3f, 0f, 2f, 3f)),
    ELECTRONIC("Electronic", floatArrayOf(5f, 4f, 0f, 4f, 5f)),
    ACOUSTIC("Acoustic", floatArrayOf(3f, 2f, 1f, 2f, 3f))
}

@Composable
fun EqualizerDialog(
    viewModel: MusicViewModel,
    onDismiss: () -> Unit
) {
    val currentPreset by viewModel.eqPreset.collectAsState()
    val bass by viewModel.bassBoost.collectAsState()
    val treble by viewModel.trebleBoost.collectAsState()

    var selectedPreset by remember { mutableIntStateOf(currentPreset.coerceIn(0, EqPreset.entries.size - 1)) }
    var bassValue by remember { mutableFloatStateOf(bass) }
    var trebleValue by remember { mutableFloatStateOf(treble) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        confirmButton = {
            TextButton(onClick = {
                viewModel.setEqPreset(selectedPreset)
                viewModel.setBassBoost(bassValue)
                viewModel.setTrebleBoost(trebleValue)
                onDismiss()
            }) { Text("Apply") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Equalizer", fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column {
                Text("Preset", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EqPreset.entries.forEach { preset ->
                        val selected = preset.ordinal == selectedPreset
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                            onClick = { selectedPreset = preset.ordinal }
                        ) {
                            Text(
                                preset.displayName,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text("Bass Boost", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("${bassValue.toInt()} dB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Slider(value = bassValue, onValueChange = { bassValue = it }, valueRange = -12f..12f, steps = 23)
                Spacer(Modifier.height(8.dp))
                Text("Treble Boost", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("${trebleValue.toInt()} dB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Slider(value = trebleValue, onValueChange = { trebleValue = it }, valueRange = -12f..12f, steps = 23)
                Spacer(Modifier.height(12.dp))
                // Live band preview
                val presetBands = EqPreset.entries[selectedPreset].bands
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val frequencies = listOf("60", "230", "910", "3.6k", "14k")
                    frequencies.forEachIndexed { i, freq ->
                        val height = (presetBands[i] + 12f) / 24f
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height((20 + height * 50).dp),
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f + height * 0.3f)
                            ) {}
                            Spacer(Modifier.height(4.dp))
                            Text(freq, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
    )
}
