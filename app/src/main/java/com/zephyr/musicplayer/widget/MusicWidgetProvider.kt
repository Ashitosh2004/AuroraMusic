package com.zephyr.musicplayer.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.widget.RemoteViews
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.zephyr.musicplayer.MainActivity
import com.zephyr.musicplayer.R

class MusicWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            updateWidgetInternal(context, appWidgetManager, id, null, false)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PLAY_PAUSE -> sendCommandToService(context, ACTION_PLAY_PAUSE)
            ACTION_NEXT -> sendCommandToService(context, ACTION_NEXT)
            ACTION_PREV -> sendCommandToService(context, ACTION_PREV)
        }
    }

    private fun sendCommandToService(context: Context, action: String) {
        val intent = Intent(action).apply {
            component = ComponentName(context, WIDGET_RECEIVER_COMPONENT)
            setPackage(context.packageName)
        }
        context.sendBroadcast(intent)
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.zephyr.musicplayer.PLAY_PAUSE"
        const val ACTION_NEXT = "com.zephyr.musicplayer.NEXT"
        const val ACTION_PREV = "com.zephyr.musicplayer.PREV"
        const val WIDGET_RECEIVER_COMPONENT = "com.zephyr.musicplayer.MusicWidgetReceiver"

        fun updateWidget(context: Context, player: ExoPlayer?) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, MusicWidgetProvider::class.java))
            val title = player?.currentMediaItem?.mediaId?.let {
                // Fallback: just show "Playing" if we can't resolve title
                "Aurora Music"
            } ?: "Aurora Music"
            val isPlaying = player?.isPlaying == true
            ids.forEach { id ->
                updateWidgetInternal(context, mgr, id, title, isPlaying)
            }
        }

        private fun updateWidgetInternal(
            context: Context,
            mgr: AppWidgetManager,
            widgetId: Int,
            title: String?,
            isPlaying: Boolean
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_music)

            views.setTextViewText(R.id.widget_title, title ?: "Aurora Music")
            views.setTextViewText(R.id.widget_subtitle, "Tap to open")
            views.setImageViewResource(
                R.id.widget_play_pause,
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )

            // Click on container → open app
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openPi = PendingIntent.getActivity(
                context, 0, openIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_root, openPi)

            // Click on play/pause
            val ppIntent = Intent(context, MusicWidgetProvider::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            val ppPi = PendingIntent.getBroadcast(
                context, 1, ppIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_play_pause, ppPi)

            // Next
            val nextIntent = Intent(context, MusicWidgetProvider::class.java).apply {
                action = ACTION_NEXT
            }
            val nextPi = PendingIntent.getBroadcast(
                context, 2, nextIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_next, nextPi)

            // Previous
            val prevIntent = Intent(context, MusicWidgetProvider::class.java).apply {
                action = ACTION_PREV
            }
            val prevPi = PendingIntent.getBroadcast(
                context, 3, prevIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_prev, prevPi)

            mgr.updateAppWidget(widgetId, views)
        }
    }
}
