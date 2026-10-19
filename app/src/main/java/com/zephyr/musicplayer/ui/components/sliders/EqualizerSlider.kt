package com.zephyr.musicplayer.ui.components.sliders

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * Style 6 — Live EQ-style slider: bars to the left of progress are accent-colored;
 * to the right are tint. The bar nearest the playhead pulses subtly while playing.
 */
@Composable
fun EqualizerSlider(
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    bars: Int = 50,
    height: androidx.compose.ui.unit.Dp = 40.dp
) {
    val accent = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceTint
    var dragOffset by remember { mutableFloatStateOf(progress) }
    var isDragging by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else progress,
        animationSpec = tween(80, easing = LinearEasing),
        label = "eq-progress"
    )
    val infinite = rememberInfiniteTransition(label = "eq-bounce")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Restart),
        label = "eq-phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(Unit) {
                detectTapGestures { tap ->
                    val v = (tap.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onSeekFinished(v)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragOffset = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                        onSeek(dragOffset)
                    },
                    onDragEnd = { onSeekFinished(dragOffset); isDragging = false },
                    onDragCancel = { isDragging = false }
                ) { change, _ ->
                    dragOffset = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onSeek(dragOffset)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val totalWidth = size.width
            val gap = 1.5f
            val barW = (totalWidth - gap * (bars - 1)) / bars
            val playhead = bars * animatedProgress
            for (i in 0 until bars) {
                val x = i * (barW + gap)
                // Base height follows a sine envelope; add bounce for played bars near playhead
                val base = 0.3f + (sin(i * 0.6f) * 0.5f + 0.5f) * 0.5f
                val distToPlayhead = kotlin.math.abs(i - playhead)
                val pulse = if (isPlaying && distToPlayhead < 6 && i <= playhead) {
                    (1f - distToPlayhead / 6f) * 0.5f * (sin(phase + i * 0.6f) * 0.5f + 0.5f)
                } else 0f
                val h = size.height * (base + pulse).coerceIn(0.2f, 1f)
                val y = (size.height - h) / 2
                val played = i < playhead
                drawRoundRect(
                    color = if (played) accent else trackColor,
                    topLeft = Offset(x, y),
                    size = Size(barW, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(barW / 2, barW / 2)
                )
            }
        }
    }
}
