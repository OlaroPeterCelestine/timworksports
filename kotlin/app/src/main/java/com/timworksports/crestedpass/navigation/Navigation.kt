package com.timworksports.crestedpass.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timworksports.crestedpass.data.repository.CrestedPassRepository
import com.timworksports.crestedpass.ui.band.BandViewModel
import com.timworksports.crestedpass.ui.discover.DiscoverViewModel
import com.timworksports.crestedpass.ui.embassy.EmbassyViewModel
import com.timworksports.crestedpass.ui.home.HomeViewModel
import com.timworksports.crestedpass.ui.predict.PredictViewModel
import com.timworksports.crestedpass.ui.profile.ProfileViewModel
import com.timworksports.crestedpass.ui.ticket.TicketViewModel
import com.timworksports.crestedpass.ui.trail.TrailViewModel
import com.timworksports.crestedpass.ui.wallet.WalletViewModel

class CrestedPassViewModelFactory(
    private val repository: CrestedPassRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository)
            modelClass.isAssignableFrom(WalletViewModel::class.java) -> WalletViewModel(repository)
            modelClass.isAssignableFrom(PredictViewModel::class.java) -> PredictViewModel(repository)
            modelClass.isAssignableFrom(TrailViewModel::class.java) -> TrailViewModel(repository)
            modelClass.isAssignableFrom(EmbassyViewModel::class.java) -> EmbassyViewModel(repository)
            modelClass.isAssignableFrom(TicketViewModel::class.java) -> TicketViewModel(repository)
            modelClass.isAssignableFrom(DiscoverViewModel::class.java) -> DiscoverViewModel(repository)
            modelClass.isAssignableFrom(BandViewModel::class.java) -> BandViewModel(repository)
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(repository)
            else -> throw IllegalArgumentException("Unknown ViewModel ${modelClass.name}")
        }
        return vm as T
    }
}

object AppGraph {
    const val Home = "home"
    const val Coaches = "coaches"
    const val Events = "events"
    const val Athletes = "athletes"
    const val Shop = "shop"
    const val Wallet = "wallet"
    const val Discover = "discover"
    const val Predict = "predict"
    const val Trail = "trail"
    const val You = "you"
    const val Settings = "settings"
    const val Embassy = "embassy"
    const val Ticket = "ticket"
    const val EventTicket = "event/{eventId}"
    const val ArLens = "ar"
    const val Band = "band"

    fun eventTicket(eventId: String) = "event/$eventId"
}
