package com.zephyr.musicplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zephyr.musicplayer.ui.components.MoreOptionsSheet
import com.zephyr.musicplayer.ui.components.SongInfoDialog
import com.zephyr.musicplayer.ui.components.sliders.SliderStyle
import com.zephyr.musicplayer.ui.screens.dialogs.EqualizerDialog
import com.zephyr.musicplayer.ui.screens.players.AuroraPlayer
import com.zephyr.musicplayer.ui.screens.players.PlayerStyle
import com.zephyr.musicplayer.ui.screens.players.PlayerUiState
import com.zephyr.musicplayer.viewmodel.MusicViewModel
import kotlinx.coroutines.delay

@Composable
fun NowPlayingScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.position.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val shuffleMode by viewModel.shuffleMode.collectAsState()
    val playlist by viewModel.playlist.collectAsState()
    val prefs by viewModel.preferences.collectAsState(initial = null)

    var localPosition by remember(currentSong?.id) { mutableLongStateOf(position) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(currentSong?.id, isPlaying, position) {
        if (!isDragging) localPosition = position
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying && !isDragging) {
            localPosition += 500
            delay(500)
        }
    }

    val sliderStyle = SliderStyle.entries.getOrElse(prefs?.sliderStyleIndex ?: 0) { SliderStyle.PILL }
    val playerStyle = PlayerStyle.entries.getOrElse(prefs?.playerStyleIndex ?: 0) { PlayerStyle.CLASSIC }
    val safeDuration = duration.coerceAtLeast(1)
    val progress = (localPosition.toFloat() / safeDuration.toFloat()).coerceIn(0f, 1f)

    var showMoreOptions by remember { mutableStateOf(false) }
    var showSongInfo by remember { mutableStateOf(false) }
    var showEqualizer by remember { mutableStateOf(false) }
    var showPlaybackSpeed by remember { mutableStateOf(false) }
    var showSleepTimer by remember { mutableStateOf(false) }
    var showAddToPlaylist by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val sleepRemaining by viewModel.sleepTimerRemainingMs.collectAsState()

    val state = PlayerUiState(
        title = currentSong?.title ?: "No song selected",
        artist = currentSong?.artist ?: "—",
        album = currentSong?.album ?: "",
        artUri = currentSong?.artUri,
        progress = progress,
        positionMs = localPosition,
        durationMs = duration,
        isPlaying = isPlaying,
        shuffle = shuffleMode,
        repeatMode = repeatMode
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM PLAYLIST",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Aurora Music",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = { showMoreOptions = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                // Quick favorite heart in top bar
                val isFav = currentSong?.id?.toString() in favoriteIds
                IconButton(onClick = {
                    val song = currentSong
                    if (song != null) {
                        viewModel.toggleFavorite(song) { added ->
                            infoMessage = if (added) "Added to Favorites" else "Removed from Favorites"
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFav) "Remove favorite" else "Add to favorites",
                        tint = if (isFav) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Selected player style
            Box(modifier = Modifier.weight(1f)) {
                AuroraPlayer(
                    style = playerStyle,
                    state = state,
                    sliderStyle = sliderStyle,
                    onPlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.next() },
                    onPrev = { viewModel.previous() },
                    onSeek = { frac ->
                        isDragging = true
                        localPosition = (frac * safeDuration).toLong()
                    },
                    onSeekFinished = { frac ->
                        val target = (frac * safeDuration).toLong()
                        viewModel.seekTo(target)
                        localPosition = target
                        isDragging = false
                    },
                    onShuffle = { viewModel.toggleShuffle() },
                    onRepeat = { viewModel.cycleRepeatMode() }
                )
            }

            // Up Next preview
            val currentIndex = playlist.indexOfFirst { it.id == currentSong?.id }
            val upNext = if (currentIndex >= 0 && currentIndex < playlist.size - 1)
                playlist.subList(currentIndex + 1, minOf(currentIndex + 4, playlist.size))
            else emptyList()

            if (upNext.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UP NEXT",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Filled.QueueMusic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    )
                }
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    items(upNext) { song ->
                        com.zephyr.musicplayer.ui.components.SongItem(
                            song = song,
                            onClick = {
                                val idx = playlist.indexOf(song)
                                if (idx >= 0) viewModel.playSongs(playlist, idx)
                            }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // 3-dots menu — bottom sheet with playback options
        if (showMoreOptions) {
            MoreOptionsSheet(
                onDismiss = { showMoreOptions = false },
                onAddToQueue = {
                    showMoreOptions = false
                    infoMessage = "Added to queue"
                },
                onAddToPlaylist = {
                    showMoreOptions = false
                    showAddToPlaylist = true
                },
                onViewAlbum = {
                    showMoreOptions = false
                    infoMessage = "Album: ${currentSong?.album ?: "—"}"
                },
                onViewArtist = {
                    showMoreOptions = false
                    infoMessage = "Artist: ${currentSong?.artist ?: "—"}"
                },
                onToggleFavorite = {
                    showMoreOptions = false
                    val song = currentSong
                    if (song != null) {
                        viewModel.toggleFavorite(song) { added ->
                            infoMessage = if (added) "Added to Favorites" else "Removed from Favorites"
                        }
                    } else {
                        infoMessage = "No song to favorite"
                    }
                },
                onSleepTimer = {
                    showMoreOptions = false
                    showSleepTimer = true
                },
                onEqualizer = {
                    showMoreOptions = false
                    showEqualizer = true
                },
                onPlaybackSpeed = {
                    showMoreOptions = false
                    showPlaybackSpeed = true
                },
                onShare = {
                    showMoreOptions = false
                    val song = currentSong
                    if (song != null) {
                        com.zephyr.musicplayer.features.share.ShareHelper.shareSong(context, song)
                    } else {
                        infoMessage = "Nothing to share"
                    }
                },
                onDetails = {
                    showMoreOptions = false
                    showSongInfo = true
                }
            )
        }

        // Song info dialog
        if (showSongInfo) {
            SongInfoDialog(
                song = currentSong,
                onDismiss = { showSongInfo = false }
            )
        }

        // Equalizer dialog
        if (showEqualizer) {
            EqualizerDialog(
                viewModel = viewModel,
                onDismiss = { showEqualizer = false }
            )
        }

        // Playback speed dialog
        if (showPlaybackSpeed) {
            com.zephyr.musicplayer.ui.screens.dialogs.PlaybackSpeedDialog(
                viewModel = viewModel,
                onDismiss = { showPlaybackSpeed = false }
            )
        }

        // Sleep timer dialog
        if (showSleepTimer) {
            com.zephyr.musicplayer.ui.screens.dialogs.SleepTimerDialog(
                viewModel = viewModel,
                onDismiss = { showSleepTimer = false }
            )
        }

        // Add to playlist dialog
        if (showAddToPlaylist) {
            com.zephyr.musicplayer.ui.screens.dialogs.AddToPlaylistDialog(
                viewModel = viewModel,
                song = currentSong,
                onDismiss = { showAddToPlaylist = false },
                onAdded = { msg ->
                    showAddToPlaylist = false
                    infoMessage = msg
                }
            )
        }

        // Transient info toast-like snackbar replacement
        infoMessage?.let { msg ->
            androidx.compose.material3.Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 20.dp, end = 20.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Text(msg, style = MaterialTheme.typography.bodyMedium)
            }
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(1800)
                infoMessage = null
            }
        }
    }
}
