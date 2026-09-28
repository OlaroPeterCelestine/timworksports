package com.timworksports.crestedpass.ui.trail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
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
import com.timworksports.crestedpass.data.model.StampState
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.components.SerifTitle
import com.timworksports.crestedpass.ui.components.TrailBar
import com.timworksports.crestedpass.ui.theme.Border
import com.timworksports.crestedpass.ui.theme.Cream
import com.timworksports.crestedpass.ui.theme.Gold
import com.timworksports.crestedpass.ui.theme.LockedGray
import com.timworksports.crestedpass.ui.theme.Muted
import com.timworksports.crestedpass.ui.theme.Navy
import com.timworksports.crestedpass.ui.theme.StampGreen
import com.timworksports.crestedpass.ui.theme.SurfaceWhite

@Composable
fun TrailScreen(
    factory: CrestedPassViewModelFactory,
    vm: TrailViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()
    val nextReward = when {
        state.trail.collected >= 6 -> "Trail complete · signed Cranes scarf unlocked"
        state.trail.collected >= 5 -> "Hospitality voucher unlocked · scarf at 6 stamps"
        else -> "Next reward at 5 stamps · match-day hospitality voucher"
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ScreenHeader("Trail", "Collect stamps at UTB partners")
        Spacer(Modifier.height(16.dp))
        BrandCard { TrailBar(state.trail.collected, state.trail.total) }
        Spacer(Modifier.height(12.dp))
        BrandCard {
            GoldLabel("Reward unlock")
            Spacer(Modifier.height(8.dp))
            SerifTitle(nextReward, size = 20)
            Caption("Licensed with Uganda Tourism Board", color = Gold)
        }
        Spacer(Modifier.height(20.dp))
        GoldLabel("Stamp path")
        Spacer(Modifier.height(12.dp))
        state.stamps.forEachIndexed { index, stamp ->
            Row(Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StampNode(stamp.state)
                    if (index != state.stamps.lastIndex) {
                        Box(
                            Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(
                                    when (stamp.state) {
                                        StampState.COMPLETED -> StampGreen
                                        StampState.NEXT_AVAILABLE -> Gold
                                        StampState.LOCKED -> LockedGray
                                    }
                                )
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                BrandCard(modifier = Modifier.padding(bottom = 8.dp)) {
                    GoldLabel(
                        when (stamp.state) {
                            StampState.COMPLETED -> "Collected"
                            StampState.NEXT_AVAILABLE -> "Next available"
                            StampState.LOCKED -> "Locked"
                        }
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(stamp.name, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                    Caption(stamp.location)
                    if (stamp.state == StampState.NEXT_AVAILABLE) {
                        Spacer(Modifier.height(10.dp))
                        PrimaryButton("Collect stamp (demo)", vm::collectNext, gold = true)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        GoldLabel("Partner directory")
        Spacer(Modifier.height(10.dp))
        state.partners.forEach { place ->
            BrandCard {
                Text(place.name, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                Caption("${place.area} · ${place.detail}")
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun StampNode(state: StampState) {
    val border = when (state) {
        StampState.COMPLETED -> StampGreen
        StampState.NEXT_AVAILABLE -> Gold
        StampState.LOCKED -> LockedGray
    }
    val colors = MaterialTheme.colorScheme
    val fill = when (state) {
        StampState.COMPLETED -> StampGreen
        StampState.NEXT_AVAILABLE -> colors.surface
        StampState.LOCKED -> colors.surfaceVariant
    }
    Box(
        Modifier
            .size(28.dp)
            .background(fill, CircleShape)
            .border(2.dp, border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            StampState.COMPLETED -> Icon(
                Icons.Filled.Check,
                contentDescription = "Collected",
                tint = SurfaceWhite,
                modifier = Modifier.size(16.dp)
            )
            StampState.NEXT_AVAILABLE -> Box(
                Modifier
                    .size(8.dp)
                    .background(Gold, CircleShape)
            )
            StampState.LOCKED -> Icon(
                Icons.Filled.Lock,
                contentDescription = "Locked",
                tint = Muted,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
