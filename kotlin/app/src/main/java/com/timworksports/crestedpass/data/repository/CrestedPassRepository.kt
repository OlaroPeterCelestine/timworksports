package com.timworksports.crestedpass.data.repository

import com.timworksports.crestedpass.data.model.AppSnapshot
import kotlinx.coroutines.flow.Flow

interface CrestedPassRepository {
    fun observeSnapshot(): Flow<AppSnapshot>
    suspend fun topUp(amount: Int)
    suspend fun submitPrediction(homeScore: Int, awayScore: Int)
    suspend fun settle(memberId: String)
    suspend fun dismissSafetyAlert()
    suspend fun collectNextStamp()
    suspend fun toggleLanguage()
    suspend fun toggleReelLike(id: String)
    suspend fun toggleShotLike(id: String)
    suspend fun addDestinationToTrail(id: String)
    suspend fun freezeBand()
    suspend fun reissueBand()
    suspend fun simulateBandTap()
    suspend fun buyTicket(eventId: String)
}
