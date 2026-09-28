package com.timworksports.crestedpass.ui.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.data.model.asUgx
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.components.QrBitmap
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.components.SerifTitle
import com.timworksports.crestedpass.ui.theme.Cream
import com.timworksports.crestedpass.ui.theme.Muted
import com.timworksports.crestedpass.ui.theme.Navy
import com.timworksports.crestedpass.ui.theme.StampGreen
import com.timworksports.crestedpass.ui.theme.SurfaceWhite

private val topUpAmounts = listOf(10_000, 20_000, 50_000, 100_000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    factory: CrestedPassViewModelFactory,
    vm: WalletViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()
    val snapshot = state.snapshot
    val qrPainter = remember(snapshot.payToken) { QrBitmap.painter(snapshot.payToken) }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ScreenHeader("Wallet", "Cashless fan spend · mock payments only")
        Spacer(Modifier.height(20.dp))
        GoldLabel("Balance")
        SerifTitle(snapshot.user.walletBalance.asUgx(), size = 36)
        Caption("Held for ${snapshot.user.name}")
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Top up", vm::openTopUp, gold = true)
        state.confirmedAmount?.let { amount ->
            Spacer(Modifier.height(10.dp))
            BrandCard {
                Text("Simulated top-up of ${amount.asUgx()} complete.", color = StampGreen)
            }
        }
        Spacer(Modifier.height(20.dp))
        BrandCard(padding = 20.dp) {
            GoldLabel("Tap to pay")
            Spacer(Modifier.height(12.dp))
            Image(
                painter = qrPainter,
                contentDescription = "Tap to pay QR code",
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(12.dp))
            Caption("Show this code at a Crested Pass stall. Token ${snapshot.payToken}.", Modifier.align(Alignment.CenterHorizontally))
        }
        Spacer(Modifier.height(24.dp))
        GoldLabel("Activity")
        Spacer(Modifier.height(10.dp))
        snapshot.transactions.forEach { txn ->
            BrandCard {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(txn.vendor, color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp)
                        Caption(if (txn.amount < 0) "Spend" else "Top-up")
                    }
                    Text(
                        txn.amount.asUgx(),
                        color = if (txn.amount < 0) MaterialTheme.colorScheme.onBackground else StampGreen,
                        fontSize = 15.sp
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(12.dp))
    }

    if (state.showTopUp) {
        ModalBottomSheet(
            onDismissRequest = vm::closeTopUp,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                SerifTitle("Top up wallet", size = 22)
                Caption("No payment SDK in this prototype — amounts are simulated.")
                Spacer(Modifier.height(16.dp))
                topUpAmounts.forEach { amount ->
                    PrimaryButton(amount.asUgx(), onClick = { vm.topUp(amount) })
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
