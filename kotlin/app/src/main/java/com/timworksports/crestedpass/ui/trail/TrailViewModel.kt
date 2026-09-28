package com.timworksports.crestedpass.ui.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timworksports.crestedpass.data.model.AppSnapshot
import com.timworksports.crestedpass.data.repository.CrestedPassRepository
import com.timworksports.crestedpass.data.repository.MockRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrailViewModel(
    private val repository: CrestedPassRepository
) : ViewModel() {
    val uiState: StateFlow<AppSnapshot> = repository.observeSnapshot().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MockRepository.initialSnapshot()
    )

    fun collectNext() {
        viewModelScope.launch { repository.collectNextStamp() }
    }
}
