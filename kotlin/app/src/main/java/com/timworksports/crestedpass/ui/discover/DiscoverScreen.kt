package com.timworksports.crestedpass.ui.discover

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timworksports.crestedpass.data.model.Coach
import com.timworksports.crestedpass.data.model.Destination
import com.timworksports.crestedpass.data.model.HighlightClip
import com.timworksports.crestedpass.data.model.Player
import com.timworksports.crestedpass.data.model.PromoAd
import com.timworksports.crestedpass.data.model.ReelClip
import com.timworksports.crestedpass.data.model.SportProfile
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.data.model.FestivalEvent
import com.timworksports.crestedpass.data.model.asUgx
import com.timworksports.crestedpass.ui.components.AvatarMark
import com.timworksports.crestedpass.ui.components.BrandCard
import com.timworksports.crestedpass.ui.components.Caption
import com.timworksports.crestedpass.ui.components.FilterChip
import com.timworksports.crestedpass.ui.components.GoldLabel
import com.timworksports.crestedpass.ui.components.PrimaryButton
import com.timworksports.crestedpass.ui.components.ScreenHeader
import com.timworksports.crestedpass.ui.components.SerifTitle
import com.timworksports.crestedpass.ui.components.SportCover
import com.timworksports.crestedpass.ui.media.imageRes
import androidx.compose.material3.MaterialTheme
import com.timworksports.crestedpass.ui.theme.AlertRed
import com.timworksports.crestedpass.ui.theme.Gold
import kotlin.math.abs
import kotlin.math.roundToInt

private const val PANE_EVENTS = 0
private const val PANE_COACHES = 1
private const val PANE_RANKINGS = 2
private const val PANE_ADS = 3
private const val PANE_SPORTS = 4
private const val PANE_HIGHLIGHTS = 5
private const val PANE_REELS = 6
private const val PANE_DESTINATIONS = 7
private val panes = listOf("Events", "Coaches", "Athletes", "Ads", "Sports", "Highlights", "Reels", "Destinations")

