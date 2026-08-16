package com.example.atunes.service

import com.example.atunes.data.model.Track

/**
 * Immutable snapshot of the current playback state.
 * Emitted via StateFlow so all UI layers can observe without coupling to the service.
 */
data class NowPlayingState(
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: Int = 0,       // 0 = OFF, 1 = ONE, 2 = ALL
    val queue: List<Track> = emptyList(),
    val queueIndex: Int = 0
) {
    val progress: Float
        get() = if (durationMs > 0) positionMs / durationMs.toFloat() else 0f
}
