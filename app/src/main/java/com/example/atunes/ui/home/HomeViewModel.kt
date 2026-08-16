package com.example.atunes.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atunes.data.db.AlbumSummary
import com.example.atunes.data.model.Track
import com.example.atunes.data.repository.MusicRepository
import com.example.atunes.service.NowPlayingState
import com.example.atunes.service.PlaybackController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.app.PendingIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MusicRepository(app.applicationContext)

    private val _deletePermissionRequest = MutableSharedFlow<PendingIntent>()
    val deletePermissionRequest = _deletePermissionRequest.asSharedFlow()

    val recentTracks: StateFlow<List<Track>> = repo.getRecentTracks(20)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val albums: StateFlow<List<AlbumSummary>> = repo.getAlbums()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nowPlaying: StateFlow<NowPlayingState> = PlaybackController.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NowPlayingState())

    fun playTrack(tracks: List<Track>, index: Int) {
        PlaybackController.play(tracks, index)
    }

    fun togglePlayPause() = PlaybackController.togglePlayPause()

    fun toggleLike(track: Track) {
        viewModelScope.launch { repo.toggleLike(track) }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            try {
                val intent = repo.deleteTrack(track)
                if (intent != null) {
                    _deletePermissionRequest.emit(intent)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
