package com.timworksports.crestedpass.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Watch
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.data.model.CrestedBand
import com.timworksports.crestedpass.data.model.Player
import com.timworksports.crestedpass.data.model.SquadMember
import com.timworksports.crestedpass.data.model.Ticket
import com.timworksports.crestedpass.data.model.User
import com.timworksports.crestedpass.data.model.label
import com.timworksports.crestedpass.data.model.asUgx
import com.timworksports.crestedpass.data.model.handle
import com.timworksports.crestedpass.data.model.initials
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.components.AvatarMark
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.QuickAction
import com.timworksports.crestedpass.ui.components.SerifTitle
import com.timworksports.crestedpass.ui.components.TrailBar
import com.timworksports.crestedpass.ui.media.imageRes
import com.timworksports.crestedpass.ui.theme.AlertRed
import com.timworksports.crestedpass.ui.theme.Gold
import java.util.Locale

@Composable
fun ProfileScreen(
    factory: CrestedPassViewModelFactory,
    onWallet: () -> Unit,
    onTrail: () -> Unit,
    onPredict: () -> Unit,
    onTicket: () -> Unit,
    onBand: () -> Unit,
    onEmbassy: () -> Unit,
    onSettings: () -> Unit,
    onSignOut: () -> Unit,
    vm: ProfileViewModel = viewModel(factory = factory)
) {
    val snapshot by vm.uiState.collectAsState()
    val colors = MaterialTheme.colorScheme
    val user = snapshot.user
    val shots = vm.myPostImages(snapshot)
    val nextTier = if (snapshot.loyalty.tier == "Silver") "Gold" else "next tier"

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SerifTitle("You", size = 24)
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.outline, CircleShape)
                    .clickable(onClick = onSettings),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = colors.onBackground, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarMark(key = user.name, initials = user.initials, size = 92.dp, ring = true)
            Spacer(Modifier.size(16.dp))
            Column {
                Text(user.name, color = colors.onBackground, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text(user.handle, color = colors.onSurfaceVariant, fontSize = 14.sp)
                Text("Kampala · dream side", color = colors.onSurfaceVariant, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Text("12-day streak", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "${snapshot.loyalty.tier} member",
                    color = Gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .border(1.dp, Gold.copy(alpha = 0.45f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        BrandCard(padding = 0.dp) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatCell("12", "Streak", Modifier.weight(1f))
                Box(Modifier.size(width = 1.dp, height = 36.dp).background(colors.outline))
                StatCell("82", "Recovery", Modifier.weight(1f))
                Box(Modifier.size(width = 1.dp, height = 36.dp).background(colors.outline))
                StatCell("${vm.points(snapshot)}", "Pitch score", Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(16.dp))
        SportsDream(players = snapshot.players, squad = snapshot.squad)
        Spacer(Modifier.height(16.dp))
        PassCard(user = user, ticket = snapshot.ticket, band = snapshot.crestedBand, onWallet = onWallet)
        Spacer(Modifier.height(16.dp))
        GoldLabel("Shortcuts")
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction("Ticket", Icons.Outlined.ConfirmationNumber, onTicket, Modifier.weight(1f))
            QuickAction("Band", Icons.Outlined.Watch, onBand, Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction("Predict", Icons.Outlined.SportsSoccer, onPredict, Modifier.weight(1f))
            QuickAction("Embassy", Icons.Outlined.Apartment, onEmbassy, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        BrandCard(onClick = onTrail) {
            TrailBar(collected = snapshot.trail.collected, total = snapshot.trail.total)
            Spacer(Modifier.height(8.dp))
            Caption(
                if (snapshot.loyalty.stampsToNextTier == 0) "Gold tier unlocked"
                else "${snapshot.loyalty.stampsToNextTier} stamps to $nextTier"
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Open trail", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("›", color = colors.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(16.dp))
        GoldLabel("Snaps")
        Spacer(Modifier.height(10.dp))
        if (shots.isEmpty()) {
            Caption("Shots you publish on Home will land here.")
        } else {
            shots.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEach { item ->
                        Image(
                            painter = painterResource(imageRes(item.second)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }
                    repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        BrandCard(onClick = onSignOut) {
            Text("Sign out", color = AlertRed, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun StatCell(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(label, color = colors.onSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable
private fun PassCard(
    user: User,
    ticket: Ticket,
    band: CrestedBand,
    onWallet: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    BrandCard(padding = 20.dp) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            GoldLabel("Fan pass")
            Text(band.bandId, color = colors.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        SerifTitle(user.walletBalance.asUgx(), size = 28)
        Caption("Wallet · ${band.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }} band", color = Gold)
        Spacer(Modifier.height(14.dp))
        Text(ticket.matchLabel, color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Caption("${ticket.block} · ${ticket.seat}")
        Spacer(Modifier.height(8.dp))
        Text("Crested Band", color = colors.onBackground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Caption(if (band.paired) "Paired for gates & stalls" else "Not paired")
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Gold)
                .clickable(onClick = onWallet)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Open wallet", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

private data class Slice(val label: String, val value: Float, val color: Color)

private data class WatchRead(val label: String, val value: String, val detail: String, val icon: ImageVector)

private val recoverySlices = listOf(
    Slice("Sleep", 38f, Color(0xFF6AA6FF)),
    Slice("Strain", 24f, AlertRed),
    Slice("Ready", 38f, Gold)
)

private val sportSlices = listOf(
    Slice("Football", 42f, Gold),
    Slice("Court", 23f, Color(0xFFE0A100)),
    Slice("Track", 20f, Color(0xFFC45C6A)),
    Slice("Rest", 15f, Color(0xFF8A8A8A))
)

@Composable
private fun SportsDream(players: List<Player>, squad: List<SquadMember>) {
    val pioneers = players
        .groupBy { it.sport }
        .mapNotNull { (_, group) -> group.maxWithOrNull(compareBy<Player> { it.scoring }.thenByDescending { it.name }) }
        .sortedBy { it.sport }
    WatchCard()
    Spacer(Modifier.height(12.dp))
    ChartCard(
        title = "Recovery",
        score = "82",
        caption = "Ready to train. Sleep carried the night and strain is still low.",
        slices = recoverySlices
    )
    Spacer(Modifier.height(12.dp))
    ChartCard(
        title = "This week",
        score = "5",
        caption = "Five sessions. Football still leads the week.",
        slices = sportSlices
    )
    Spacer(Modifier.height(12.dp))
    RingsCard()
    Spacer(Modifier.height(16.dp))
    GoldLabel("Pioneers")
    Spacer(Modifier.height(4.dp))
    Caption("Best in each sport this season")
    Spacer(Modifier.height(10.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        pioneers.forEach { athlete ->
            PioneerCard(athlete)
        }
    }
    Spacer(Modifier.height(16.dp))
    GoldLabel("Dream side")
    Spacer(Modifier.height(10.dp))
    BrandCard {
        Text("Chemistry 86", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
        Caption("The side trains together. Watch sessions count toward the streak.")
        Spacer(Modifier.height(10.dp))
        squad.forEach { member ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(member.name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)
                Caption(member.status.label())
            }
        }
    }
}

@Composable
private fun WatchCard() {
    val reads = listOf(
        WatchRead("Heart", "68", "Resting 68 · peak 154 bpm in the last session", Icons.Outlined.FavoriteBorder),
        WatchRead("Steps", "8,640", "8,640 steps · 6.4 km since midnight", Icons.AutoMirrored.Outlined.DirectionsWalk),
        WatchRead("Sleep", "7h 12m", "7h 12m asleep · 1h 40m deep", Icons.Outlined.Bedtime),
        WatchRead("Load", "612", "612 active kcal · HRV 64 ms · SpO2 98%", Icons.Outlined.LocalFireDepartment)
    )
    var selected by remember { mutableStateOf(reads.first().label) }
    val current = reads.first { it.label == selected }
    BrandCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            GoldLabel("Watch")
            Caption("Synced 2m ago")
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            reads.forEach { read ->
                WatchChip(read, selected == read.label, Modifier.weight(1f)) { selected = read.label }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.MonitorHeart, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Caption(current.detail)
        }
    }
}

@Composable
private fun WatchChip(read: WatchRead, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) colors.surfaceVariant else colors.background)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(read.icon, contentDescription = read.label, tint = if (selected) Gold else colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(Modifier.height(4.dp))
        Text(
            read.value,
            color = colors.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(read.label, color = colors.onSurfaceVariant, fontSize = 10.sp)
    }
}

@Composable
private fun ChartCard(title: String, score: String, caption: String, slices: List<Slice>) {
    BrandCard {
        GoldLabel(title)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(116.dp)) {
                Donut(slices, Modifier.fillMaxSize())
                Text(score, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                slices.forEach { slice ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(slice.color))
                        Spacer(Modifier.width(8.dp))
                        Text(slice.label, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text("${slice.value.toInt()}%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Caption(caption)
    }
}

@Composable
private fun Donut(slices: List<Slice>, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = Stroke(width = size.minDimension * 0.16f, cap = StrokeCap.Butt)
        val arcSize = Size(size.width * 0.84f, size.height * 0.84f)
        val origin = Offset(size.width * 0.08f, size.height * 0.08f)
        var start = -90f
        val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
        slices.forEach { slice ->
            val sweep = slice.value / total * 360f
            drawArc(
                color = slice.color,
                startAngle = start,
                sweepAngle = (sweep - 3f).coerceAtLeast(1f),
                useCenter = false,
                topLeft = origin,
                size = arcSize,
                style = stroke
            )
            start += sweep
        }
    }
}

@Composable
private fun RingsCard() {
    BrandCard {
        GoldLabel("Today")
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            RingStat("Move", 0.74f, Gold, "74%")
            RingStat("Sessions", 0.6f, Color(0xFF6AA6FF), "3/5")
            RingStat("Recovery", 0.82f, AlertRed, "82")
        }
    }
}

@Composable
private fun RingStat(label: String, progress: Float, color: Color, value: String) {
    val track = MaterialTheme.colorScheme.outline
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = Stroke(width = size.minDimension * 0.12f, cap = StrokeCap.Round)
                val arcSize = Size(size.width * 0.78f, size.height * 0.78f)
                val origin = Offset(size.width * 0.11f, size.height * 0.11f)
                drawArc(track, 0f, 360f, false, origin, arcSize, style = stroke)
                drawArc(color, -90f, 360f * progress, false, origin, arcSize, style = stroke)
            }
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable
private fun PioneerCard(athlete: Player) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .width(148.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(imageRes(athlete.imageName)),
            contentDescription = athlete.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(Modifier.height(8.dp))
        Text(athlete.name, color = colors.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
        GoldLabel(athlete.sport)
        Caption("${athlete.scoring} ${athlete.scoringLabel}")
    }
}
