package com.zephyr.musicplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zephyr.musicplayer.data.FavoritesRepository
import com.zephyr.musicplayer.data.PlaylistMeta
import com.zephyr.musicplayer.data.PlaylistRepository
import com.zephyr.musicplayer.data.PlaybackSettingsRepository
import com.zephyr.musicplayer.data.MusicRepository
import com.zephyr.musicplayer.data.Song
import com.zephyr.musicplayer.player.PlayerController
import com.zephyr.musicplayer.ui.theme.PreferencesRepository
import com.zephyr.musicplayer.ui.theme.ThemeMode
import com.zephyr.musicplayer.ui.theme.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MusicViewModel(app: Application) : AndroidViewModel(app) {

    private val controller = PlayerController(app)

    val songs = MutableStateFlow<List<Song>>(emptyList())
    val isReady get() = controller.isReady
    val isPlaying get() = controller.isPlaying
    val currentSong get() = controller.currentSong
    val playlist get() = controller.playlist
    val position get() = controller.position
    val duration get() = controller.duration
    val repeatMode get() = controller.repeatMode
    val shuffleMode get() = controller.shuffleMode

    private val _preferences = MutableStateFlow<UserPreferences?>(null)
    val preferences: StateFlow<UserPreferences?> = _preferences

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Favorites
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    val favoriteSongs: StateFlow<List<Song>> get() = _favoriteSongs
    private val _favoriteSongs = MutableStateFlow<List<Song>>(emptyList())

    private fun refreshFavoriteSongs() {
        _favoriteSongs.value = songs.value.filter { it.id.toString() in _favoriteIds.value }
    }

    // Custom playlists
    private val _playlists = MutableStateFlow<List<PlaylistMeta>>(emptyList())
    val playlists: StateFlow<List<PlaylistMeta>> = _playlists.asStateFlow()

    private val _playlistContents = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val playlistContents: StateFlow<Map<String, List<String>>> = _playlistContents.asStateFlow()

    // Playback settings
    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _eqPreset = MutableStateFlow(0)
    val eqPreset: StateFlow<Int> = _eqPreset.asStateFlow()

    private val _bassBoost = MutableStateFlow(0f)
    val bassBoost: StateFlow<Float> = _bassBoost.asStateFlow()

    private val _trebleBoost = MutableStateFlow(0f)
    val trebleBoost: StateFlow<Float> = _trebleBoost.asStateFlow()

    // Sleep timer
    private val _sleepTimerRemainingMs = MutableStateFlow<Long?>(null)
    val sleepTimerRemainingMs: StateFlow<Long?> = _sleepTimerRemainingMs.asStateFlow()

    init {
        controller.connect()
        loadPreferences()
        loadFavorites()
        loadPlaylists()
        loadPlaybackSettings()
    }

    fun setPermissionGranted(granted: Boolean) {
        _permissionGranted.value = granted
        if (granted) loadSongs()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            PreferencesRepository.read(getApplication()).collectLatest { prefs ->
                _preferences.value = prefs
            }
        }
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            val loaded = MusicRepository.loadAllSongs(getApplication())
            songs.value = loaded
            _isLoading.value = false
            refreshFavoriteSongs()
            refreshPlaylistSongs()
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            FavoritesRepository.read(getApplication()).collectLatest { fav ->
                _favoriteIds.value = fav
                refreshFavoriteSongs()
            }
        }
    }

    private fun refreshPlaylistSongs() {
        // Trigger any UI that depends on playlist content refresh
        _playlistContents.value = _playlistContents.value.toMap()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            PlaylistRepository.readIndex(getApplication()).collectLatest { meta ->
                _playlists.value = meta
                // Snapshot current contents for each playlist (non-blocking)
                val contents = mutableMapOf<String, List<String>>()
                for (m in meta) {
                    try {
                        val list = PlaylistRepository.readContent(getApplication(), m.id).first()
                        contents[m.id] = list
                    } catch (e: Exception) {
                        contents[m.id] = emptyList()
                    }
                }
                _playlistContents.value = contents.toMap()
            }
        }
        // Also re-fetch contents whenever a playlist key changes by re-reading index periodically
        // (DataStore emits on every write, so the index flow will re-emit)
    }

    private fun loadPlaybackSettings() {
        viewModelScope.launch {
            PlaybackSettingsRepository.readSpeed(getApplication()).collectLatest {
                _playbackSpeed.value = it
                try { controller.setPlaybackSpeed(it) } catch (_: Exception) {}
            }
        }
        viewModelScope.launch {
            PlaybackSettingsRepository.readEqPreset(getApplication()).collectLatest {
                _eqPreset.value = it
            }
        }
        viewModelScope.launch {
            PlaybackSettingsRepository.readBass(getApplication()).collectLatest {
                _bassBoost.value = it
            }
        }
        viewModelScope.launch {
            PlaybackSettingsRepository.readTreble(getApplication()).collectLatest {
                _trebleBoost.value = it
            }
        }
    }

    fun refresh() {
        if (_permissionGranted.value) loadSongs()
    }

    fun playSongs(songsList: List<Song>, startIndex: Int = 0) {
        controller.playSongs(songsList, startIndex)
    }

    fun togglePlayPause() = controller.togglePlayPause()
    fun next() = controller.next()
    fun previous() = controller.previous()
    fun seekTo(pos: Long) = controller.seekTo(pos)
    fun toggleShuffle() = controller.toggleShuffle()
    fun cycleRepeatMode() = controller.cycleRepeatMode()

    fun setSearchQuery(q: String) { _searchQuery.value = q }

    // ---- Favorites ----
    fun toggleFavorite(song: Song, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val added = FavoritesRepository.toggle(getApplication(), song.id.toString())
            onDone(added)
        }
    }

    fun isFavorite(song: Song): Boolean = song.id.toString() in _favoriteIds.value

    // ---- Custom playlists ----
    fun createPlaylist(name: String, onDone: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = PlaylistRepository.createPlaylist(getApplication(), name)
            onDone(id)
        }
    }

    fun deletePlaylist(id: String) {
        viewModelScope.launch {
            PlaylistRepository.deletePlaylist(getApplication(), id)
        }
    }

    fun renamePlaylist(id: String, newName: String) {
        viewModelScope.launch {
            PlaylistRepository.renamePlaylist(getApplication(), id, newName)
        }
    }

    fun addSongToPlaylist(playlistId: String, song: Song) {
        viewModelScope.launch {
            PlaylistRepository.addSong(getApplication(), playlistId, song.id.toString())
        }
    }

    fun removeSongFromPlaylist(playlistId: String, song: Song) {
        viewModelScope.launch {
            PlaylistRepository.removeSong(getApplication(), playlistId, song.id.toString())
        }
    }

    fun playPlaylist(playlistId: String) {
        viewModelScope.launch {
            val ids = _playlistContents.value[playlistId] ?: return@launch
            val songsToPlay = songs.value.filter { it.id.toString() in ids }
            if (songsToPlay.isNotEmpty()) {
                controller.playSongs(songsToPlay, 0)
            }
        }
    }

    fun songsInPlaylist(playlistId: String): List<Song> {
        val ids = _playlistContents.value[playlistId] ?: return emptyList()
        return songs.value.filter { it.id.toString() in ids }
    }

    // ---- Playback settings ----
    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            PlaybackSettingsRepository.setSpeed(getApplication(), speed)
            controller.setPlaybackSpeed(speed)
            _playbackSpeed.value = speed
        }
    }

    fun setEqPreset(preset: Int) {
        viewModelScope.launch {
            PlaybackSettingsRepository.setEqPreset(getApplication(), preset)
            _eqPreset.value = preset
        }
    }

    fun setBassBoost(value: Float) {
        viewModelScope.launch {
            PlaybackSettingsRepository.setBass(getApplication(), value)
            _bassBoost.value = value
        }
    }

    fun setTrebleBoost(value: Float) {
        viewModelScope.launch {
            PlaybackSettingsRepository.setTreble(getApplication(), value)
            _trebleBoost.value = value
        }
    }

    // ---- Sleep timer ----
    fun startSleepTimer(durationMs: Long) {
        viewModelScope.launch {
            _sleepTimerRemainingMs.value = durationMs
            var remaining = durationMs
            while (remaining > 0) {
                kotlinx.coroutines.delay(1000)
                remaining -= 1000
                _sleepTimerRemainingMs.value = remaining.coerceAtLeast(0)
            }
            // Stop playback
            controller.pause()
            _sleepTimerRemainingMs.value = null
        }
    }

    fun cancelSleepTimer() {
        _sleepTimerRemainingMs.value = null
    }

    // ---- Preference updates ----
    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            PreferencesRepository.setTheme(getApplication(), mode)
        }
    }

    fun setAccent(index: Int) {
        viewModelScope.launch {
            PreferencesRepository.setAccent(getApplication(), index)
        }
    }

    fun setDynamicAccent(on: Boolean) {
        viewModelScope.launch {
            PreferencesRepository.setDynamicAccent(getApplication(), on)
        }
    }

    fun setRoundAlbumArt(on: Boolean) {
        viewModelScope.launch {
            PreferencesRepository.setRoundAlbumArt(getApplication(), on)
        }
    }

    fun setMiniPlayerStyle(style: Int) {
        viewModelScope.launch {
            PreferencesRepository.setMiniPlayerStyle(getApplication(), style)
        }
    }

    fun setMiniPlayerBlur(on: Boolean) {
        viewModelScope.launch {
            PreferencesRepository.setMiniPlayerBlur(getApplication(), on)
        }
    }

    fun setListSpacing(spacing: Int) {
        viewModelScope.launch {
            PreferencesRepository.setListSpacing(getApplication(), spacing)
        }
    }

    fun setShowAlbumArtInList(on: Boolean) {
        viewModelScope.launch {
            PreferencesRepository.setShowAlbumArtInList(getApplication(), on)
        }
    }

    fun setSliderStyle(index: Int) {
        viewModelScope.launch {
            PreferencesRepository.setSliderStyle(getApplication(), index)
        }
    }

    fun setPlayerStyle(index: Int) {
        viewModelScope.launch {
            PreferencesRepository.setPlayerStyle(getApplication(), index)
        }
    }

    override fun onCleared() {
        controller.release()
        super.onCleared()
    }
}

