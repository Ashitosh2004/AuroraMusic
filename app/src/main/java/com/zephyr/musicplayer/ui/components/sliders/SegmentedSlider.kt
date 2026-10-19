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

/**
 * Style 2 — Segmented bar: a series of vertical bars of equal height, played ones
 * are accent-colored, unplayed ones are tint-colored. Spotify-like.
 */
@Composable
fun SegmentedSlider(
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    segments: Int = 40,
    height: androidx.compose.ui.unit.Dp = 28.dp
) {
    val accent = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceTint
    val accentDim = accent.copy(alpha = 0.5f)
    var dragOffset by remember { mutableFloatStateOf(progress) }
    var isDragging by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragOffset else progress,
        animationSpec = tween(80, easing = LinearEasing),
        label = "seg-progress"
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
            val gap = 2f
            val segWidth = (totalWidth - gap * (segments - 1)) / segments
            val segHeight = size.height * 0.55f
            val yPos = (size.height - segHeight) / 2
            val activeSegments = (segments * animatedProgress).toInt()

            for (i in 0 until segments) {
                val x = i * (segWidth + gap)
                val isPlayed = i < activeSegments
                val isCurrent = i == activeSegments && animatedProgress < 1f
                val color = when {
                    isPlayed -> accent
                    isCurrent -> accentDim
                    else -> trackColor
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, yPos),
                    size = Size(segWidth, segHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(segWidth / 2, segWidth / 2)
                )
            }
        }
    }
}

