package com.zephyr.musicplayer.ui.screens.players

import androidx.compose.runtime.Immutable

@Immutable
data class PlayerUiState(
    val title: String,
    val artist: String,
    val album: String,
    val artUri: Any?,
    val progress: Float,
    val positionMs: Long,
    val durationMs: Long,
    val isPlaying: Boolean,
    val shuffle: Boolean,
    val repeatMode: Int
)

enum class PlayerStyle(val displayName: String, val description: String) {
    CLASSIC("Classic", "Large rounded album art with controls below — Apple Music style"),
    VINYL("Vinyl", "Spinning disc with center label"),
    EXPANDED("Expanded", "Hero art + fade-in info, large controls"),
    COMPACT("Compact", "Inline card with horizontal layout"),
    GLANCE("Glance", "Floating card with progress ring"),
    FLOATING("Floating", "Album art with live bouncing visualizer")
}
