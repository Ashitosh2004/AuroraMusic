package com.zephyr.musicplayer.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.zephyr.musicplayer.data.auroraDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode { LIGHT, DARK, AMOLED, SEPIA, ROSE, FOREST, SYSTEM }

data class UserPreferences(
    val themeMode: ThemeMode,
    val accentIndex: Int,
    val useDynamicAccent: Boolean,
    val roundAlbumArt: Boolean,
    val miniPlayerStyle: Int,
    val useMiniPlayerBlur: Boolean,
    val listSpacing: Int,
    val showAlbumArtInList: Boolean,
    val sliderStyleIndex: Int,
    val playerStyleIndex: Int
)

object PreferencesRepository {
    private val THEME_KEY = intPreferencesKey("theme_mode")
    private val ACCENT_KEY = intPreferencesKey("accent_index")
    private val DYNAMIC_KEY = intPreferencesKey("use_dynamic_accent")
    private val ROUND_ART_KEY = intPreferencesKey("round_album_art")
    private val MINI_STYLE_KEY = intPreferencesKey("mini_player_style")
    private val MINI_BLUR_KEY = intPreferencesKey("mini_player_blur")
    private val LIST_SPACING_KEY = intPreferencesKey("list_spacing")
    private val SHOW_ART_LIST_KEY = intPreferencesKey("show_album_art_in_list")
    private val SLIDER_STYLE_KEY = intPreferencesKey("slider_style")
    private val PLAYER_STYLE_KEY = intPreferencesKey("player_style")

    fun read(context: Context): Flow<UserPreferences> = context.auroraDataStore.data.map { prefs ->
        UserPreferences(
            themeMode = ThemeMode.entries.getOrElse(prefs[THEME_KEY] ?: ThemeMode.DARK.ordinal) { ThemeMode.DARK },
            accentIndex = prefs[ACCENT_KEY] ?: 0,
            useDynamicAccent = (prefs[DYNAMIC_KEY] ?: 0) == 1,
            roundAlbumArt = (prefs[ROUND_ART_KEY] ?: 1) == 1,
            miniPlayerStyle = prefs[MINI_STYLE_KEY] ?: 0,
            useMiniPlayerBlur = (prefs[MINI_BLUR_KEY] ?: 0) == 1,
            listSpacing = prefs[LIST_SPACING_KEY] ?: 0,
            showAlbumArtInList = (prefs[SHOW_ART_LIST_KEY] ?: 1) == 1,
            sliderStyleIndex = prefs[SLIDER_STYLE_KEY] ?: 0,
            playerStyleIndex = prefs[PLAYER_STYLE_KEY] ?: 0
        )
    }

    suspend fun setTheme(context: Context, mode: ThemeMode) {
        context.auroraDataStore.edit { it[THEME_KEY] = mode.ordinal }
    }

    suspend fun setAccent(context: Context, index: Int) {
        context.auroraDataStore.edit { it[ACCENT_KEY] = index }
    }

    suspend fun setDynamicAccent(context: Context, on: Boolean) {
        context.auroraDataStore.edit { it[DYNAMIC_KEY] = if (on) 1 else 0 }
    }

    suspend fun setRoundAlbumArt(context: Context, on: Boolean) {
        context.auroraDataStore.edit { it[ROUND_ART_KEY] = if (on) 1 else 0 }
    }

    suspend fun setMiniPlayerStyle(context: Context, style: Int) {
        context.auroraDataStore.edit { it[MINI_STYLE_KEY] = style }
    }

    suspend fun setMiniPlayerBlur(context: Context, on: Boolean) {
        context.auroraDataStore.edit { it[MINI_BLUR_KEY] = if (on) 1 else 0 }
    }

    suspend fun setListSpacing(context: Context, spacing: Int) {
        context.auroraDataStore.edit { it[LIST_SPACING_KEY] = spacing }
    }

    suspend fun setShowAlbumArtInList(context: Context, on: Boolean) {
        context.auroraDataStore.edit { it[SHOW_ART_LIST_KEY] = if (on) 1 else 0 }
    }

    suspend fun setSliderStyle(context: Context, index: Int) {
        context.auroraDataStore.edit { it[SLIDER_STYLE_KEY] = index }
    }

    suspend fun setPlayerStyle(context: Context, index: Int) {
        context.auroraDataStore.edit { it[PLAYER_STYLE_KEY] = index }
    }
}
