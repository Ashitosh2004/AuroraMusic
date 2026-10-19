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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Style 5 — Minimal thin slider: extremely thin track with a small thumb that
 * expands on drag. Calm and Apple-Music-now-playing-like.
 */
@Composable
fun MinimalSlider(
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 22.dp
) {
    val accent = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceTint
    var dragOffset by remember { mutableFloatStateOf(progress) }
    var isDragging by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else progress,
        animationSpec = tween(80, easing = LinearEasing),
        label = "min-progress"
    )
    val thumbScale by animateFloatAsState(if (isDragging) 1.4f else 1f, label = "min-thumb")

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
            val barH = (size.height * 0.08f).coerceAtLeast(2f)
            val corner = CornerRadius(barH / 2, barH / 2)
            val y = (size.height - barH) / 2
            // Track
            drawRoundRect(color = trackColor, topLeft = Offset(0f, y), size = Size(size.width, barH), cornerRadius = corner)
            // Progress
            drawRoundRect(color = accent, topLeft = Offset(0f, y), size = Size(size.width * animatedProgress, barH), cornerRadius = corner)
            // Thumb
            val thumbR = (barH * 1.5f) * thumbScale
            drawCircle(color = accent, radius = thumbR, center = Offset(size.width * animatedProgress, size.height / 2))
        }
    }
}
