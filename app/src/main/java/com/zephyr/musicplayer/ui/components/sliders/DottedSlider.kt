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

/**
 * Style 4 — Dotted slider: row of round dots that fill as the song plays.
 * Subtle live shimmer on the current dot.
 */
@Composable
fun DottedSlider(
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    dots: Int = 30,
    height: androidx.compose.ui.unit.Dp = 24.dp
) {
    val accent = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceTint
    var dragOffset by remember { mutableFloatStateOf(progress) }
    var isDragging by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else progress,
        animationSpec = tween(80, easing = LinearEasing),
        label = "dot-progress"
    )

    val infinite = rememberInfiniteTransition(label = "dot-shimmer")
    val shimmer by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse),
        label = "shimmer"
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
            val dotR = (size.height * 0.28f).coerceIn(2f, 8f)
            val step = size.width / dots
            for (i in 0 until dots) {
                val x = step * i + step / 2
                val y = size.height / 2
                val played = (i + 0.5f) / dots <= animatedProgress
                val color = if (played) accent else trackColor
                val r = if (i == (dots * animatedProgress).toInt().coerceAtMost(dots - 1)) {
                    dotR * shimmer
                } else dotR
                drawCircle(color = color, radius = r, center = Offset(x, y))
            }
        }
    }
}
