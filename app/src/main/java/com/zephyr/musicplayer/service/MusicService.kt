package com.zephyr.musicplayer.service

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.zephyr.musicplayer.MainActivity
import com.zephyr.musicplayer.data.MusicRepository
import com.zephyr.musicplayer.data.Song
import com.zephyr.musicplayer.widget.MusicWidgetProvider

class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        updateWidget()
                    }
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        updateWidget()
                    }
                })
            }

        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            ?: Intent(this, MainActivity::class.java)
        val sessionActivityIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player!!)
            .setSessionActivity(sessionActivityIntent)
            .build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    private fun updateWidget() {
        MusicWidgetProvider.updateWidget(this, player)
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        val p = player ?: return
        p.clearMediaItems()
        val items = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.mediaId)
                .setUri(song.data)
                .build()
        }
        p.setMediaItems(items, startIndex, 0L)
        p.prepare()
        p.playWhenReady = true
    }
}
