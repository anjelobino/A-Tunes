package com.example.atunes.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atunes.service.NowPlayingState
import com.example.atunes.service.PlaybackController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NowPlayingViewModel : ViewModel() {

    val state: StateFlow<NowPlayingState> = PlaybackController.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NowPlayingState())

    fun togglePlayPause() = PlaybackController.togglePlayPause()
    fun skipNext()        = PlaybackController.skipNext()
    fun skipPrevious()    = PlaybackController.skipPrevious()
    fun seekTo(ms: Long)  = PlaybackController.seekTo(ms)
    fun toggleShuffle()   = PlaybackController.toggleShuffle()
    fun cycleRepeat()     = PlaybackController.cycleRepeat()
    fun updatePosition()  = PlaybackController.updatePosition()
}
