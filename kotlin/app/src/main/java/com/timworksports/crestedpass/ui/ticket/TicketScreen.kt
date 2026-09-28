package com.timworksports.crestedpass.ui.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.data.model.asUgx
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.components.SportCover
import com.timworksports.crestedpass.ui.theme.AlertRed
import com.timworksports.crestedpass.ui.theme.SurfaceWhite

@Composable
fun TicketScreen(
    factory: CrestedPassViewModelFactory,
    eventId: String? = null,
    vm: TicketViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()
    val event = eventId?.let { id -> state.events.find { it.id == id } }
    val owned = if (eventId != null) {
        state.tickets.find { it.eventId == eventId }
    } else {
        state.tickets.firstOrNull() ?: state.ticket
    }
    val showAlert = owned != null && state.safetyAlert != null && !state.safetyAlertDismissed
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        if (owned != null) {
            ScreenHeader(owned.matchLabel, "Printable Crested Pass · ${owned.venue}")
            Spacer(Modifier.height(16.dp))
            if (showAlert) {
                BrandCard(background = AlertRed) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Column(Modifier.weight(1f)) {
                            GoldLabel("Safety alert")
                            Spacer(Modifier.height(6.dp))
                            Text(state.safetyAlert.orEmpty(), color = SurfaceWhite, fontSize = 14.sp)
                        }
                        TextButton(onClick = vm::dismissAlert) {
                            Text("Dismiss", color = SurfaceWhite)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            PrintableTicketPass(ticket = owned, holderName = state.user.name)
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = "Print ticket",
                gold = true,
                onClick = { printMatchTicket(context, owned, state.user.name) }
            )
        } else if (event != null) {
            SportCover(
                event = event,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(Modifier.height(16.dp))
            ScreenHeader(event.title, "${event.sport} · ${event.whenLabel}")
            Caption("${event.venue} · ${event.city}")
            Spacer(Modifier.height(16.dp))
            BrandCard {
                Row(Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        GoldLabel("Wallet")
                        Text(state.user.walletBalance.asUgx(), color = colors.onBackground, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        GoldLabel("Ticket")
                        Text(event.price.asUgx(), color = colors.onBackground, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            if (state.user.walletBalance >= event.price) {
                PrimaryButton("Buy ticket", { vm.buy(event.id) }, gold = true)
                Spacer(Modifier.height(8.dp))
                Caption("Pays from your Crested Pass wallet. Mock checkout only.")
            } else {
                Caption("Need ${(event.price - state.user.walletBalance).asUgx()} more. Top up to buy.")
                Spacer(Modifier.height(12.dp))
                listOf(10_000, 20_000, 50_000, 100_000).forEach { amount ->
                    PrimaryButton("Top up ${amount.asUgx()}", { vm.topUp(amount) })
                    Spacer(Modifier.height(8.dp))
                }
            }
        } else {
            ScreenHeader("Tickets", "Buy an upcoming event from Home")
        }
        Spacer(Modifier.height(20.dp))
    }
}
