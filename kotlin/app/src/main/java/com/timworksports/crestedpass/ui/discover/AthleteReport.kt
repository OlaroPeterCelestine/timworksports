package com.timworksports.crestedpass.ui.discover

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.timworksports.crestedpass.data.model.FestivalEvent
import com.timworksports.crestedpass.data.model.Player
import com.timworksports.crestedpass.data.model.TrainingSession
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.FilterChip
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.media.imageRes
import com.timworksports.crestedpass.ui.theme.AlertRed
import com.timworksports.crestedpass.ui.theme.Gold

private val eventSports = listOf("Football", "Basketball", "Tennis", "Boxing", "Swimming", "Athletics")

@Composable
fun AthleteReport(player: Player, peers: List<Player>, onBack: () -> Unit) {
    var session by remember { mutableStateOf<TrainingSession?>(null) }
    val open = session
    if (open != null) {
        SessionReport(open, onBack = { session = null })
        return
    }
    val colors = MaterialTheme.colorScheme
    val ranked = peers.sortedWith(compareByDescending<Player> { it.scoring }.thenBy { it.name })
    val rank = ranked.indexOfFirst { it.id == player.id }.let { if (it < 0) peers.size else it + 1 }
    val sessions = player.sessions()
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ScreenHeader(player.name, "${player.sport} · rank $rank of ${ranked.size}", onBack = onBack)
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(imageRes(player.imageName)),
            contentDescription = player.sport,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(14.dp))
        )
        Spacer(Modifier.height(16.dp))
        GoldLabel("${player.position} · ${player.club}")
        Spacer(Modifier.height(12.dp))
        ReportTotals(sessions)
        Spacer(Modifier.height(16.dp))
        GoldLabel("Ranking")
        ReportLine("Place", "$rank of ${ranked.size}")
        ReportLine(player.scoringLabel.replaceFirstChar { it.uppercase() }, "${player.scoring}")
        ReportLine("Appearances", "${player.appearances}")
        ReportLine("City", player.city)
        Spacer(Modifier.height(16.dp))
        TrainingFeed(sessions, onOpen = { session = it })
        Spacer(Modifier.height(12.dp))
        Caption(player.bio)
    }
}

@Composable
fun YourTraining(onOpen: (TrainingSession) -> Unit) {
    val sessions = com.timworksports.crestedpass.data.model.personalTraining()
    Column {
        GoldLabel("Your training")
        Spacer(Modifier.height(8.dp))
        ReportTotals(sessions)
        Spacer(Modifier.height(12.dp))
        TrainingFeed(sessions, onOpen)
    }
}

@Composable
fun SessionReport(session: TrainingSession, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val colors = MaterialTheme.colorScheme
    val zones = listOf(
        "Easy" to 0.46f,
        "Steady" to 0.34f,
        "Hard" to 0.20f
    )
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ScreenHeader(session.title, "${session.kind} · ${session.date}", onBack = onBack)
        Spacer(Modifier.height(16.dp))
        ReportTotals(listOf(session))
        Spacer(Modifier.height(16.dp))
        GoldLabel("Report")
        ReportLine("Distance", session.distanceLabel)
        ReportLine("Time", "${session.minutes} min")
        ReportLine("Pace", session.pace)
        ReportLine("Avg heart rate", "${session.avgHr} bpm")
        ReportLine("Max heart rate", "${session.maxHr} bpm")
        ReportLine("Elevation", "${session.elevationM} m")
        ReportLine("Effort", "${session.effort} / 10")
        Spacer(Modifier.height(16.dp))
        GoldLabel("Heart rate")
        Spacer(Modifier.height(8.dp))
        zones.forEachIndexed { index, (label, share) ->
            val tint = when (index) {
                0 -> colors.onSurfaceVariant
                1 -> Gold
                else -> AlertRed
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = colors.onBackground, fontSize = 13.sp, modifier = Modifier.width(64.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.surfaceVariant)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(share)
                            .height(8.dp)
                            .background(tint)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Caption("${(share * 100).toInt()}%")
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(8.dp))
        GoldLabel("Splits")
        Spacer(Modifier.height(8.dp))
        session.splits.forEachIndexed { index, split ->
            ReportLine("Km ${index + 1}", split)
        }
        Spacer(Modifier.height(12.dp))
        Caption(session.notes)
    }
}

@Composable
fun CreateEventForm(
    onSave: (title: String, sport: String, city: String, venue: String, date: String, time: String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var title by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf(eventSports.first()) }
    var city by remember { mutableStateOf("Kampala") }
    var venue by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Sat 4 Oct") }
    var time by remember { mutableStateOf("16:00") }
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ScreenHeader("Create event", "Host a session for athletes", onBack = onBack)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(Modifier.height(12.dp))
        GoldLabel("Sport")
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            eventSports.forEach { name ->
                FilterChip(name, selected = name == sport, onClick = { sport = name })
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(city, { city = it }, label = { Text("City") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(venue, { venue = it }, label = { Text("Venue") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(date, { date = it }, label = { Text("Date") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(time, { time = it }, label = { Text("Time") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        Spacer(Modifier.height(18.dp))
        PrimaryButton(
            text = "Create event",
            onClick = { onSave(title.trim(), sport, city.trim(), venue.trim(), date.trim(), time.trim()) },
            gold = true,
            enabled = title.isNotBlank() && city.isNotBlank() && venue.isNotBlank()
        )
    }
}

@Composable
fun HostedEventDetail(event: FestivalEvent, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ScreenHeader(event.title, event.sport, onBack = onBack)
        Spacer(Modifier.height(16.dp))
        GoldLabel("Your event")
        ReportLine("When", event.whenLabel)
        ReportLine("City", event.city)
        ReportLine("Venue", event.venue)
        Spacer(Modifier.height(12.dp))
        Caption("Athletes can find this on the Events tab. Entry is open in this prototype.")
    }
}

@Composable
private fun ReportTotals(sessions: List<TrainingSession>) {
    val colors = MaterialTheme.colorScheme
    val km = sessions.sumOf { it.distanceKm }
    val minutes = sessions.sumOf { it.minutes }
    val training = sessions.count { it.kind == "Training" }
    val competitions = sessions.count { it.kind == "Competition" }
    BrandCard(padding = 0.dp) {
        Row(Modifier.fillMaxWidth().padding(vertical = 14.dp)) {
            TotalCell("%.1f".format(km), "km", Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(36.dp).background(colors.outline).align(Alignment.CenterVertically))
            TotalCell("${minutes / 60}h ${minutes % 60}m", "time", Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(36.dp).background(colors.outline).align(Alignment.CenterVertically))
            TotalCell("$training", "sessions", Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(36.dp).background(colors.outline).align(Alignment.CenterVertically))
            TotalCell("$competitions", "comps", Modifier.weight(1f))
        }
    }
}

@Composable
private fun TotalCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun TrainingFeed(sessions: List<TrainingSession>, onOpen: (TrainingSession) -> Unit) {
    GoldLabel("Activities")
    Spacer(Modifier.height(8.dp))
    sessions.forEach { item ->
        BrandCard(onClick = { onOpen(item) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(item.title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Caption("${item.kind} · ${item.date}")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${item.distanceLabel} · ${item.pace} · ${item.avgHr} bpm",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 13.sp
                    )
                }
                Text(
                    item.kind.take(1),
                    color = if (item.kind == "Competition") Gold else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun ReportLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Caption(label)
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
    }
}
