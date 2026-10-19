package com.zephyr.musicplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zephyr.musicplayer.ui.components.sliders.AuroraSlider
import com.zephyr.musicplayer.ui.components.sliders.SliderStyle
import com.zephyr.musicplayer.ui.screens.players.PlayerStyle
import com.zephyr.musicplayer.ui.theme.ThemeMode
import com.zephyr.musicplayer.ui.theme.accentOptions
import com.zephyr.musicplayer.viewmodel.MusicViewModel

@Composable
fun SettingsScreen(
    viewModel: MusicViewModel,
    contentPadding: PaddingValues
) {
    val prefs by viewModel.preferences.collectAsState(initial = null)
    val themeMode = prefs?.themeMode ?: ThemeMode.DARK
    val accentIndex = prefs?.accentIndex ?: 0
    val roundArt = prefs?.roundAlbumArt ?: true
    val miniStyle = prefs?.miniPlayerStyle ?: 0
    val miniBlur = prefs?.useMiniPlayerBlur ?: false
    val listSpacing = prefs?.listSpacing ?: 0
    val showArtInList = prefs?.showAlbumArtInList ?: true
    val sliderStyleIndex = prefs?.sliderStyleIndex ?: 0
    val playerStyleIndex = prefs?.playerStyleIndex ?: 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Page header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 12.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Customize your listening experience",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }
        }

        // ===== Theme =====
        item {
            SettingsCard(title = "Theme", icon = Icons.Filled.Palette) {
                val themes = listOf(
                    ThemeOption("Daylight", ThemeMode.LIGHT, Icons.Filled.LightMode),
                    ThemeOption("Midnight", ThemeMode.DARK, Icons.Filled.Brightness2),
                    ThemeOption("AMOLED", ThemeMode.AMOLED, Icons.Filled.GraphicEq),
                    ThemeOption("Sepia", ThemeMode.SEPIA, Icons.Filled.LocalFlorist),
                    ThemeOption("Rose", ThemeMode.ROSE, Icons.Filled.WbTwilight),
                    ThemeOption("Forest", ThemeMode.FOREST, Icons.Filled.Park),
                    ThemeOption("System", ThemeMode.SYSTEM, Icons.Filled.Brush)
                )
                themes.forEachIndexed { i, opt ->
                    ThemeRow(
                        title = opt.name,
                        icon = opt.icon,
                        selected = themeMode == opt.mode,
                        onClick = { viewModel.setTheme(opt.mode) }
                    )
                    if (i < themes.lastIndex) {
                        SettingsDivider()
                    }
                }
            }
        }

        // ===== Accent color =====
        item {
            SettingsCard(title = "Accent Color", icon = Icons.Filled.FormatPaint) {
                Text(
                    text = "Choose a flat color tone. No gradients, no neon.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 14.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
                ) {
                    accentOptions.take(5).forEachIndexed { i, accent ->
                        AccentDot(
                            color = accent.color,
                            label = accent.name,
                            selected = accentIndex == i,
                            onClick = { viewModel.setAccent(i) }
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
                ) {
                    accentOptions.drop(5).forEachIndexed { i, accent ->
                        val idx = i + 5
                        AccentDot(
                            color = accent.color,
                            label = accent.name,
                            selected = accentIndex == idx,
                            onClick = { viewModel.setAccent(idx) }
                        )
                    }
                }
            }
        }

        // ===== Player Customization =====
        item {
            SettingsCard(title = "Player Customization", icon = Icons.Filled.RoundedCorner) {
                ToggleRow(
                    title = "Round album art",
                    subtitle = "Use circular album art on the now playing screen",
                    checked = roundArt,
                    onCheckedChange = { viewModel.setRoundAlbumArt(it) }
                )
                SettingsDivider()
                ToggleRow(
                    title = "Show album art in list",
                    subtitle = "Display artwork thumbnails for every song row",
                    checked = showArtInList,
                    onCheckedChange = { viewModel.setShowAlbumArtInList(it) }
                )
                SettingsDivider()
                ToggleRow(
                    title = "Blurred mini player",
                    subtitle = "Apply a soft blur background behind the mini player",
                    checked = miniBlur,
                    onCheckedChange = { viewModel.setMiniPlayerBlur(it) }
                )
            }
        }

        // ===== Player Style =====
        item {
            SettingsCard(title = "Player Style", icon = Icons.Filled.MusicNote) {
                Text(
                    text = "Six animated layouts. All rounded, all flat.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PlayerStyle.entries.forEachIndexed { i, style ->
                    SelectableRow(
                        title = style.displayName,
                        subtitle = style.description,
                        selected = playerStyleIndex == style.ordinal,
                        onClick = { viewModel.setPlayerStyle(style.ordinal) }
                    )
                    if (i < PlayerStyle.entries.lastIndex) SettingsDivider()
                }
            }
        }

        // ===== Slider Style =====
        item {
            SettingsCard(title = "Progress Slider Style", icon = Icons.Filled.GraphicEq) {
                Text(
                    text = "Tap any style to preview. Six designs available.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SliderStyle.entries.forEachIndexed { i, style ->
                    SelectableRow(
                        title = style.displayName,
                        subtitle = style.description,
                        selected = sliderStyleIndex == style.ordinal,
                        onClick = { viewModel.setSliderStyle(style.ordinal) }
                    )
                    if (i < SliderStyle.entries.lastIndex) SettingsDivider()
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Live preview",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                val selectedSliderStyle = SliderStyle.entries.getOrElse(sliderStyleIndex) { SliderStyle.PILL }
                var previewProgress by remember { mutableStateOf(0.3f) }
                LaunchedEffect(Unit) {
                    var dir = 1f
                    var p = 0.1f
                    while (true) {
                        p += dir * 0.012f
                        if (p >= 0.95f) dir = -1f
                        if (p <= 0.05f) dir = 1f
                        previewProgress = p
                        kotlinx.coroutines.delay(50)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceTint
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AuroraSlider(
                            style = selectedSliderStyle,
                            progress = previewProgress,
                            onSeek = {},
                            onSeekFinished = {},
                            isPlaying = true,
                            modifier = Modifier.fillMaxWidth(),
                            height = 32.dp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Style: ${selectedSliderStyle.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // ===== Mini Player Style =====
        item {
            SettingsCard(title = "Mini Player Style", icon = Icons.Filled.ViewCompact) {
                listOf(
                    MiniStyleOption(0, "Compact", "Slim bar with title and basic controls"),
                    MiniStyleOption(1, "Detailed", "Bar with progress and full controls"),
                    MiniStyleOption(2, "Expanded", "Tall bar with album art and wave")
                ).forEachIndexed { i, opt ->
                    SelectableRow(
                        title = opt.name,
                        subtitle = opt.description,
                        selected = miniStyle == opt.index,
                        onClick = { viewModel.setMiniPlayerStyle(opt.index) }
                    )
                    if (i < 2) SettingsDivider()
                }
            }
        }

        // ===== List Density =====
        item {
            SettingsCard(title = "List Density", icon = Icons.Filled.ViewCompact) {
                Text(
                    text = "Adjust vertical spacing between song rows for comfortable reading.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comfortable",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                    Slider(
                        value = listSpacing.toFloat(),
                        onValueChange = { viewModel.setListSpacing(it.toInt()) },
                        valueRange = 0f..16f,
                        steps = 7,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    )
                    Text(
                        text = "Spacious",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }
        }

        // ===== About =====
        item {
            SettingsCard(title = "About", icon = Icons.Filled.MusicNote) {
                Text(
                    text = "Aurora Music",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Version 1.0.0 • Offline-first music player",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Designed with restraint. Solid colors, generous spacing, and a focus on your music. No gradients, no neon — just clean listening.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section header (above the card)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        // The card itself
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(vertical = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f))
        )
    }
}

private data class ThemeOption(val name: String, val mode: ThemeMode, val icon: ImageVector)
private data class MiniStyleOption(val index: Int, val name: String, val description: String)

@Composable
private fun ThemeRow(title: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceTint,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.onPrimary
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun AccentDot(color: androidx.compose.ui.graphics.Color, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
                .then(
                    if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    else Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceTint
            )
        )
    }
}

@Composable
private fun SelectableRow(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
