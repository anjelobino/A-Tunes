package com.example.atunes.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atunes.data.db.AlbumSummary
import com.example.atunes.data.db.ArtistSummary
import com.example.atunes.data.model.Playlist
import com.example.atunes.data.model.Track
import com.example.atunes.data.repository.MusicRepository
import com.example.atunes.service.PlaybackController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MusicRepository(app.applicationContext)

    val playlists: StateFlow<List<Playlist>> = repo.playlistDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedTracks: StateFlow<List<Track>> = repo.getLikedTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val artists: StateFlow<List<ArtistSummary>> = repo.getArtists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val albums: StateFlow<List<AlbumSummary>> = repo.getAlbums()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTracks: StateFlow<List<Track>> = repo.getAllTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun playTracks(tracks: List<Track>, index: Int = 0) {
        PlaybackController.play(tracks, index)
    }

    fun toggleLike(track: Track) {
        viewModelScope.launch { repo.toggleLike(track) }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repo.playlistDao.insertPlaylist(
                com.example.atunes.data.model.Playlist(name = name)
            )
        }
    }
}
