package com.timworksports.crestedpass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Sports
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.timworksports.crestedpass.data.repository.MockRepository
import com.timworksports.crestedpass.navigation.AppGraph
import com.timworksports.crestedpass.navigation.CrestedPassViewModelFactory
import com.timworksports.crestedpass.ui.auth.AuthScreen
import com.timworksports.crestedpass.ui.ar.ArLensScreen
import com.timworksports.crestedpass.ui.band.BandScreen
import com.timworksports.crestedpass.ui.discover.DiscoverScreen
import com.timworksports.crestedpass.ui.embassy.EmbassyScreen
import com.timworksports.crestedpass.ui.home.HomeScreen
import com.timworksports.crestedpass.ui.home.NotificationsScreen
import com.timworksports.crestedpass.ui.predict.PredictScreen
import com.timworksports.crestedpass.ui.profile.ProfileScreen
import com.timworksports.crestedpass.ui.profile.SettingsScreen
import com.timworksports.crestedpass.ui.shop.ShopScreen
import com.timworksports.crestedpass.ui.splash.SplashScreen
import com.timworksports.crestedpass.ui.theme.CrestedPassTheme
import com.timworksports.crestedpass.ui.theme.DeepGreenBright
import com.timworksports.crestedpass.ui.theme.Muted
import com.timworksports.crestedpass.ui.theme.ThemeMode
import com.timworksports.crestedpass.ui.ticket.TicketScreen
import com.timworksports.crestedpass.ui.trail.TrailScreen
import com.timworksports.crestedpass.ui.wallet.WalletScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = MockRepository()
        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.System) }
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
                ThemeMode.System -> systemDark
            }
            CrestedPassTheme(darkTheme = darkTheme) {
                var showSplash by remember { mutableStateOf(true) }
                var signedIn by remember { mutableStateOf(false) }
                var authSession by remember { mutableIntStateOf(0) }
                Box(Modifier.fillMaxSize()) {
                    when {
                        showSplash -> SplashScreen { showSplash = false }
                        !signedIn -> key(authSession) {
                            AuthScreen { signedIn = true }
                        }
                        else -> CrestedPassApp(
                            repository = repository,
                            themeMode = themeMode,
                            onSetTheme = { themeMode = it },
                            onSignOut = {
                                signedIn = false
                                authSession++
                            }
                        )
                    }
                }
            }
        }
    }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(AppGraph.Home, "Home", Icons.Outlined.Home),
    Tab(AppGraph.Coaches, "Coaches", Icons.Outlined.Sports),
    Tab(AppGraph.Events, "Events", Icons.Outlined.Event),
    Tab(AppGraph.Athletes, "Athletes", Icons.Outlined.Groups),
    Tab(AppGraph.Shop, "Shop", Icons.Outlined.ShoppingBag),
    Tab(AppGraph.Settings, "Settings", Icons.Outlined.Settings)
)

