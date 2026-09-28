package com.timworksports.crestedpass.ui.band

import android.app.Activity
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.components.SerifTitle
import com.timworksports.crestedpass.ui.theme.Gold
import com.timworksports.crestedpass.ui.theme.Navy
import com.timworksports.crestedpass.ui.theme.SurfaceWhite

@Composable
fun BandScreen(
    factory: CrestedPassViewModelFactory,
    vm: BandViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()
    val lastTap by vm.lastTap.collectAsState()
    val context = LocalContext.current
    var listening by remember { mutableStateOf(false) }

    DisposableEffect(listening) {
        val activity = context as? Activity
        val adapter = activity?.let { NfcAdapter.getDefaultAdapter(it) }
        if (listening && activity != null && adapter != null) {
            adapter.enableReaderMode(
                activity,
                { _ -> activity.runOnUiThread { vm.simulateTap() } },
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                Bundle()
            )
        }
        onDispose {
            if (activity != null && adapter != null) {
                runCatching { adapter.disableReaderMode(activity) }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ScreenHeader("Crested Band", "Tap to enter, pay, and stamp")
        Spacer(Modifier.height(16.dp))
        BrandCard(padding = 20.dp) {
            GoldLabel(if (state.crestedBand.paired) "Paired" else "Unpaired")
            Spacer(Modifier.height(10.dp))
            SerifTitle(state.crestedBand.bandId, size = 28)
            Spacer(Modifier.height(8.dp))
            Caption("Status · ${state.crestedBand.status.replaceFirstChar { it.uppercase() }}", color = Gold)
            Spacer(Modifier.height(10.dp))
            Caption(
                "The band holds an ID, not cash. Lost bands are frozen and reissued against the same wallet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(12.dp))
        BrandCard {
            GoldLabel("Loyalty")
            Spacer(Modifier.height(8.dp))
            SerifTitle(state.loyalty.tier, size = 26)
            Caption(
                if (state.loyalty.stampsToNextTier == 0) "Top tier unlocked"
                else "${state.loyalty.stampsToNextTier} stamps to the next tier"
            )
            Spacer(Modifier.height(6.dp))
            Caption("Priority lane · embassy fast-track · bonus predictor points")
        }
        if (lastTap != null) {
            Spacer(Modifier.height(12.dp))
            BrandCard {
                GoldLabel("Last tap")
                Spacer(Modifier.height(6.dp))
                Text(lastTap!!, color = Navy, fontSize = 15.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Simulate tap", vm::simulateTap, gold = true)
        Spacer(Modifier.height(8.dp))
        Caption("Emulator-safe. On a device this same path reads the NFC UID.")
        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            if (listening) "Stop NFC listener" else "Listen for NFC (device)",
            onClick = { listening = !listening }
        )
        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            if (state.crestedBand.status == "frozen") "Unfreeze band" else "Freeze lost band",
            vm::freeze
        )
        Spacer(Modifier.height(12.dp))
        PrimaryButton("Reissue band", vm::reissue)
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {}
    }
}
