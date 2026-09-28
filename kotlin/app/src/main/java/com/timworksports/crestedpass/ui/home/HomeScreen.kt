package com.timworksports.crestedpass.ui.home

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Sports
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.data.model.initials
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.AvatarMark
import com.timworksports.crestedpass.ui.discover.ReelsFeed
import com.timworksports.crestedpass.ui.theme.Gold

@Suppress("UNUSED_PARAMETER")
@Composable
fun HomeScreen(
    factory: CrestedPassViewModelFactory,
    onPay: () -> Unit,
    onPredict: () -> Unit,
    onTrail: () -> Unit,
    onScan: () -> Unit,
    onEmbassy: () -> Unit,
    onTicket: () -> Unit,
    onBand: () -> Unit,
    onProfile: () -> Unit,
    onCoaches: () -> Unit = {},
    onEvents: () -> Unit = {},
    onAthletes: () -> Unit = {},
    onShop: () -> Unit = {},
    onEventTicket: (String) -> Unit,
    vm: HomeViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    var viewerIndex by remember { mutableStateOf<Int?>(null) }
    var seen by remember { mutableStateOf(setOf<String>()) }

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colors.background)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Timwork Sports", color = colors.onBackground, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text("Coaching, events, athletes, shop", color = colors.onSurfaceVariant, fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "Ticket",
                    color = colors.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onTicket)
                )
                AvatarMark(
                    key = state.user.name,
                    initials = state.user.initials,
                    size = 32.dp,
                    modifier = Modifier.clickable(onClick = onProfile)
                )
            }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                StoryCirclesRow(
                    userName = state.user.name,
                    reels = state.reels,
                    seen = seen,
                    onOpen = { index, id ->
                        seen = seen + id
                        viewerIndex = index
                    }
                )
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAction(Icons.Outlined.Sports, "Coaches", onCoaches)
                    QuickAction(Icons.Outlined.Event, "Events", onEvents)
                    QuickAction(Icons.Outlined.Groups, "Athletes", onAthletes)
                    QuickAction(Icons.Outlined.CalendarMonth, "Book", onCoaches)
                    QuickAction(Icons.Outlined.ShoppingBag, "Shop", onShop)
                }
            }
            items(state.shots, key = { it.id }) { shot ->
                ShotPost(shot = shot, onLike = { vm.toggleShotLike(shot.id) })
            }
        }
    }

    val start = viewerIndex
    if (start != null) {
        Dialog(
            onDismissRequest = { viewerIndex = null },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            val safe = WindowInsets.safeDrawing.asPaddingValues()
            val top = safe.calculateTopPadding().coerceAtLeast(40.dp)
            val bottom = safe.calculateBottomPadding().coerceAtLeast(48.dp) + 56.dp
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                ReelsFeed(
                    reels = state.reels,
                    destinations = state.destinations,
                    onLike = vm::toggleLike,
                    onAddToTrail = vm::addToTrail,
                    modifier = Modifier.fillMaxSize(),
                    initialPage = start,
                    topPadding = top + 8.dp,
                    bottomPadding = bottom + 16.dp
                )
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = top, end = 16.dp)
                        .clickable { viewerIndex = null }
                )
            }
        }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(colors.surface)
                .border(1.dp, colors.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Gold, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = colors.onBackground, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

