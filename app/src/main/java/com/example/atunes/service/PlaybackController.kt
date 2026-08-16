package com.example.atunes.service

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.atunes.data.model.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton wrapper around Media3's [MediaController].
 * Provides a Compose-friendly [StateFlow<NowPlayingState>] that all ViewModels observe.
 */
object PlaybackController {

    private val _state = MutableStateFlow(NowPlayingState())
    val state: StateFlow<NowPlayingState> = _state.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    fun connect(context: Context) {
        if (controller?.isConnected == true) return

        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture!!.addListener({
            controller = controllerFuture!!.get()
            controller?.addListener(playerListener)
        }, MoreExecutors.directExecutor())
    }

    fun disconnect() {
        controller?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFuture ?: return)
        controller = null
    }

    // ── Commands ────────────────────────────────────────────────────────────

    fun play(tracks: List<Track>, startIndex: Int = 0) {
        val items = tracks.map { it.toMediaItem() }
        controller?.apply {
            setMediaItems(items, startIndex, 0)
            prepare()
            play()
        }
        _state.value = _state.value.copy(queue = tracks, queueIndex = startIndex)
    }

    fun togglePlayPause() {
        controller?.let { if (it.isPlaying) it.pause() else it.play() }
    }

    fun skipNext() = controller?.seekToNextMediaItem()
    fun skipPrevious() = controller?.seekToPreviousMediaItem()

    fun seekTo(positionMs: Long) = controller?.seekTo(positionMs)

    fun toggleShuffle() {
        val next = !(controller?.shuffleModeEnabled ?: false)
        controller?.shuffleModeEnabled = next
        _state.value = _state.value.copy(isShuffle = next)
    }

    fun cycleRepeat() {
        val next = ((controller?.repeatMode ?: 0) + 1) % 3
        controller?.repeatMode = next
        _state.value = _state.value.copy(repeatMode = next)
    }

    fun updatePosition() {
        controller?.let { c ->
            _state.value = _state.value.copy(
                positionMs = c.currentPosition,
                durationMs = c.duration.coerceAtLeast(0)
            )
        }
    }

    // ── Player listener ─────────────────────────────────────────────────────

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.value = _state.value.copy(isPlaying = isPlaying)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val idx = controller?.currentMediaItemIndex ?: return
            val q = _state.value.queue
            if (idx < q.size) {
                _state.value = _state.value.copy(track = q[idx], queueIndex = idx)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            controller?.let { c ->
                _state.value = _state.value.copy(
                    positionMs = c.currentPosition,
                    durationMs = c.duration.coerceAtLeast(0)
                )
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun Track.toMediaItem(): MediaItem = MediaItem.Builder()
        .setUri(Uri.parse(contentUri))
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setTrackNumber(trackNumber)
                .build()
        )
        .build()
}
