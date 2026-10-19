package com.zephyr.musicplayer.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Splash screen with animated music note.
 * All flat colors — no gradients, no neon.
 *
 * Sequence:
 *  1. Ring + center disc scale-in with spring
 *  2. Music note icon scales in after delay
 *  3. App name fades in below
 *  4. Pulsing subtle scale on the whole logo while waiting
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    val bg = MaterialTheme.colorScheme.background
    val onBg = MaterialTheme.colorScheme.onBackground

    // Outer ring scale-in
    val ringScale = remember { Animatable(0f) }
    // Center disc
    val discScale = remember { Animatable(0f) }
    // Icon pop-in
    val iconScale = remember { Animatable(0f) }
    // Name alpha
    val nameAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        try {
            // Ring expand
            ringScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            // Disc fill
            discScale.animateTo(1f, tween(400, easing = LinearEasing))
            // Icon pop
            iconScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
            // Name fade
            nameAlpha.animateTo(1f, tween(500, easing = LinearEasing))
            // Hold
            delay(550)
        } catch (_: Exception) {
            // ignore animation errors — still finish
        }
        // Done — always call onFinished so the app proceeds even if animation fails
        onFinished()
    }

    // Safety net: ensure we ALWAYS finish within 2.5 seconds even if the animation coroutine fails
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        onFinished()
    }

    // Subtle infinite pulse on the ring after initial animation
    val infinite = rememberInfiniteTransition(label = "splash-pulse")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                // Outer ring
                Surface(
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.18f),
                    modifier = Modifier
                        .size(140.dp)
                        .scale(ringScale.value * pulse)
                ) {}
                // Middle ring
                Surface(
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.32f),
                    modifier = Modifier
                        .size(108.dp)
                        .scale(ringScale.value)
                ) {}
                // Inner disc
                Surface(
                    shape = CircleShape,
                    color = accent,
                    modifier = Modifier
                        .size(76.dp)
                        .scale(discScale.value)
                ) {}
                // Music note icon on top
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(38.dp)
                        .scale(iconScale.value)
                )
            }
            Spacer(Modifier.height(20.dp))
            // App name
            androidx.compose.animation.AnimatedVisibility(
                visible = nameAlpha.value > 0.05f,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Aurora Music",
                        style = MaterialTheme.typography.headlineMedium,
                        color = onBg,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Listen. Discover. Curate.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onBg.copy(alpha = 0.55f)
                    )
                }
            }
        }
    }
}
