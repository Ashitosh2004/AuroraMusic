package com.zephyr.musicplayer.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FavoritesRepository {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_song_ids")

    fun read(context: Context): Flow<Set<String>> =
        context.auroraDataStore.data.map { it[FAVORITES_KEY] ?: emptySet() }

    suspend fun toggle(context: Context, songId: String): Boolean {
        var added = false
        context.auroraDataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            added = songId !in current
            prefs[FAVORITES_KEY] = if (added) current + songId else current - songId
        }
        return added
    }

    suspend fun add(context: Context, songId: String) {
        context.auroraDataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            prefs[FAVORITES_KEY] = current + songId
        }
    }
}

/**
 * Custom playlists are stored as a serialized map.
 * Key format: "playlist_<id>" -> "name|songId1,songId2,..."
 */
object PlaylistRepository {
    private val PLAYLISTS_INDEX_KEY = stringPreferencesKey("playlists_index")
    private fun playlistKey(id: String) = stringPreferencesKey("playlist_content_$id")
    private val playlistIdCounter = intPreferencesKey("playlist_id_counter")

    fun readIndex(context: Context): Flow<List<PlaylistMeta>> =
        context.auroraDataStore.data.map { prefs ->
            (prefs[PLAYLISTS_INDEX_KEY] ?: "")
                .split("|")
                .filter { it.isNotBlank() }
                .map { entry ->
                    val parts = entry.split("::", limit = 2)
                    PlaylistMeta(id = parts[0], name = parts.getOrNull(1) ?: "Untitled")
                }
        }

    fun readContent(context: Context, id: String): Flow<List<String>> =
        context.auroraDataStore.data.map { prefs ->
            (prefs[playlistKey(id)] ?: "")
                .split(",")
                .filter { it.isNotBlank() }
        }

    suspend fun createPlaylist(context: Context, name: String): String {
        var newId = ""
        context.auroraDataStore.edit { prefs ->
            val counter = (prefs[playlistIdCounter] ?: 0) + 1
            prefs[playlistIdCounter] = counter
            newId = "p$counter"
            val currentIndex = prefs[PLAYLISTS_INDEX_KEY] ?: ""
            val entry = "$newId::$name"
            prefs[PLAYLISTS_INDEX_KEY] = if (currentIndex.isBlank()) entry else "$currentIndex|$entry"
            prefs[playlistKey(newId)] = ""
        }
        return newId
    }

    suspend fun renamePlaylist(context: Context, id: String, newName: String) {
        context.auroraDataStore.edit { prefs ->
            val currentIndex = prefs[PLAYLISTS_INDEX_KEY] ?: return@edit
            val updated = currentIndex.split("|").joinToString("|") { entry ->
                val parts = entry.split("::", limit = 2)
                if (parts[0] == id) "$id::$newName" else entry
            }
            prefs[PLAYLISTS_INDEX_KEY] = updated
        }
    }

    suspend fun deletePlaylist(context: Context, id: String) {
        context.auroraDataStore.edit { prefs ->
            val currentIndex = prefs[PLAYLISTS_INDEX_KEY] ?: return@edit
            val updated = currentIndex.split("|").filterNot { it.startsWith("$id::") }.joinToString("|")
            prefs[PLAYLISTS_INDEX_KEY] = updated
            prefs.remove(playlistKey(id))
        }
    }

    suspend fun addSong(context: Context, playlistId: String, songId: String) {
        context.auroraDataStore.edit { prefs ->
            val current = prefs[playlistKey(playlistId)] ?: ""
            if (songId !in current.split(",")) {
                prefs[playlistKey(playlistId)] = if (current.isBlank()) songId else "$current,$songId"
            }
        }
    }

    suspend fun removeSong(context: Context, playlistId: String, songId: String) {
        context.auroraDataStore.edit { prefs ->
            val current = prefs[playlistKey(playlistId)] ?: return@edit
            val updated = current.split(",").filter { it != songId }.joinToString(",")
            prefs[playlistKey(playlistId)] = updated
        }
    }

    suspend fun clearPlaylist(context: Context, playlistId: String) {
        context.auroraDataStore.edit { prefs ->
            prefs[playlistKey(playlistId)] = ""
        }
    }
}

data class PlaylistMeta(val id: String, val name: String)

object PlaybackSettingsRepository {
    private val SPEED_KEY = floatPreferencesKey("playback_speed")
    private val EQ_PRESET_KEY = intPreferencesKey("eq_preset")
    private val BASS_KEY = floatPreferencesKey("bass_boost")
    private val TREBLE_KEY = floatPreferencesKey("treble_boost")

    fun readSpeed(context: Context): Flow<Float> =
        context.auroraDataStore.data.map { it[SPEED_KEY] ?: 1.0f }

    suspend fun setSpeed(context: Context, speed: Float) {
        context.auroraDataStore.edit { it[SPEED_KEY] = speed }
    }

    fun readEqPreset(context: Context): Flow<Int> =
        context.auroraDataStore.data.map { it[EQ_PRESET_KEY] ?: 0 }

    suspend fun setEqPreset(context: Context, preset: Int) {
        context.auroraDataStore.edit { it[EQ_PRESET_KEY] = preset }
    }

    fun readBass(context: Context): Flow<Float> =
        context.auroraDataStore.data.map { it[BASS_KEY] ?: 0f }

    suspend fun setBass(context: Context, value: Float) {
        context.auroraDataStore.edit { it[BASS_KEY] = value }
    }

    fun readTreble(context: Context): Flow<Float> =
        context.auroraDataStore.data.map { it[TREBLE_KEY] ?: 0f }

    suspend fun setTreble(context: Context, value: Float) {
        context.auroraDataStore.edit { it[TREBLE_KEY] = value }
    }
}
