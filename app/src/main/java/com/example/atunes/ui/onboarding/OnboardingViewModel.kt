package com.example.atunes.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atunes.data.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ScanState {
    object Idle : ScanState()
    data class Scanning(val count: Int) : ScanState()
    data class Done(val total: Int) : ScanState()
    data class Error(val message: String) : ScanState()
}

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MusicRepository(app.applicationContext)

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState

    fun startScan(folderFilter: String? = null) {
        viewModelScope.launch {
            _scanState.value = ScanState.Scanning(0)
            try {
                val total = repo.scanLibrary(folderFilter = folderFilter) { count ->
                    _scanState.value = ScanState.Scanning(count)
                }
                _scanState.value = ScanState.Done(total)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
