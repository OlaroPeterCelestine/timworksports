package com.timworksports.crestedpass.ui.predict

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
import com.timworksports.crestedpass.ui.theme.Border
import com.timworksports.crestedpass.ui.theme.Cream
import com.timworksports.crestedpass.ui.theme.Gold
import com.timworksports.crestedpass.ui.theme.Navy
import com.timworksports.crestedpass.ui.theme.Serif
import com.timworksports.crestedpass.ui.theme.SurfaceWhite

@Composable
fun PredictScreen(
    factory: CrestedPassViewModelFactory,
    onBack: (() -> Unit)? = null,
    vm: PredictViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()
    val match = state.snapshot.nextMatch
    val submitted = state.snapshot.prediction

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ScreenHeader("Predict", "Lock a score before kickoff", onBack = onBack)
        Spacer(Modifier.height(16.dp))
        BrandCard {
            GoldLabel("Current fixture")
            Spacer(Modifier.height(8.dp))
            SerifTitle("${match.home} vs ${match.away}", size = 24)
            Caption("${match.venue} · ${match.kickoffInHours}h to kickoff", color = Gold)
        }
        Spacer(Modifier.height(16.dp))
        BrandCard {
            GoldLabel("Your score")
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreStepper(match.home, state.homeScore, vm::bumpHome)
                Text("–", color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontFamily = Serif)
                ScoreStepper(match.away, state.awayScore, vm::bumpAway)
            }
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                if (submitted.homeScore != null) "Update prediction" else "Lock in prediction",
                onClick = vm::submit,
                gold = true
            )
            if (submitted.homeScore != null && submitted.awayScore != null) {
                Spacer(Modifier.height(10.dp))
                Caption("Locked in: ${match.home} ${submitted.homeScore}–${submitted.awayScore} ${match.away}")
            }
        }
        Spacer(Modifier.height(16.dp))
        BrandCard {
            GoldLabel("Points key")
            Spacer(Modifier.height(8.dp))
            Caption("Exact score · 10 pts")
            Caption("Correct winner + goal difference · 5 pts")
            Caption("Correct winner · 3 pts")
            Caption("Any prediction submitted · 1 pt")
        }
        Spacer(Modifier.height(20.dp))
        GoldLabel("Leaderboard")
        Spacer(Modifier.height(10.dp))
        state.snapshot.leaderboard.forEachIndexed { index, entry ->
            val isYou = entry.name == state.snapshot.user.name
            BrandCard(background = if (isYou) MaterialTheme.colorScheme.surfaceVariant else Color.Unspecified) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${index + 1}",
                            color = if (isYou) MaterialTheme.colorScheme.onBackground else Gold,
                            fontFamily = Serif,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                if (isYou) "${entry.name}  ·  you" else entry.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (isYou) FontWeight.SemiBold else FontWeight.Normal
                            )
                            if (isYou) Caption("Your row")
                        }
                    }
                    SerifTitle("${entry.points}", size = 22)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ScoreStepper(label: String, value: Int, onBump: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Caption(label)
        Spacer(Modifier.height(8.dp))
        StepperButton("+") { onBump(1) }
        Box(
            Modifier
                .padding(vertical = 8.dp)
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Border, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text("$value", fontSize = 28.sp, fontFamily = Serif, color = MaterialTheme.colorScheme.onBackground)
        }
        StepperButton("–") { onBump(-1) }
    }
}

@Composable
private fun StepperButton(label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(1.dp, Border, CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}
