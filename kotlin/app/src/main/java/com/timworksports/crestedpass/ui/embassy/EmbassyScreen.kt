package com.timworksports.crestedpass.ui.embassy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.data.model.SquadStatus
import com.timworksports.crestedpass.data.model.asUgx
import com.timworksports.crestedpass.data.model.label
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PillButton
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.components.StatusDot
import com.timworksports.crestedpass.ui.theme.Cream
import com.timworksports.crestedpass.ui.theme.Gold
import com.timworksports.crestedpass.ui.theme.LockedGray
import com.timworksports.crestedpass.ui.theme.Navy
import com.timworksports.crestedpass.ui.theme.StampGreen

@Composable
fun EmbassyScreen(
    factory: CrestedPassViewModelFactory,
    vm: EmbassyViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ScreenHeader("Fan Embassy", "Desks for visiting supporters")
        Spacer(Modifier.height(16.dp))
        state.embassies.forEach { place ->
            BrandCard {
                GoldLabel("Location")
                Spacer(Modifier.height(4.dp))
                Text(place.name, color = MaterialTheme.colorScheme.onBackground, fontSize = 17.sp)
                Caption("${place.area} · ${place.detail}")
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(12.dp))
        GoldLabel("Squad")
        Spacer(Modifier.height(10.dp))
        Caption("Linked friends for this match. Statuses are mocked — no live location.")
        Spacer(Modifier.height(10.dp))
        state.squad.forEach { member ->
            BrandCard {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(member.name, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusDot(
                                when (member.status) {
                                    SquadStatus.AT_VENUE -> StampGreen
                                    SquadStatus.EN_ROUTE -> Gold
                                    SquadStatus.PAYMENT_PENDING -> LockedGray
                                }
                            )
                            Spacer(Modifier.width(6.dp))
                            Caption(member.status.label())
                        }
                        member.pendingAmount?.let {
                            Caption("Owes ${it.asUgx()}")
                        }
                    }
                    if (member.status == SquadStatus.PAYMENT_PENDING) {
                        PillButton("Settle", onClick = { vm.settle(member.id) }, gold = true)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(12.dp))
    }
}
