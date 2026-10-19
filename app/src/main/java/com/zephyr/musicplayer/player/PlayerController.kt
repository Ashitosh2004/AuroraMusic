package com.zephyr.musicplayer.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.zephyr.musicplayer.data.Song
import com.zephyr.musicplayer.service.MusicService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerController(private val context: Context) {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist

    private val _position = MutableStateFlow(0L)
    val position: StateFlow<Long> = _position

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode: StateFlow<Boolean> = _shuffleMode

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    private var currentPlaylist: List<Song> = emptyList()

    fun connect() {
        if (controllerFuture != null) return
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture = future
        future.addListener({
            val ctrl = future.get()
            controller = ctrl
            _isReady.value = true
            ctrl.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val id = mediaItem?.mediaId?.toLongOrNull()
                    _currentSong.value = currentPlaylist.firstOrNull { it.id == id }
                }
                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    _position.value = ctrl.currentPosition
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = ctrl.duration.coerceAtLeast(0)
                        _position.value = ctrl.currentPosition
                    }
                }
                override fun onRepeatModeChanged(repeatMode: Int) {
                    _repeatMode.value = repeatMode
                }
                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    _shuffleMode.value = shuffleModeEnabled
                }
            })
            // initialize current song
            val id = ctrl.currentMediaItem?.mediaId?.toLongOrNull()
            _currentSong.value = currentPlaylist.firstOrNull { it.id == id }
            _isPlaying.value = ctrl.isPlaying
            _duration.value = ctrl.duration.coerceAtLeast(0)
            _position.value = ctrl.currentPosition
            _repeatMode.value = ctrl.repeatMode
            _shuffleMode.value = ctrl.shuffleModeEnabled
        }, MoreExecutors.directExecutor())
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        currentPlaylist = songs
        _playlist.value = songs
        // Build media items and queue on the controller
        val ctrl = controller ?: return
        val items = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.mediaId)
                .setUri(song.data)
                .build()
        }
        ctrl.setMediaItems(items, startIndex, 0L)
        ctrl.prepare()
        ctrl.playWhenReady = true
        _currentSong.value = songs.getOrNull(startIndex)
    }

    fun togglePlayPause() {
        val ctrl = controller ?: return
        if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
    }

    fun next() {
        controller?.seekToNextMediaItem()
    }

    fun previous() {
        controller?.seekToPreviousMediaItem()
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun toggleShuffle() {
        val ctrl = controller ?: return
        ctrl.shuffleModeEnabled = !ctrl.shuffleModeEnabled
    }

    fun cycleRepeatMode() {
        val ctrl = controller ?: return
        ctrl.repeatMode = when (ctrl.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
            else -> Player.REPEAT_MODE_OFF
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        controller?.playbackParameters = androidx.media3.common.PlaybackParameters(speed, 1.0f)
    }

    fun pause() {
        controller?.pause()
    }

    fun play() {
        controller?.play()
    }

    fun release() {
        controller?.release()
        controller = null
        _isReady.value = false
    }
}
