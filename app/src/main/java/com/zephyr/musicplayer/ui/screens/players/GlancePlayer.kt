package com.zephyr.musicplayer.ui.screens.players

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zephyr.musicplayer.ui.components.sliders.SliderStyle
import kotlin.math.min

/**
 * Style 5 — Glance / Progress Ring.
 * Album art in a circle surrounded by a progress ring drawn via Canvas. Soft, ambient.
 */
@Composable
fun GlancePlayer(
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
    val accent = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceTint
    val onBg = MaterialTheme.colorScheme.onBackground
    val infinite = rememberInfiniteTransition(label = "glance-pulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val ringScale by animateFloatAsState(
        targetValue = if (state.isPlaying) 1f else 0.96f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ring-scale"
    )
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(80, easing = LinearEasing),
        label = "glance-prog"
    )

    var ringRadius = 160.dp * ringScale
    val strokeW = 6.dp
    val pulseAlpha = if (state.isPlaying) pulse else 0.7f

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(360.dp)) {
            // Progress ring
            Canvas(modifier = Modifier.size(360.dp)) {
                val w = size.width
                val stroke = strokeW.toPx()
                val r = (w - stroke) / 2
                val center = Offset(w / 2, w / 2)
                // Track ring
                drawCircle(
                    color = trackColor,
                    radius = r,
                    center = center,
                    style = Stroke(width = stroke)
                )
                // Progress arc
                val sweep = 360f * animatedProgress
                drawArc(
                    color = accent.copy(alpha = pulseAlpha),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(w - stroke, w - stroke),
                    style = Stroke(width = stroke)
                )
            }
            // Album art inside the ring
            AsyncImage(
                model = state.artUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(280.dp)
                    .scale(ringScale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceTint)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(state.title, style = MaterialTheme.typography.titleLarge, color = onBg, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(state.artist, style = MaterialTheme.typography.bodyMedium, color = onBg.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(12.dp))
        // Time display
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(state.positionMs), style = MaterialTheme.typography.labelSmall, color = onBg.copy(alpha = 0.6f))
            Text(formatTime(state.durationMs), style = MaterialTheme.typography.labelSmall, color = onBg.copy(alpha = 0.6f))
        }
        Spacer(Modifier.height(12.dp))
        // Tap-to-seek invisible slider via click on ring is omitted — use the standard controls row
        // We use a thin slider for explicit seeking since the ring is more decorative.
        com.zephyr.musicplayer.ui.components.sliders.AuroraSlider(
            style = sliderStyle,
            progress = state.progress,
            onSeek = onSeek,
            onSeekFinished = onSeekFinished,
            isPlaying = state.isPlaying,
            modifier = Modifier.fillMaxWidth(),
            height = 24.dp
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = onPrev, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Filled.SkipPrevious, "Prev", tint = onBg, modifier = Modifier.size(36.dp))
            }
            Surface(shape = CircleShape, color = accent, modifier = Modifier.size(72.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    IconButton(onClick = onPlayPause, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            if (state.isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
            IconButton(onClick = onNext, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Filled.SkipNext, "Next", tint = onBg, modifier = Modifier.size(36.dp))
            }
        }
    }
}
