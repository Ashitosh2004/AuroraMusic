package com.zephyr.musicplayer.ui.components.sliders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class SliderStyle(val displayName: String, val description: String) {
    PILL("Pill", "Soft rounded bar with a two-tone thumb"),
    SEGMENTED("Segmented", "Equal-height bars, Spotify-like"),
    WAVEFORM("Waveform", "Pseudo-waveform visualization"),
    DOTTED("Dotted", "Row of dots that fill as it plays"),
    MINIMAL("Minimal", "Ultra-thin bar with expanding thumb"),
    EQUALIZER("Equalizer", "Live EQ bars that pulse near playhead")
}

@Composable
fun AuroraSlider(
    style: SliderStyle,
    progress: Float,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    height: Dp = 32.dp
) {
    when (style) {
        SliderStyle.PILL -> PillSlider(progress, onSeek, onSeekFinished, modifier, height = height)
        SliderStyle.SEGMENTED -> SegmentedSlider(progress, onSeek, onSeekFinished, modifier, height = height)
        SliderStyle.WAVEFORM -> WaveformSlider(progress, onSeek, onSeekFinished, modifier, height = height + 12.dp)
        SliderStyle.DOTTED -> DottedSlider(progress, onSeek, onSeekFinished, modifier, height = height)
        SliderStyle.MINIMAL -> MinimalSlider(progress, onSeek, onSeekFinished, modifier, height = height)
        SliderStyle.EQUALIZER -> EqualizerSlider(progress, onSeek, onSeekFinished, modifier, isPlaying = isPlaying, height = height + 8.dp)
    }
}
