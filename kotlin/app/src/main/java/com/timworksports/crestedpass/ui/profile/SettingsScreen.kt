package com.timworksports.crestedpass.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.data.model.asUgx
import com.timworksports.crestedpass.data.model.handle
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.components.SettingsRow
import com.timworksports.crestedpass.ui.components.SettingsToggleRow
import com.timworksports.crestedpass.ui.theme.Gold
import com.timworksports.crestedpass.ui.theme.ThemeMode
import com.timworksports.crestedpass.ui.theme.label
import java.util.Locale

@Composable
fun SettingsScreen(
    factory: CrestedPassViewModelFactory,
    themeMode: ThemeMode,
    onSetTheme: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    onWallet: () -> Unit,
    onBand: () -> Unit,
    onEmbassy: () -> Unit,
    onSignOut: () -> Unit,
    vm: ProfileViewModel = viewModel(factory = factory)
) {
    val snapshot by vm.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    var notifyMatch by rememberSaveable { mutableStateOf(true) }
    var notifyTrail by rememberSaveable { mutableStateOf(true) }
    var notifyWallet by rememberSaveable { mutableStateOf(true) }
    val bandStatus = snapshot.crestedBand.status.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ScreenHeader("Settings", "Account, payments, and look", onBack = onBack)
        Spacer(Modifier.height(20.dp))

        GoldLabel("Account")
        Spacer(Modifier.height(10.dp))
        BrandCard {
            SettingsRow(
                icon = Icons.Outlined.Person,
                title = snapshot.user.name,
                value = snapshot.user.handle,
                showChevron = false
            )
            HorizontalDivider(color = colors.outline)
            SettingsRow(
                icon = Icons.Outlined.Language,
                title = "Language",
                value = if (snapshot.language == "EN") "English" else "Luganda",
                showChevron = false,
                onClick = vm::toggleLanguage
            )
        }

        Spacer(Modifier.height(18.dp))
        GoldLabel("Payments & security")
        Spacer(Modifier.height(10.dp))
        BrandCard {
            SettingsRow(
                icon = Icons.Outlined.AccountBalanceWallet,
                title = "Wallet",
                value = snapshot.user.walletBalance.asUgx(),
                onClick = onWallet
            )
            HorizontalDivider(color = colors.outline)
            SettingsRow(
                icon = Icons.Outlined.Watch,
                title = "Crested Band",
                value = bandStatus,
                onClick = onBand
            )
            HorizontalDivider(color = colors.outline)
            SettingsRow(
                icon = Icons.Outlined.AcUnit,
                title = if (snapshot.crestedBand.status == "frozen") "Unfreeze band" else "Freeze lost band",
                showChevron = false,
                onClick = vm::freezeBand
            )
        }

        Spacer(Modifier.height(18.dp))
        GoldLabel("Appearance")
        Spacer(Modifier.height(10.dp))
        BrandCard {
            Caption("Follows system unless you lock a look.")
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(ThemeMode.System, ThemeMode.Light, ThemeMode.Dark).forEach { mode ->
                    val selected = themeMode == mode
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) Gold else colors.background)
                            .then(
                                if (selected) Modifier
                                else Modifier.border(1.dp, colors.outline, RoundedCornerShape(8.dp))
                            )
                            .clickable { onSetTheme(mode) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            mode.label,
                            color = if (selected) Color.Black else colors.onBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        GoldLabel("Notifications")
        Spacer(Modifier.height(10.dp))
        BrandCard {
            SettingsToggleRow(Icons.Outlined.SportsSoccer, "Match alerts", notifyMatch) { notifyMatch = it }
            HorizontalDivider(color = colors.outline)
            SettingsToggleRow(Icons.Outlined.Terrain, "Trail stamps", notifyTrail) { notifyTrail = it }
            HorizontalDivider(color = colors.outline)
            SettingsToggleRow(Icons.Outlined.AccountBalanceWallet, "Wallet activity", notifyWallet) { notifyWallet = it }
        }

        Spacer(Modifier.height(18.dp))
        GoldLabel("Safety & support")
        Spacer(Modifier.height(10.dp))
        BrandCard {
            val alert = snapshot.safetyAlert
            if (alert != null && !snapshot.safetyAlertDismissed) {
                Caption(alert)
                Spacer(Modifier.height(8.dp))
            }
            SettingsRow(icon = Icons.Outlined.Apartment, title = "Fan Embassy", onClick = onEmbassy)
        }

        Spacer(Modifier.height(18.dp))
        GoldLabel("About")
        Spacer(Modifier.height(10.dp))
        BrandCard {
            SettingsRow(
                icon = Icons.Outlined.Info,
                title = "Timwork Sports",
                value = "Coaching and shop",
                showChevron = false
            )
            Spacer(Modifier.height(8.dp))
            Caption("Timwork Sports is for coaching, events, athlete profiles, and the shop. AFCON and UTB programmes come later.")
        }

        Spacer(Modifier.height(18.dp))
        BrandCard(onClick = onSignOut) {
            SettingsRow(
                icon = Icons.AutoMirrored.Outlined.Logout,
                title = "Sign out",
                destructive = true,
                showChevron = false
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}
