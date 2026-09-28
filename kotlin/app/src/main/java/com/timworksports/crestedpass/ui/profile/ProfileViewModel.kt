package com.timworksports.crestedpass.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timworksports.crestedpass.data.model.AppSnapshot
import com.timworksports.crestedpass.data.model.Shot
import com.timworksports.crestedpass.data.repository.CrestedPassRepository
import com.timworksports.crestedpass.data.repository.MockRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: CrestedPassRepository
) : ViewModel() {
    val uiState: StateFlow<AppSnapshot> = repository.observeSnapshot().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MockRepository.initialSnapshot()
    )

    fun points(snapshot: AppSnapshot): Int =
        snapshot.leaderboard.firstOrNull { it.name == snapshot.user.name }?.points ?: 0

    fun myShots(snapshot: AppSnapshot): List<Shot> =
        snapshot.shots.filter { it.author == snapshot.user.name }

    fun myPostImages(snapshot: AppSnapshot): List<Pair<String, String>> {
        val shots = myShots(snapshot).map { it.id to it.imageName }
        val reels = snapshot.reels.filter { it.author == snapshot.user.name }.map { it.id to it.imageName }
        return shots + reels
    }

    fun toggleLanguage() {
        viewModelScope.launch { repository.toggleLanguage() }
    }

    fun freezeBand() {
        viewModelScope.launch { repository.freezeBand() }
    }

    fun toggleShotLike(id: String) {
        viewModelScope.launch { repository.toggleShotLike(id) }
    }
}
