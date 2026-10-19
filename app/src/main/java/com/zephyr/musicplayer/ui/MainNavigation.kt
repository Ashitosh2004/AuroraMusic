package com.zephyr.musicplayer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zephyr.musicplayer.ui.components.FloatingNavigationBar
import com.zephyr.musicplayer.ui.components.MiniPlayer
import com.zephyr.musicplayer.ui.screens.FavoritesScreen
import com.zephyr.musicplayer.ui.screens.HomeScreen
import com.zephyr.musicplayer.ui.screens.LibraryScreen
import com.zephyr.musicplayer.ui.screens.NowPlayingScreen
import com.zephyr.musicplayer.ui.screens.PlaylistsScreen
import com.zephyr.musicplayer.ui.screens.SettingsScreen
import com.zephyr.musicplayer.viewmodel.MusicViewModel

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Library : Screen("library", "Library")
    data object Playlists : Screen("playlists", "Playlists")
    data object Favorites : Screen("favorites", "Favorites")
    data object Settings : Screen("settings", "Settings")
    data object NowPlaying : Screen("now_playing", "Now Playing")
}

@Composable
fun MainNavigation(viewModel: MusicViewModel) {
    val navController = rememberNavController()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val showMiniPlayer = currentSong != null

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showNowPlaying by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            // Hide bottom bar on the Now Playing full screen
            if (currentRoute != Screen.NowPlaying.route) {
                Column {
                    AnimatedVisibility(
                        visible = showMiniPlayer,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ) {
                        MiniPlayer(
                            song = currentSong!!,
                            isPlaying = isPlaying,
                            onPlayPause = { viewModel.togglePlayPause() },
                            onNext = { viewModel.next() },
                            onClick = {
                                navController.navigate(Screen.NowPlaying.route)
                            }
                        )
                    }
                    FloatingNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    contentPadding = innerPadding,
                    onSongClick = { song, list ->
                        viewModel.playSongs(list, list.indexOf(song))
                    }
                )
            }
            composable(Screen.Library.route) {
                LibraryScreen(
                    viewModel = viewModel,
                    contentPadding = innerPadding,
                    onSongClick = { song, list ->
                        viewModel.playSongs(list, list.indexOf(song))
                    }
                )
            }
            composable(Screen.Playlists.route) {
                PlaylistsScreen(
                    viewModel = viewModel,
                    contentPadding = innerPadding,
                    onSongClick = { song, list ->
                        viewModel.playSongs(list, list.indexOf(song))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    contentPadding = innerPadding,
                    onSongClick = { song, list ->
                        viewModel.playSongs(list, list.indexOf(song))
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel, contentPadding = innerPadding)
            }
            composable(Screen.NowPlaying.route) {
                NowPlayingScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
