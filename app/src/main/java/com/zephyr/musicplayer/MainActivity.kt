package com.zephyr.musicplayer

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.zephyr.musicplayer.ui.screens.splash.SplashScreen
import com.zephyr.musicplayer.ui.theme.AuroraTheme
import com.zephyr.musicplayer.ui.theme.accentOptions
import com.zephyr.musicplayer.viewmodel.MusicViewModel
import com.zephyr.musicplayer.ui.MainNavigation
import com.zephyr.musicplayer.ui.theme.PreferencesRepository
import com.zephyr.musicplayer.ui.theme.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.any { it }
        viewModel.setPermissionGranted(granted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs by viewModel.preferences.collectAsState(initial = null)
            val currentPrefs = prefs
            val accentColor = remember(currentPrefs) {
                accentOptions.getOrElse(currentPrefs?.accentIndex ?: 0) { accentOptions[0] }.color
            }
            val themeMode = currentPrefs?.themeMode ?: ThemeMode.DARK

            var showSplash by remember { mutableStateOf(true) }

            AuroraTheme(themeMode = themeMode, accentColor = accentColor) {
                if (showSplash) {
                    SplashScreen(onFinished = {
                        // Idempotent — safe to call multiple times
                        showSplash = false
                    })
                } else {
                    MainNavigation(viewModel = viewModel)
                }
            }
        }
        requestAudioPermission()
    }

    private fun requestAudioPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permission)
    }
}
