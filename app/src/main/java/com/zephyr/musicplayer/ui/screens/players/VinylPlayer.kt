package com.zephyr.musicplayer.ui.screens.players

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zephyr.musicplayer.ui.components.sliders.AuroraSlider
import com.zephyr.musicplayer.ui.components.sliders.SliderStyle

/**
 * Style 2 — Vinyl: a rotating circular album disc with a center label, like a record.
 * Rotates only while isPlaying = true.
 */
@Composable
fun VinylPlayer(
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
    val infinite = rememberInfiniteTransition(label = "vinyl-spin")
    val rotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl-rot"
    )
    val actualRotation = if (state.isPlaying) rotation else 0f

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier.size(340.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer vinyl disc (solid color, no gradient)
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .rotate(actualRotation)
            ) {
                // Ring details
                repeat(8) { i ->
                    val r = 30 + i * 16
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                        drawCircle(
                            color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.18f),
                            radius = r.toFloat(),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f),
                            center = center
                        )
                    }
                }
                // Album art on the disc
                AsyncImage(
                    model = state.artUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceTint)
                )
            }
            // Center spindle
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            // Tonearm (static)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 30.dp, top = 20.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(80.dp, 120.dp)) {
                    val armColor = androidx.compose.ui.graphics.Color(0xFFB8B0A0)
                    drawRoundRect(
                        color = armColor,
                        topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.5f, 0f),
                        size = androidx.compose.ui.geometry.Size(8f, size.height * 0.7f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                    drawCircle(
                        color = armColor,
                        radius = 16f,
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f + 4f, 0f)
                    )
                    drawCircle(
                        color = androidx.compose.ui.graphics.Color(0xFF5A5048),
                        radius = 10f,
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f + 4f, size.height * 0.7f)
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(state.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(state.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(20.dp))
        AuroraSlider(style = sliderStyle, progress = state.progress, onSeek = onSeek, onSeekFinished = onSeekFinished, isPlaying = state.isPlaying, modifier = Modifier.fillMaxWidth(), height = 32.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(state.positionMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Text(formatTime(state.durationMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
        Spacer(Modifier.height(16.dp))
        ControlsRow(state, onShuffle, onPrev, onPlayPause, onNext, onRepeat)
    }
}
