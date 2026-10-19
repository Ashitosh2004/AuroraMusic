package com.zephyr.musicplayer.ui.components.sliders

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Style 1 — Pill slider: a thick rounded bar with a soft glowing thumb (no actual glow).
 * Smooth and minimal, Apple-Music style.
 */
@Composable
fun PillSlider(
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 28.dp
) {
    val accent = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceTint
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    var dragOffset by remember { mutableFloatStateOf(progress) }
    var isDragging by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else progress,
        animationSpec = tween(80, easing = LinearEasing),
        label = "pill-progress"
    )
    val thumbScale by animateFloatAsState(if (isDragging) 1.15f else 1f, label = "pill-thumb")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(Unit) {
                detectTapGestures { tap ->
                    val w = size.width.toFloat()
                    val v = (tap.x / w).coerceIn(0f, 1f)
                    dragOffset = v
                    onSeekFinished(v)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val w = size.width.toFloat()
                        dragOffset = (offset.x / w).coerceIn(0f, 1f)
                        onSeek(dragOffset)
                    },
                    onDragEnd = {
                        onSeekFinished(dragOffset)
                        isDragging = false
                    },
                    onDragCancel = { isDragging = false }
                ) { change, _ ->
                    val w = size.width.toFloat()
                    dragOffset = (change.position.x / w).coerceIn(0f, 1f)
                    onSeek(dragOffset)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val barHeight = (size.height * 0.18f).coerceAtLeast(4f)
            val corner = CornerRadius(barHeight / 2, barHeight / 2)
            // Track
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(0f, (size.height - barHeight) / 2),
                size = Size(size.width, barHeight),
                cornerRadius = corner
            )
            // Progress
            drawRoundRect(
                color = accent,
                topLeft = Offset(0f, (size.height - barHeight) / 2),
                size = Size(size.width * animatedProgress, barHeight),
                cornerRadius = corner
            )
            // Thumb
            val thumbR = (barHeight * 0.85f) * thumbScale
            val cx = size.width * animatedProgress
            val cy = size.height / 2
            drawCircle(color = onSurfaceColor, radius = thumbR, center = Offset(cx, cy))
            drawCircle(color = accent, radius = thumbR * 0.55f, center = Offset(cx, cy))
        }
    }
}
