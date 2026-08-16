package com.example.atunes.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atunes.data.model.Track
import com.example.atunes.data.repository.MusicRepository
import com.example.atunes.service.PlaybackController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MusicRepository(app.applicationContext)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<List<Track>> = _query
        .debounce(200)
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList())
            else repo.searchTracks(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    fun onQueryChanged(q: String) {
        _query.value = q
    }

    fun onSearchSubmitted(q: String) {
        if (q.isBlank()) return
        _recentSearches.value = listOf(q) + _recentSearches.value.take(9)
    }

    fun playTrack(tracks: List<Track>, index: Int) {
        PlaybackController.play(tracks, index)
    }
}
