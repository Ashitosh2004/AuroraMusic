package com.zephyr.musicplayer.ui.screens.players

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AuroraPlayer(
    style: PlayerStyle,
    state: PlayerUiState,
    sliderStyle: com.zephyr.musicplayer.ui.components.sliders.SliderStyle,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (style) {
        PlayerStyle.CLASSIC -> ClassicPlayer(state, sliderStyle, onPlayPause, onNext, onPrev, onSeek, onSeekFinished, onShuffle, onRepeat, modifier)
        PlayerStyle.VINYL -> VinylPlayer(state, sliderStyle, onPlayPause, onNext, onPrev, onSeek, onSeekFinished, onShuffle, onRepeat, modifier)
        PlayerStyle.EXPANDED -> ExpandedPlayer(state, sliderStyle, onPlayPause, onNext, onPrev, onSeek, onSeekFinished, onShuffle, onRepeat, modifier)
        PlayerStyle.COMPACT -> CompactPlayer(state, sliderStyle, onPlayPause, onNext, onPrev, onSeek, onSeekFinished, onShuffle, onRepeat, modifier)
        PlayerStyle.GLANCE -> GlancePlayer(state, sliderStyle, onPlayPause, onNext, onPrev, onSeek, onSeekFinished, onShuffle, onRepeat, modifier)
        PlayerStyle.FLOATING -> FloatingPlayer(state, sliderStyle, onPlayPause, onNext, onPrev, onSeek, onSeekFinished, onShuffle, onRepeat, modifier)
    }
}
