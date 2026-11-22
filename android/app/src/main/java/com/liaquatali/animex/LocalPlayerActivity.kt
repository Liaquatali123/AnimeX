package com.liaquatali.animex

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TrackSelectionDialogBuilder
import com.liaquatali.animex.data.AnimeDatabase
import com.liaquatali.animex.data.WatchHistoryEntity
import kotlinx.coroutines.launch

/** Plays user-selected local videos and stores playback progress on device. */
class LocalPlayerActivity : ComponentActivity() {
    private var player: ExoPlayer? = null
    private var mediaUri: Uri? = null
    private var displayName: String = "Local video"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.data ?: run { finish(); return }
        mediaUri = uri
        displayName = resolveDisplayName(uri)
        val exoPlayer = ExoPlayer.Builder(this).build()
        player = exoPlayer
        val mediaItem = MediaItem.Builder().setUri(uri).apply {
            if (uri.path?.endsWith(".m3u8", ignoreCase = true) == true) setMimeType(MimeTypes.APPLICATION_M3U8)
        }.build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = false

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.BLACK)
        }
        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 8, 12, 8)
        }
        controls.addView(Button(this).apply {
            text = "Audio"
            setOnClickListener {
                TrackSelectionDialogBuilder(this@LocalPlayerActivity, "Audio tracks", exoPlayer, C.TRACK_TYPE_AUDIO).build().show()
            }
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        controls.addView(Button(this).apply {
            text = "Subtitles"
            setOnClickListener {
                TrackSelectionDialogBuilder(this@LocalPlayerActivity, "Subtitle tracks", exoPlayer, C.TRACK_TYPE_TEXT).build().show()
            }
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        root.addView(controls)
        root.addView(PlayerView(this).apply {
            player = exoPlayer
            useController = true
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
        })
        setContentView(root)

        lifecycleScope.launch {
            val saved = AnimeDatabase.get(this@LocalPlayerActivity).watchHistoryDao().find(uri.toString())
            if (saved != null && saved.positionMs > 5_000) exoPlayer.seekTo(saved.positionMs)
            exoPlayer.playWhenReady = true
        }
    }

    private fun resolveDisplayName(uri: Uri): String = try {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) ?: "Local video" else "Local video"
        } ?: "Local video"
    } catch (_: Exception) { uri.lastPathSegment ?: "Local video" }

    private fun saveProgress() {
        val current = player ?: return
        val uri = mediaUri ?: return
        val position = current.currentPosition.coerceAtLeast(0)
        val duration = current.duration.takeIf { it != C.TIME_UNSET && it > 0 } ?: 0L
        lifecycleScope.launch {
            AnimeDatabase.get(this@LocalPlayerActivity).watchHistoryDao().save(
                WatchHistoryEntity(uri.toString(), displayName, position, duration, System.currentTimeMillis())
            )
        }
    }

    override fun onStop() {
        saveProgress()
        player?.pause()
        super.onStop()
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }
}