@Composable
fun DiscoverScreen(
    factory: CrestedPassViewModelFactory,
    onEventTicket: (String) -> Unit = {},
    initialPane: Int = PANE_COACHES,
    heading: String = "Coaches",
    subtitle: String = "Programmes, events, and athletes",
    vm: DiscoverViewModel = viewModel(factory = factory)
) {
    val state by vm.uiState.collectAsState()
    var pane by remember { mutableIntStateOf(initialPane) }
    var city by remember { mutableStateOf("All") }
    var playerSport by remember { mutableStateOf("All") }
    val cities = remember(state.events) { listOf("All") + state.events.map { it.city }.distinct().sorted() }
    val playerSports = remember(state.sports) { listOf("All") + state.sports.map { it.name } }
    var playing by remember { mutableStateOf<HighlightClip?>(null) }
    var destination by remember { mutableStateOf<Destination?>(null) }
    var sportProfile by remember { mutableStateOf<SportProfile?>(null) }
    var coach by remember { mutableStateOf<Coach?>(null) }
    var player by remember { mutableStateOf<Player?>(null) }
    var creatingEvent by remember { mutableStateOf(false) }
    var hostedEvent by remember { mutableStateOf<FestivalEvent?>(null) }
    var chromeHeightPx by remember { mutableFloatStateOf(0f) }
    var overlayHeightPx by remember { mutableFloatStateOf(0f) }
    var titleHeightPx by remember { mutableFloatStateOf(0f) }
    var reelPageOffset by remember { mutableFloatStateOf(0f) }
    val highlightsState = rememberLazyGridState()
    val destinationsState = rememberLazyGridState()
    val eventsScroll = rememberScrollState()
    val sportsScroll = rememberScrollState()
    val coachesScroll = rememberScrollState()
    val playersScroll = rememberScrollState()
    val adsScroll = rememberScrollState()
    val density = LocalDensity.current

    BackHandler(enabled = creatingEvent || hostedEvent != null || playing != null || player != null || sportProfile != null || coach != null || destination != null) {
        when {
            creatingEvent -> creatingEvent = false
            hostedEvent != null -> hostedEvent = null
            playing != null -> playing = null
            player != null -> player = null
            sportProfile != null -> sportProfile = null
            coach != null -> coach = null
            else -> destination = null
        }
    }

    if (creatingEvent) {
        CreateEventForm(
            onSave = { title, sport, cityName, venue, date, time ->
                vm.createEvent(title, sport, cityName, venue, date, time)
                creatingEvent = false
            },
            onBack = { creatingEvent = false }
        )
        return
    }

    if (hostedEvent != null) {
        HostedEventDetail(hostedEvent!!, onBack = { hostedEvent = null })
        return
    }

    if (playing != null) {
        val clip = playing!!
        Box(Modifier.fillMaxSize()) {
            ReelPage(
                reel = ReelClip(clip.id, "highlight", clip.title, clip.videoUrl, clip.imageName),
                destination = null,
                onLike = {},
                onAddToTrail = {}
            )
            Text(
                "Close",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { playing = null }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
        return
    }

    val openPlayer = player
    if (openPlayer != null) {
        AthleteReport(
            player = openPlayer,
            peers = state.players.filter { it.sport == openPlayer.sport },
            onBack = { player = null }
        )
        return
    }

    val openSport = sportProfile
    if (openSport != null) {
        SportDetail(
            sport = openSport,
            coaches = state.coaches.filter { it.sport == openSport.name },
            players = state.players.filter { it.sport == openSport.name },
            events = state.events.filter { it.sport == openSport.name },
            highlights = state.highlights.filter { it.sport == openSport.name },
            onBack = { sportProfile = null },
            onEvent = onEventTicket,
            onPlayer = { player = it }
        )
        return
    }

    val openCoach = coach
    if (openCoach != null) {
        CoachDetail(coach = openCoach, onBack = { coach = null })
        return
    }

    if (destination != null) {
        DestinationDetail(destination!!, onAdd = {
            vm.addToTrail(destination!!.id)
        }, onBack = { destination = null })
        return
    }

    LaunchedEffect(pane) {
        if (pane == PANE_REELS) titleHeightPx = 0f
        reelPageOffset = 0f
    }

    val collapsePx by remember {
        derivedStateOf {
            val scroll = when (pane) {
                PANE_EVENTS -> eventsScroll.value
                PANE_SPORTS -> sportsScroll.value
                PANE_COACHES -> coachesScroll.value
                PANE_RANKINGS -> playersScroll.value
                PANE_ADS -> adsScroll.value
                else -> 0
            }
            when (pane) {
                PANE_REELS -> (abs(reelPageOffset) * 52f).coerceAtMost(52f)
                PANE_HIGHLIGHTS -> collapseFromGrid(highlightsState, titleHeightPx)
                PANE_DESTINATIONS -> collapseFromGrid(destinationsState, titleHeightPx)
                else -> scroll.toFloat().coerceIn(0f, titleHeightPx.coerceAtLeast(0f))
            }
        }
    }
    val topInset = with(density) { chromeHeightPx.toDp() }.takeIf { chromeHeightPx > 0f } ?: 112.dp

    Box(
        Modifier
            .fillMaxSize()
            .background(if (pane == PANE_REELS) Color.Black else MaterialTheme.colorScheme.background)
    ) {
        when (pane) {
            PANE_SPORTS -> SportsList(
                sports = state.sports,
                scrollState = sportsScroll,
                topInset = topInset,
                onOpen = { sportProfile = it }
            )
            PANE_COACHES -> CoachesList(
                coaches = state.coaches,
                ad = null,
                scrollState = coachesScroll,
                topInset = topInset,
                onOpen = { coach = it }
            )
            PANE_RANKINGS -> RankingsList(
                players = state.players,
                sports = state.sports.map { it.name },
                sport = playerSport,
                ad = null,
                scrollState = playersScroll,
                topInset = topInset,
                onOpen = { player = it }
            )
            PANE_ADS -> AdsList(
                ads = state.ads,
                scrollState = adsScroll,
                topInset = topInset
            )
            PANE_EVENTS -> EventsList(
                events = state.events.filter { city == "All" || it.city == city },
                tickets = state.tickets,
                ad = null,
                scrollState = eventsScroll,
                topInset = topInset,
                onEventTicket = onEventTicket,
                onCreate = { creatingEvent = true },
                onHosted = { hostedEvent = it }
            )
            PANE_HIGHLIGHTS -> HighlightsGrid(
                clips = state.highlights,
                state = highlightsState,
                topInset = topInset,
                onPlay = { playing = it }
            )
            PANE_REELS -> ReelsFeed(
                reels = state.reels,
                destinations = state.destinations,
                onLike = vm::toggleLike,
                onAddToTrail = vm::addToTrail,
                onPageOffset = { reelPageOffset = it },
                topPadding = (with(density) { overlayHeightPx.toDp() } + 10.dp).coerceAtLeast(56.dp)
            )
            PANE_DESTINATIONS -> DestinationsGrid(
                destinations = state.destinations,
                state = destinationsState,
                topInset = topInset,
                onOpen = { destination = it }
            )
        }

        DiscoverChrome(
            heading = heading,
            subtitle = subtitle,
            pane = pane,
            onPane = { pane = it },
            city = city,
            cities = cities,
            onCity = { city = it },
            playerSport = playerSport,
            playerSports = playerSports,
            onPlayerSport = { playerSport = it },
            overReels = pane == PANE_REELS,
            collapsePx = collapsePx,
            onChromeHeight = { height ->
                if (height > 0f) {
                    overlayHeightPx = height
                    if (pane != PANE_REELS) chromeHeightPx = height
                }
            },
            onTitleHeight = { titleHeightPx = it }
        )
    }
}

private fun collapseFromGrid(state: LazyGridState, titlePx: Float): Float {
    if (titlePx <= 0f) return 0f
    return if (state.firstVisibleItemIndex > 0) titlePx
    else state.firstVisibleItemScrollOffset.toFloat().coerceIn(0f, titlePx)
}

@Composable
private fun DiscoverChrome(
    heading: String,
    subtitle: String,
    pane: Int,
    onPane: (Int) -> Unit,
    city: String,
    cities: List<String>,
    onCity: (String) -> Unit,
    playerSport: String,
    playerSports: List<String>,
    onPlayerSport: (String) -> Unit,
    overReels: Boolean,
    collapsePx: Float,
    onChromeHeight: (Float) -> Unit,
    onTitleHeight: (Float) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Box(
        Modifier
            .fillMaxWidth()
            .zIndex(1f)
            .onGloballyPositioned { onChromeHeight(it.size.height.toFloat()) }
            .offset { IntOffset(0, -collapsePx.roundToInt()) }
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    if (overReels) {
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Black.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        )
                    } else {
                        Brush.verticalGradient(listOf(colors.background, colors.background))
                    }
                )
        )
        Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            if (!overReels) {
                Column(
                    Modifier.onGloballyPositioned { onTitleHeight(it.size.height.toFloat()) }
                ) {
                    ScreenHeader(heading, subtitle)
                    Spacer(Modifier.height(12.dp))
                }
            }
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                panes.forEachIndexed { index, title ->
                    FilterChip(
                        label = title,
                        selected = pane == index,
                        onClick = { onPane(index) },
                        onDark = overReels
                    )
                }
            }
            if (pane == PANE_RANKINGS) {
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    playerSports.forEach { item ->
                        FilterChip(
                            label = item,
                            selected = playerSport == item,
                            onClick = { onPlayerSport(item) }
                        )
                    }
                }
            }
            if (pane == PANE_EVENTS) {
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cities.forEach { item ->
                        FilterChip(
                            label = item,
                            selected = city == item,
                            onClick = { onCity(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReelsFeed(
    reels: List<ReelClip>,
    destinations: List<Destination>,
    onLike: (String) -> Unit,
    onAddToTrail: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onPageOffset: (Float) -> Unit = {},
    topPadding: Dp = 56.dp,
    bottomPadding: Dp = 24.dp
) {
    val pager = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (reels.size - 1).coerceAtLeast(0)),
        pageCount = { reels.size }
    )
    LaunchedEffect(pager) {
        snapshotFlow { pager.currentPageOffsetFraction }
            .collect { onPageOffset(abs(it)) }
    }
    VerticalPager(
        state = pager,
        modifier = modifier.fillMaxSize()
    ) { page ->
        val reel = reels[page]
        ReelPage(
            reel = reel,
            destination = destinations.firstOrNull { it.id == reel.destinationId },
            onLike = { onLike(reel.id) },
            onAddToTrail = onAddToTrail,
            topPadding = topPadding,
            bottomPadding = bottomPadding
        )
    }
}

@Composable
fun ReelPage(
    reel: ReelClip,
    destination: Destination?,
    onLike: () -> Unit,
    onAddToTrail: (String) -> Unit,
    topPadding: Dp = 56.dp,
    bottomPadding: Dp = 24.dp
) {
    var muted by remember { mutableStateOf(true) }
    Box(
        Modifier
            .fillMaxSize()
            .clickable { muted = !muted }
    ) {
        Image(
            painter = painterResource(imageRes(reel.imageName)),
            contentDescription = reel.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
        )
        Row(
            Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = topPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Gold),
                contentAlignment = Alignment.Center
            ) {
                Text(reel.initials, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(reel.author, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(reel.handle, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
            }
        }
        Row(
            Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 16.dp, bottom = bottomPadding),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    reel.type.replaceFirstChar { it.uppercase() },
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    reel.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (destination != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Add to trail",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onAddToTrail(destination.id) }
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Icon(
                    if (reel.liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (reel.liked) AlertRed else Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onLike)
                )
                Icon(Icons.Outlined.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(24.dp))
                Icon(
                    if (muted) Icons.AutoMirrored.Outlined.VolumeOff else Icons.AutoMirrored.Outlined.VolumeUp,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun HighlightsGrid(
    clips: List<HighlightClip>,
    state: LazyGridState,
    topInset: Dp,
    onPlay: (HighlightClip) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(Modifier.height(topInset))
        }
        items(clips, key = { it.id }) { clip ->
            Column(Modifier.clickable { onPlay(clip) }) {
                Box {
                    Image(
                        painter = painterResource(imageRes(clip.imageName)),
                        contentDescription = clip.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Box(
                        Modifier
                            .align(Alignment.Center)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                GoldLabel(clip.sport)
                Text(clip.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun DestinationsGrid(
    destinations: List<Destination>,
    state: LazyGridState,
    topInset: Dp,
    onOpen: (Destination) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(Modifier.height(topInset))
        }
        items(destinations, key = { it.id }) { item ->
            Column(Modifier.clickable { onOpen(item) }) {
                Image(
                    painter = painterResource(imageRes(item.imageName)),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(8.dp))
                GoldLabel(item.category)
                Text(item.name, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DestinationDetail(destination: Destination, onAdd: () -> Unit, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ScreenHeader(destination.name, destination.category, onBack = onBack)
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(imageRes(destination.imageName)),
            contentDescription = destination.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(14.dp))
        )
        Spacer(Modifier.height(16.dp))
        GoldLabel(destination.category)
        Spacer(Modifier.height(8.dp))
        SerifTitle(destination.name, size = 28)
        Spacer(Modifier.height(8.dp))
        Caption(destination.blurb)
        Spacer(Modifier.height(16.dp))
        if (destination.trailEligible) {
            PrimaryButton("Add to Trail", onAdd, gold = true)
            Spacer(Modifier.height(10.dp))
        }
        PrimaryButton("Book this", {})
    }
}

@Composable
private fun EventsList(
    events: List<FestivalEvent>,
    tickets: List<com.timworksports.crestedpass.data.model.Ticket>,
    ad: PromoAd?,
    scrollState: ScrollState,
    topInset: Dp,
    onEventTicket: (String) -> Unit,
    onCreate: () -> Unit,
    onHosted: (FestivalEvent) -> Unit
) {
    val owned = tickets.map { it.eventId }.toSet()
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(topInset))
        PrimaryButton("Create event", onCreate, gold = true)
        Spacer(Modifier.height(12.dp))
        if (ad != null) {
            AdCard(ad)
            Spacer(Modifier.height(12.dp))
        }
        events.sortedBy { it.kickoffInHours }.forEach { event ->
            val hosted = event.price == 0
            BrandCard(onClick = { if (hosted) onHosted(event) else onEventTicket(event.id) }) {
                Column {
                    SportCover(
                        event = event,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(148.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.height(10.dp))
                    Column {
                        GoldLabel(event.sport)
                        Spacer(Modifier.height(4.dp))
                        Text(event.title, color = colors.onBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Caption("${event.whenLabel} · ${event.city}")
                        Spacer(Modifier.height(6.dp))
                        Row {
                            Text(
                                if (hosted) "Open" else event.price.asUgx(),
                                color = colors.onBackground,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                when {
                                    hosted -> "Your event"
                                    event.id in owned -> "View ticket"
                                    else -> "Buy ticket"
                                },
                                color = if (hosted || event.id !in owned) Gold else colors.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun SportsList(
    sports: List<SportProfile>,
    scrollState: ScrollState,
    topInset: Dp,
    onOpen: (SportProfile) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(topInset))
        sports.forEach { sport ->
            BrandCard(onClick = { onOpen(sport) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(imageRes(sport.imageName)),
                        contentDescription = sport.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(sport.name, color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Caption(sport.federation)
                        Spacer(Modifier.height(4.dp))
                        Text("${sport.athletes} athletes · ${sport.city}", color = colors.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun CoachesList(
    coaches: List<Coach>,
    ad: PromoAd?,
    scrollState: ScrollState,
    topInset: Dp,
    onOpen: (Coach) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(topInset))
        if (ad != null) {
            AdCard(ad)
            Spacer(Modifier.height(12.dp))
        }
        coaches.forEach { person ->
            BrandCard(onClick = { onOpen(person) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(imageRes(person.imageName)),
                        contentDescription = person.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(person.name, color = colors.onBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        GoldLabel("${person.role} · ${person.sport}")
                        Spacer(Modifier.height(4.dp))
                        Caption("${person.rating} · ${person.record} · ${person.years} yrs")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun RankingsList(
    players: List<Player>,
    sports: List<String>,
    sport: String,
    ad: PromoAd?,
    scrollState: ScrollState,
    topInset: Dp,
    onOpen: (Player) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val sections = sports.filter { sport == "All" || it == sport }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(topInset))
        if (ad != null) {
            AdCard(ad)
            Spacer(Modifier.height(12.dp))
        }
        sections.forEach { name ->
            val ranked = players.filter { it.sport == name }
                .sortedWith(compareByDescending<Player> { it.scoring }.thenBy { it.name })
            if (ranked.isEmpty()) return@forEach
            GoldLabel(name)
            Spacer(Modifier.height(8.dp))
            ranked.forEachIndexed { index, athlete ->
            BrandCard(onClick = { onOpen(athlete) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${index + 1}",
                        color = if (index < 3) Gold else colors.onSurfaceVariant,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(28.dp)
                    )
                    Image(
                        painter = painterResource(imageRes(athlete.imageName)),
                        contentDescription = athlete.sport,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(athlete.name, color = colors.onBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Caption("${athlete.club} · ${athlete.sessions().size} activities")
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${athlete.scoring}", color = colors.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Caption(athlete.scoringLabel)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun AdCard(ad: PromoAd) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
    ) {
        Image(
            painter = painterResource(imageRes(ad.imageName)),
            contentDescription = ad.headline,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )
        Column(Modifier.padding(12.dp)) {
            GoldLabel("Sponsored · ${ad.sponsor}")
            Spacer(Modifier.height(4.dp))
            Text(ad.headline, color = colors.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Caption(ad.detail)
        }
    }
}

@Composable
private fun AdsList(
    ads: List<PromoAd>,
    scrollState: ScrollState,
    topInset: Dp
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(topInset))
        ads.forEach { ad ->
            AdCard(ad)
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Caption(label)
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SportDetail(
    sport: SportProfile,
    coaches: List<Coach>,
    players: List<Player>,
    events: List<FestivalEvent>,
    highlights: List<HighlightClip>,
    onBack: () -> Unit,
    onEvent: (String) -> Unit,
    onPlayer: (Player) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ScreenHeader(sport.name, sport.federation, onBack = onBack)
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(imageRes(sport.imageName)),
            contentDescription = sport.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(14.dp))
        )
        Spacer(Modifier.height(16.dp))
        SerifTitle(sport.name, size = 28)
        Caption(sport.federation)
        Spacer(Modifier.height(8.dp))
        Caption(sport.blurb)
        Spacer(Modifier.height(16.dp))
        GoldLabel("Coaches")
        Spacer(Modifier.height(8.dp))
        coaches.forEach { person ->
            Text("${person.name} · ${person.role}", color = colors.onBackground, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(12.dp))
        GoldLabel("Players")
        Spacer(Modifier.height(8.dp))
        players.forEach { athlete ->
            Text(
                "${athlete.name} · ${athlete.position}",
                color = colors.onBackground,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onPlayer(athlete) }
            )
            Caption("${athlete.club} · #${athlete.number}")
            Spacer(Modifier.height(8.dp))
        }
        GoldLabel("Events")
        Spacer(Modifier.height(8.dp))
        events.sortedBy { it.kickoffInHours }.forEach { event ->
            Text(event.title, color = colors.onBackground, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onEvent(event.id) })
            Caption("${event.whenLabel} · ${event.venue}")
            Spacer(Modifier.height(8.dp))
        }
        GoldLabel("Highlights")
        Spacer(Modifier.height(8.dp))
        highlights.forEach { clip ->
            Text(clip.title, color = colors.onBackground, fontWeight = FontWeight.SemiBold)
            Caption(clip.match)
            Spacer(Modifier.height(8.dp))
        }
    }
}

private enum class CoachPanel { Rating, Record, Experience, Book }

private val coachDays = listOf("Mon 28", "Tue 29", "Wed 30", "Thu 1", "Fri 2", "Sat 3", "Sun 4")
private val coachTimes = listOf("06:30", "09:00", "16:00", "18:30")

private fun slotOpen(coach: Coach, day: Int, time: Int): Boolean =
    (day * 3 + time + coach.wins) % 4 != 0

@Composable
private fun CoachDetail(coach: Coach, onBack: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    var panel by remember { mutableStateOf(CoachPanel.Record) }
    var day by remember { mutableIntStateOf(1) }
    var time by remember { mutableIntStateOf(1) }
    var booked by remember { mutableStateOf<String?>(null) }
    Column(
        Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        ScreenHeader(coach.name, "${coach.role} · ${coach.sport}", onBack = onBack)
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(imageRes(coach.imageName)),
            contentDescription = coach.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(14.dp))
        )
        Spacer(Modifier.height(16.dp))
        SerifTitle(coach.name, size = 26)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            CoachAction(Icons.Outlined.Star, "Rating", panel == CoachPanel.Rating) { panel = CoachPanel.Rating }
            CoachAction(Icons.Outlined.EmojiEvents, "Record", panel == CoachPanel.Record) { panel = CoachPanel.Record }
            CoachAction(Icons.Outlined.WorkspacePremium, "Experience", panel == CoachPanel.Experience) { panel = CoachPanel.Experience }
            CoachAction(Icons.Outlined.CalendarMonth, "Book", panel == CoachPanel.Book) { panel = CoachPanel.Book }
        }
        Spacer(Modifier.height(18.dp))
        when (panel) {
            CoachPanel.Rating -> {
                Text("${coach.rating}", color = colors.onBackground, fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
                Caption("out of 5 · ${coach.years * 12} session reviews")
                Spacer(Modifier.height(8.dp))
                Text("★".repeat(coach.rating.toInt()) + "☆".repeat(5 - coach.rating.toInt()), color = Gold, fontSize = 18.sp)
            }
            CoachPanel.Record -> {
                DetailLine("Record", coach.record)
                DetailLine("Wins", "${coach.wins}")
                if (coach.draws > 0) DetailLine("Draws", "${coach.draws}")
                DetailLine("Losses", "${coach.losses}")
                DetailLine("Win rate", "${coach.winRate}%")
                DetailLine("Match weeks", "${coach.years * 28}")
            }
            CoachPanel.Experience -> {
                DetailLine("Years", "${coach.years}")
                DetailLine("Licence", coach.licence)
                DetailLine("Clubs", coach.clubs)
                DetailLine("Role", coach.role)
                Spacer(Modifier.height(8.dp))
                Caption(coach.bio)
            }
            CoachPanel.Book -> {
                Caption("Pick a day, then a session.")
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    coachDays.forEachIndexed { index, label ->
                        val selected = index == day
                        Text(
                            label,
                            color = if (selected) Color.Black else colors.onBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) Gold else colors.surface)
                                .border(1.dp, if (selected) Gold else colors.outline, RoundedCornerShape(8.dp))
                                .clickable { day = index }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                coachTimes.forEachIndexed { index, label ->
                    val open = slotOpen(coach, day, index)
                    val selected = index == time && open
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) colors.surfaceVariant else colors.surface)
                            .clickable(enabled = open) { time = index }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, color = colors.onBackground, fontWeight = FontWeight.SemiBold)
                        Caption(if (open) "Open" else "Taken")
                    }
                }
                Spacer(Modifier.height(12.dp))
                val choice = "${coachDays[day]} · ${coachTimes[time]}"
                val canBook = slotOpen(coach, day, time) && booked != choice
                if (booked != null) {
                    Caption("Session held with ${coach.name}. $booked at the ${coach.sport.lowercase()} ground.")
                }
                PrimaryButton(
                    text = if (booked == choice) "Booked" else "Book $choice",
                    onClick = { booked = choice },
                    gold = true,
                    enabled = canBook
                )
            }
        }
    }
}

@Composable
private fun CoachAction(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (selected) Gold else colors.surface)
                .border(1.dp, if (selected) Gold else colors.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = if (selected) Color.Black else Gold, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = colors.onBackground, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
