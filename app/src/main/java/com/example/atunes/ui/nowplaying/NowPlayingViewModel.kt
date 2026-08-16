package com.example.atunes.ui.nowplaying

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atunes.data.model.Track
import com.example.atunes.data.repository.MusicRepository
import com.example.atunes.service.NowPlayingState
import com.example.atunes.service.PlaybackController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NowPlayingViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MusicRepository(app.applicationContext)

    val state: StateFlow<NowPlayingState> = PlaybackController.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NowPlayingState())

    fun togglePlayPause() = PlaybackController.togglePlayPause()
    fun skipNext()        = PlaybackController.skipNext()
    fun skipPrevious()    = PlaybackController.skipPrevious()
    fun seekTo(ms: Long)  = PlaybackController.seekTo(ms)
    fun toggleShuffle()   = PlaybackController.toggleShuffle()
    fun cycleRepeat()     = PlaybackController.cycleRepeat()
    fun updatePosition()  = PlaybackController.updatePosition()

    fun toggleLike(track: Track) {
        viewModelScope.launch { repo.toggleLike(track) }
    }
}
