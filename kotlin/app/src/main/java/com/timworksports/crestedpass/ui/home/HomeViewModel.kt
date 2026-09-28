package com.timworksports.crestedpass.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timworksports.crestedpass.data.model.AppSnapshot
import com.timworksports.crestedpass.data.repository.CrestedPassRepository
import com.timworksports.crestedpass.data.repository.MockRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: CrestedPassRepository
) : ViewModel() {
    val uiState: StateFlow<AppSnapshot> = repository.observeSnapshot().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MockRepository.initialSnapshot()
    )

    fun toggleLanguage() {
        viewModelScope.launch { repository.toggleLanguage() }
    }

    fun toggleLike(id: String) {
        viewModelScope.launch { repository.toggleReelLike(id) }
    }

    fun toggleShotLike(id: String) {
        viewModelScope.launch { repository.toggleShotLike(id) }
    }

    fun addToTrail(id: String) {
        viewModelScope.launch { repository.addDestinationToTrail(id) }
    }
}