private fun screenTitle(route: String): String = when {
    route == AppGraph.You -> "You"
    route == AppGraph.Predict -> "Predict"
    route == AppGraph.Settings -> "Settings"
    route == AppGraph.Notifications -> "Notifications"
    route == AppGraph.Embassy -> "Fan Embassy"
    route == AppGraph.Ticket || route.startsWith("event/") -> "Ticket"
    route == AppGraph.Band -> "Crested Band"
    route == AppGraph.ArLens -> "Scan"
    else -> "Timwork Sports"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrestedPassApp(
    repository: MockRepository,
    themeMode: ThemeMode,
    onSetTheme: (ThemeMode) -> Unit,
    onSignOut: () -> Unit
) {
    val factory = remember(repository) { CrestedPassViewModelFactory(repository) }
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route.orEmpty()
    val showBar = tabs.any { it.route == route }
    val colors = MaterialTheme.colorScheme

    fun goTab(target: String) {
        nav.navigate(target) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            if (!showBar && route.isNotEmpty()) {
                TopAppBar(
                    title = { Text(screenTitle(route), color = colors.onBackground) },
                    navigationIcon = {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = DeepGreenBright)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.background,
                        titleContentColor = colors.onBackground,
                        navigationIconContentColor = DeepGreenBright
                    )
                )
            }
        },
        bottomBar = {
            if (showBar) {
                NavigationBar(
                    containerColor = colors.background,
                    tonalElevation = 0.dp,
                    contentColor = colors.onBackground
                ) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = { goTab(tab.route) },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DeepGreenBright,
                                selectedTextColor = DeepGreenBright,
                                indicatorColor = colors.surfaceVariant,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavHost(navController = nav, startDestination = AppGraph.Home) {
                composable(AppGraph.Home) {
                    HomeScreen(
                        factory = factory,
                        onPay = { goTab(AppGraph.Wallet) },
                        onPredict = { nav.navigate(AppGraph.Predict) },
                        onTrail = { goTab(AppGraph.Trail) },
                        onScan = { nav.navigate(AppGraph.ArLens) },
                        onEmbassy = { nav.navigate(AppGraph.Embassy) },
                        onTicket = { },
                        onNotifications = { nav.navigate(AppGraph.Notifications) },
                        onBand = { nav.navigate(AppGraph.Band) },
                        onProfile = { nav.navigate(AppGraph.You) },
                        onCoaches = { goTab(AppGraph.Coaches) },
                        onEvents = { goTab(AppGraph.Events) },
                        onAthletes = { goTab(AppGraph.Athletes) },
                        onShop = { goTab(AppGraph.Shop) },
                        onEventTicket = { nav.navigate(AppGraph.eventTicket(it)) }
                    )
                }
                composable(AppGraph.Coaches) {
                    DiscoverScreen(
                        factory = factory,
                        initialPane = 1,
                        heading = "Coaches",
                        subtitle = "Programmes and staff",
                        onEventTicket = { nav.navigate(AppGraph.eventTicket(it)) }
                    )
                }
                composable(AppGraph.Events) {
                    DiscoverScreen(
                        factory = factory,
                        initialPane = 0,
                        heading = "Events",
                        subtitle = "Fixtures and sessions",
                        onEventTicket = { nav.navigate(AppGraph.eventTicket(it)) }
                    )
                }
                composable(AppGraph.Athletes) {
                    DiscoverScreen(
                        factory = factory,
                        initialPane = 2,
                        heading = "Athletes",
                        subtitle = "Profiles and rankings by sport",
                        onEventTicket = { nav.navigate(AppGraph.eventTicket(it)) }
                    )
                }
                composable(AppGraph.Shop) { ShopScreen(factory) }
                composable(AppGraph.Wallet) { WalletScreen(factory) }
                composable(AppGraph.Discover) {
                    DiscoverScreen(
                        factory = factory,
                        onEventTicket = { nav.navigate(AppGraph.eventTicket(it)) }
                    )
                }
                composable(AppGraph.Trail) { TrailScreen(factory) }
                composable(AppGraph.You) {
                    ProfileScreen(
                        factory = factory,
                        onWallet = { goTab(AppGraph.Wallet) },
                        onTrail = { goTab(AppGraph.Trail) },
                        onPredict = { nav.navigate(AppGraph.Predict) },
                        onTicket = { nav.navigate(AppGraph.Ticket) },
                        onBand = { nav.navigate(AppGraph.Band) },
                        onEmbassy = { nav.navigate(AppGraph.Embassy) },
                        onSettings = { goTab(AppGraph.Settings) },
                        onSignOut = onSignOut
                    )
                }
                composable(AppGraph.Notifications) {
                    NotificationsScreen()
                }
                composable(AppGraph.Settings) {
                    SettingsScreen(
                        factory = factory,
                        themeMode = themeMode,
                        onSetTheme = onSetTheme,
                        onBack = null,
                        onWallet = { goTab(AppGraph.Wallet) },
                        onBand = { nav.navigate(AppGraph.Band) },
                        onEmbassy = { nav.navigate(AppGraph.Embassy) },
                        onSignOut = onSignOut
                    )
                }
                composable(AppGraph.Predict) {
                    PredictScreen(factory, onBack = { nav.popBackStack() })
                }
                composable(AppGraph.Embassy) { EmbassyScreen(factory) }
                composable(AppGraph.Ticket) { TicketScreen(factory) }
                composable(
                    route = AppGraph.EventTicket,
                    arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                ) { entry ->
                    TicketScreen(factory, eventId = entry.arguments?.getString("eventId"))
                }
                composable(AppGraph.ArLens) { ArLensScreen() }
                composable(AppGraph.Band) { BandScreen(factory) }
            }
        }
    }
}
