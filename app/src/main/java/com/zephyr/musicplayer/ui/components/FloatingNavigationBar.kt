package com.zephyr.musicplayer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zephyr.musicplayer.ui.Screen

/**
 * Apple-style floating navigation bar.
 * Pill-shaped, rounded, elevated, floating with horizontal margin from screen edges.
 * Selected item gets a subtle pill highlight background.
 */
@Composable
fun FloatingNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items: List<Triple<Screen, ImageVector, ImageVector>> = listOf(
        Triple(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(Screen.Library, Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
        Triple(Screen.Favorites, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        Triple(Screen.Playlists, Icons.Filled.QueueMusic, Icons.Outlined.QueueMusic),
        Triple(Screen.Settings, Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (screen, filledIcon, outlinedIcon) ->
                val selected = currentRoute == screen.route
                FloatingNavItem(
                    label = screen.title,
                    selected = selected,
                    selectedIcon = filledIcon,
                    unselectedIcon = outlinedIcon,
                    onClick = { onNavigate(screen.route) }
                )
            }
        }
    }
}

@Composable
private fun FloatingNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit
) {
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "nav-icon-scale"
    )
    val backgroundColor = if (selected) MaterialTheme.colorScheme.surfaceVariant
                           else MaterialTheme.colorScheme.surface
    val iconColor = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val textColor = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier
                    .size(22.dp)
                    .scale(iconScale)
            )
            // Show label next to icon when selected (Apple-style expanding pill)
            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = scaleIn() + androidx.compose.animation.fadeIn(),
                exit = scaleOut() + androidx.compose.animation.fadeOut()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = label,
                        color = textColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private val clickListener: (Screen) -> Unit = {}
