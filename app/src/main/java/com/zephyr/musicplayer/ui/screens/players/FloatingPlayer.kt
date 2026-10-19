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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.zephyr.musicplayer.ui.components.sliders.AuroraSlider
import com.zephyr.musicplayer.ui.components.sliders.SliderStyle
import kotlin.math.sin

/**
 * Style 6 — Floating visualizer.
 * Album art in a large rounded card above a live bouncing bar visualizer.
 */
@Composable
fun FloatingPlayer(
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
    val onBg = MaterialTheme.colorScheme.onBackground
    val infinite = rememberInfiniteTransition(label = "float-viz")
    val phase1 by infinite.animateFloat(
        initialValue = 0f, targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Restart),
        label = "p1"
    )
    val phase2 by infinite.animateFloat(
        initialValue = 0f, targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(950, easing = LinearEasing), RepeatMode.Restart),
        label = "p2"
    )
    val artScale by animateFloatAsState(
        targetValue = if (state.isPlaying) 1f else 0.94f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "float-art"
    )

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        AsyncImage(
            model = state.artUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .scale(artScale)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceTint)
        )
        Spacer(Modifier.height(20.dp))
        Text(state.title, style = MaterialTheme.typography.titleLarge, color = onBg, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(state.artist, style = MaterialTheme.typography.bodyMedium, color = onBg.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(16.dp))
        // Live bouncing visualizer — 18 bars
        Canvas(modifier = Modifier.fillMaxWidth().height(50.dp)) {
            val bars = 18
            val gap = 4f
            val bw = (size.width - gap * (bars - 1)) / bars
            val cy = size.height / 2
            for (i in 0 until bars) {
                val phase = if (i % 2 == 0) phase1 else phase2
                val raw = (sin(phase + i * 0.4) * 0.5 + 0.5).toFloat()
                val norm: Float = if (state.isPlaying) raw else 0.15f
                val h: Float = (size.height * (0.2f + norm * 0.8f)).coerceAtLeast(4f)
                val x = i * (bw + gap)
                drawRoundRect(
                    color = accent.copy(alpha = 0.85f),
                    topLeft = Offset(x, cy - h / 2),
                    size = Size(bw, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(bw / 2, bw / 2)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        AuroraSlider(
            style = sliderStyle,
            progress = state.progress,
            onSeek = onSeek,
            onSeekFinished = onSeekFinished,
            isPlaying = state.isPlaying,
            modifier = Modifier.fillMaxWidth(),
            height = 28.dp
        )
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTime(state.positionMs), style = MaterialTheme.typography.labelSmall, color = onBg.copy(alpha = 0.6f))
            Text(formatTime(state.durationMs), style = MaterialTheme.typography.labelSmall, color = onBg.copy(alpha = 0.6f))
        }
        Spacer(Modifier.height(16.dp))
        ControlsRow(state, onShuffle, onPrev, onPlayPause, onNext, onRepeat)
    }
}
