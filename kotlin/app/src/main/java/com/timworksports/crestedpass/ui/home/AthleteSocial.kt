package com.timworksports.crestedpass.ui.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.timworksports.crestedpass.data.model.Player
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.media.imageRes
import com.timworksports.crestedpass.ui.theme.Gold

private data class SideClub(
    val name: String,
    val sport: String,
    val city: String,
    val members: Int,
    val imageName: String
)

private fun clubSides(players: List<Player>): List<SideClub> =
    players.groupBy { it.club }
        .map { (name, members) ->
            val lead = members.maxBy { it.scoring }
            SideClub(name, lead.sport, lead.city, members.size, lead.imageName)
        }
        .sortedByDescending { it.members }
        .take(8)

private fun featuredAthletes(players: List<Player>): List<Player> =
    players.groupBy { it.sport }
        .mapNotNull { (_, group) -> group.maxWithOrNull(compareBy<Player> { it.scoring }.thenBy { it.name }) }
        .sortedBy { it.sport }

@Composable
fun AthleteSocial(
    players: List<Player>,
    onSeeAthletes: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val clubs = remember(players) { clubSides(players) }
    val featured = remember(players) { featuredAthletes(players) }
    var joined by remember { mutableStateOf(setOf<String>()) }
    var followed by remember { mutableStateOf(setOf<String>()) }
    var challenged by remember { mutableStateOf(setOf<String>()) }
    var open by remember { mutableStateOf<Player?>(null) }

    Column(Modifier.padding(top = 8.dp, bottom = 8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Clubs", color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text("${joined.size} joined", color = colors.onSurfaceVariant, fontSize = 12.sp)
        }
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            clubs.forEach { club ->
                ClubCard(
                    club = club,
                    joined = club.name in joined,
                    onJoin = {
                        joined = if (club.name in joined) joined - club.name else joined + club.name
                    }
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Player profiles", color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "All athletes",
                color = Gold,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onSeeAthletes)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            featured.forEach { athlete ->
                PlayerCard(athlete, athlete.id in followed) { open = athlete }
            }
        }
    }

    open?.let { athlete ->
        PlayerSocialProfile(
            player = athlete,
            peers = players.filter { it.sport == athlete.sport },
            following = athlete.id in followed,
            challenged = athlete.id in challenged,
            clubJoined = athlete.club in joined,
            onFollow = {
                followed = if (athlete.id in followed) followed - athlete.id else followed + athlete.id
            },
            onChallenge = { challenged = challenged + athlete.id },
            onJoinClub = { joined = joined + athlete.club },
            onClose = { open = null }
        )
    }
}

@Composable
private fun ClubCard(club: SideClub, joined: Boolean, onJoin: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .width(168.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(imageRes(club.imageName)),
            contentDescription = club.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(Modifier.height(8.dp))
        Text(
            club.name,
            color = colors.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Caption("${club.sport} · ${club.city}")
        Caption("${club.members} on the side")
        Spacer(Modifier.height(8.dp))
        Text(
            if (joined) "Joined" else "Join",
            color = if (joined) colors.onBackground else Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier
                .clip(CircleShape)
                .background(if (joined) colors.background else Gold)
                .clickable(onClick = onJoin)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PlayerCard(player: Player, following: Boolean, onOpen: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .width(132.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
            .clickable(onClick = onOpen)
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(imageRes(player.imageName)),
            contentDescription = player.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(112.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(Modifier.height(8.dp))
        Text(
            player.name,
            color = colors.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        GoldLabel(player.sport)
        Caption(if (following) "Following" else player.club)
    }
}

@Composable
private fun PlayerSocialProfile(
    player: Player,
    peers: List<Player>,
    following: Boolean,
    challenged: Boolean,
    clubJoined: Boolean,
    onFollow: () -> Unit,
    onChallenge: () -> Unit,
    onJoinClub: () -> Unit,
    onClose: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val ranked = peers.sortedWith(compareByDescending<Player> { it.scoring }.thenBy { it.name })
    val rank = ranked.indexOfFirst { it.id == player.id }.let { if (it < 0) peers.size else it + 1 }
    val form = List(5) { index -> listOf("W", "D", "L")[(player.scoring + index) % 3] }.joinToString("  ")
    BackHandler(onBack = onClose)
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(colors.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Gold)
                }
                Spacer(Modifier.width(8.dp))
                Text("Player profile", color = colors.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(14.dp))
            Image(
                painter = painterResource(imageRes(player.imageName)),
                contentDescription = player.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            Spacer(Modifier.height(14.dp))
            Text(player.name, color = colors.onBackground, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
            GoldLabel("${player.position} · ${player.club}")
            Spacer(Modifier.height(6.dp))
            Caption("${player.city} · rank $rank of ${ranked.size} in ${player.sport}")
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SocialPill(if (following) "Following" else "Follow", following, onFollow)
                SocialPill(if (challenged) "Challenged" else "Challenge", challenged, onChallenge, enabled = !challenged)
            }
            Spacer(Modifier.height(16.dp))
            Text("On the side", color = colors.onBackground, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Caption(player.bio)
            Spacer(Modifier.height(8.dp))
            SocialLine("Club", player.club)
            SocialLine("Last five", form)
            SocialLine(player.scoringLabel.replaceFirstChar { it.uppercase() }, "${player.scoring}")
            SocialLine("Training", "${player.sessions().count { it.kind == "Training" }} sessions")
            SocialLine("Distance", "%.1f km".format(player.sessions().sumOf { it.distanceKm }))
            SocialLine("Appearances", "${player.appearances}")
            SocialLine("Number", "#${player.number}")
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = if (clubJoined) "In ${player.club}" else "Join ${player.club}",
                onClick = onJoinClub,
                gold = true,
                enabled = !clubJoined
            )
        }
    }
}

@Composable
private fun SocialPill(label: String, filled: Boolean, onClick: () -> Unit, enabled: Boolean = true) {
    val colors = MaterialTheme.colorScheme
    Text(
        label,
        color = if (filled) colors.onBackground else Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (filled) colors.surface else Gold)
            .border(1.dp, if (filled) colors.outline else Gold, CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SocialLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Caption(label)
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
