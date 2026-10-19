package com.zephyr.musicplayer.data

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val data: String,
    val albumId: Long,
    val dateAdded: Long,
    val artUri: Uri = Uri.EMPTY
) {
    val mediaId: String get() = id.toString()
    val formattedDuration: String
        get() {
            val totalSec = duration / 1000
            val m = totalSec / 60
            val s = totalSec % 60
            return if (m >= 60) {
                val h = m / 60
                val mm = m % 60
                String.format("%d:%02d:%02d", h, mm, s)
            } else {
                String.format("%d:%02d", m, s)
            }
        }
}
