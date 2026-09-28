package com.timworksports.crestedpass.ui.wallet

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

data class WalletUiState(
    val snapshot: AppSnapshot,
    val showTopUp: Boolean,
    val confirmedAmount: Int?
)

class WalletViewModel(
    private val repository: CrestedPassRepository
) : ViewModel() {
    private val showTopUp = MutableStateFlow(false)
    private val confirmedAmount = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<WalletUiState> = combine(
        repository.observeSnapshot(),
        showTopUp,
        confirmedAmount
    ) { snapshot, open, confirmed ->
        WalletUiState(snapshot, open, confirmed)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        WalletUiState(MockRepository.initialSnapshot(), false, null)
    )

    fun openTopUp() {
        confirmedAmount.value = null
        showTopUp.value = true
    }

    fun closeTopUp() {
        showTopUp.value = false
    }

    fun topUp(amount: Int) {
        viewModelScope.launch {
            repository.topUp(amount)
            confirmedAmount.value = amount
            showTopUp.value = false
        }
    }

    fun clearConfirmation() {
        confirmedAmount.value = null
    }
}
