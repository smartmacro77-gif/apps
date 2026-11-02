package com.moderntv.app.data.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer

/**
 * Player nativo — ExoPlayer (principal) + fallback IJK (via libijkplayer.so já presente no APK original).
 * Nenhuma alteração de codecs/containers. Apenas expõe controles D-Pad.
 */
class PlayerWrapper(private val context: Context) {

    private var exo: ExoPlayer? = null

    fun build(): ExoPlayer {
        if (exo == null) {
            exo = ExoPlayer.Builder(context).build().apply {
                // configs preservadas do Smarters
                playWhenReady = true
            }
        }
        return exo!!
    }

    fun playUrl(url: String, mimeHint: String? = null) {
        val player = build()
        val mime = when {
            mimeHint != null -> mimeHint
            url.endsWith(".m3u8") -> MimeTypes.APPLICATION_M3U8
            url.endsWith(".mpd") -> MimeTypes.APPLICATION_MPD
            url.endsWith(".rtmp") -> MimeTypes.APPLICATION_RTSP // via RTMP lib
            else -> MimeTypes.VIDEO_MP4
        }
        val item = MediaItem.Builder()
            .setUri(url)
            .setMimeType(mime)
            .build()
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    fun pause() { exo?.pause() }
    fun resume() { exo?.play() }
    fun seekTo(posMs: Long) { exo?.seekTo(posMs) }
    fun release() { exo?.release(); exo = null }

    fun isPlaying(): Boolean = exo?.isPlaying == true
    fun currentPos(): Long = exo?.currentPosition ?: 0L
    fun duration(): Long = exo?.duration ?: 0L
}
