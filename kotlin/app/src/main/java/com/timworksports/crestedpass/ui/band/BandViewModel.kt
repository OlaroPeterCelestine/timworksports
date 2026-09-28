package com.timworksports.crestedpass.ui.band

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timworksports.crestedpass.data.model.AppSnapshot
import com.timworksports.crestedpass.data.repository.CrestedPassRepository
import com.timworksports.crestedpass.data.repository.MockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BandViewModel(
    private val repository: CrestedPassRepository
) : ViewModel() {
    val uiState: StateFlow<AppSnapshot> = repository.observeSnapshot().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MockRepository.initialSnapshot()
    )

    private val _lastTap = MutableStateFlow<String?>(null)
    val lastTap: StateFlow<String?> = _lastTap.asStateFlow()

    fun simulateTap() {
        viewModelScope.launch {
            repository.simulateBandTap()
            val snap = uiState.value
            _lastTap.value = if (snap.crestedBand.status == "active") {
                "Read ${snap.crestedBand.bandId} · wallet UGX ${snap.user.walletBalance}"
            } else {
                "Band ${snap.crestedBand.bandId} is frozen"
            }
        }
    }

    fun freeze() {
        viewModelScope.launch { repository.freezeBand() }
    }

    fun reissue() {
        viewModelScope.launch {
            repository.reissueBand()
            _lastTap.value = "Reissued band"
        }
    }
}
