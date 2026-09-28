package com.timworksports.crestedpass.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timworksports.crestedpass.data.model.AppSnapshot
import com.timworksports.crestedpass.data.repository.CrestedPassRepository
import com.timworksports.crestedpass.data.repository.MockRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val repository: CrestedPassRepository
) : ViewModel() {
    val uiState: StateFlow<AppSnapshot> = repository.observeSnapshot().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MockRepository.initialSnapshot()
    )

    fun toggleLike(id: String) {
        viewModelScope.launch { repository.toggleReelLike(id) }
    }

    fun addToTrail(id: String) {
        viewModelScope.launch { repository.addDestinationToTrail(id) }
    }

    fun createEvent(title: String, sport: String, city: String, venue: String, date: String, time: String) {
        viewModelScope.launch { repository.createEvent(title, sport, city, venue, date, time) }
    }
}
