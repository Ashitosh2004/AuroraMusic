package com.zephyr.musicplayer.ui.components.sliders

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
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
 * Style 3 — Waveform slider: pseudo-waveform visualization with progress overlay.
 * Heights derived deterministically from index, so they look organic but stay stable.
 */
@Composable
fun WaveformSlider(
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    bars: Int = 60,
    height: androidx.compose.ui.unit.Dp = 44.dp
) {
    val accent = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceTint
    var dragOffset by remember { mutableFloatStateOf(progress) }
    var isDragging by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else progress,
        animationSpec = tween(80, easing = LinearEasing),
        label = "wave-progress"
    )

    // Pre-compute waveform heights deterministically
    val heights = remember(bars) {
        FloatArray(bars) { i ->
            val v = (sin(i * 0.7) + sin(i * 0.31) * 0.7 + sin(i * 1.13) * 0.5).toFloat()
            val n = (v + 2.2f) / 4.4f  // normalize to ~0..1
            (0.25f + n * 0.75f)
        }
    }

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
            for (i in 0 until bars) {
                val x = i * (barW + gap)
                val h = size.height * heights[i]
                val y = (size.height - h) / 2
                val played = (i + 0.5f) / bars <= animatedProgress
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
