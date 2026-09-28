package com.timworksports.crestedpass.ui.predict

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timworksports.crestedpass.data.model.AppSnapshot
import com.timworksports.crestedpass.data.repository.CrestedPassRepository
import com.timworksports.crestedpass.data.repository.MockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PredictUiState(
    val snapshot: AppSnapshot,
    val homeScore: Int,
    val awayScore: Int
)

class PredictViewModel(
    private val repository: CrestedPassRepository
) : ViewModel() {
    private val homeScore = MutableStateFlow(1)
    private val awayScore = MutableStateFlow(1)

    val uiState: StateFlow<PredictUiState> = combine(
        repository.observeSnapshot(),
        homeScore,
        awayScore
    ) { snapshot, home, away ->
        PredictUiState(snapshot, home, away)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PredictUiState(MockRepository.initialSnapshot(), 1, 1)
    )

    fun bumpHome(delta: Int) {
        homeScore.value = (homeScore.value + delta).coerceIn(0, 9)
    }

    fun bumpAway(delta: Int) {
        awayScore.value = (awayScore.value + delta).coerceIn(0, 9)
    }

    fun submit() {
        viewModelScope.launch {
            repository.submitPrediction(homeScore.value, awayScore.value)
        }
    }
}
