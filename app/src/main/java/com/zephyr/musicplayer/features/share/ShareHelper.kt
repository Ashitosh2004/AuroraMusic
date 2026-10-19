package com.zephyr.musicplayer.features.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.zephyr.musicplayer.data.Song
import java.io.File

/**
 * Real share implementation.
 * Shares the actual audio file via Android's ACTION_SEND intent using a content:// Uri.
 * Falls back to sharing just the song title if the file cannot be resolved.
 */
object ShareHelper {

    fun shareSong(context: Context, song: Song) {
        val file = File(song.data)
        if (!file.exists()) {
            shareText(context, song)
            return
        }
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, song.title)
                putExtra(Intent.EXTRA_TEXT, "${song.title} — ${song.artist}\nShared via Aurora Music")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share \"${song.title}\""))
        } catch (e: Exception) {
            // FileProvider not configured or permission denied — share text only
            shareText(context, song)
        }
    }

    private fun shareText(context: Context, song: Song) {
        val text = "Listening to \"${song.title}\" by ${song.artist} via Aurora Music"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, song.title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share \"${song.title}\""))
    }
}
