package com.zephyr.musicplayer.ui.screens.players

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.zephyr.musicplayer.ui.components.sliders.AuroraSlider
import com.zephyr.musicplayer.ui.components.sliders.SliderStyle

/**
 * Style 1 — Classic (Apple Music inspired).
 * Large rounded album art with title, controls, and slider below.
 */
@Composable
fun ClassicPlayer(
    state: PlayerUiState,
    sliderStyle: SliderStyle,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playScale by animateFloatAsState(if (state.isPlaying) 1f else 0.92f, label = "play-scale")

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        AsyncImage(
            model = state.artUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(320.dp)
                .scale(playScale)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceTint)
        )
        Spacer(Modifier.height(28.dp))
        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = state.artist,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(20.dp))
        AuroraSlider(
            style = sliderStyle,
            progress = state.progress,
            onSeek = onSeek,
            onSeekFinished = onSeekFinished,
            isPlaying = state.isPlaying,
            modifier = Modifier.fillMaxWidth(),
            height = 32.dp
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(state.positionMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Text(formatTime(state.durationMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
        Spacer(Modifier.height(16.dp))
        ControlsRow(
            state = state,
            onShuffle = onShuffle,
            onPrev = onPrev,
            onPlayPause = onPlayPause,
            onNext = onNext,
            onRepeat = onRepeat
        )
    }
}

@Composable
internal fun ControlsRow(
    state: PlayerUiState,
    onShuffle: () -> Unit,
    onPrev: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onShuffle, modifier = Modifier.size(40.dp)) {
            Icon(
                Icons.Filled.Shuffle, "Shuffle",
                tint = if (state.shuffle) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
            )
        }
        IconButton(onClick = onPrev, modifier = Modifier.size(52.dp)) {
            Icon(Icons.Filled.SkipPrevious, "Previous",
                tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(40.dp))
        }
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(68.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                IconButton(onClick = onPlayPause, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        if (state.isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
        IconButton(onClick = onNext, modifier = Modifier.size(52.dp)) {
            Icon(Icons.Filled.SkipNext, "Next",
                tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(40.dp))
        }
        IconButton(onClick = onRepeat, modifier = Modifier.size(40.dp)) {
            Icon(
                if (state.repeatMode == Player.REPEAT_MODE_ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                "Repeat",
                tint = if (state.repeatMode != Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
            )
        }
    }
}

internal fun formatTime(ms: Long): String {
    val sec = (ms / 1000).coerceAtLeast(0)
    return "${sec / 60}:${(sec % 60).toString().padStart(2, '0')}"
}
