package com.example.atunes.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.atunes.data.model.Track
import com.example.atunes.service.PlaybackController
import com.example.atunes.ui.components.MiniPlayer
import com.example.atunes.ui.home.HomeScreen
import com.example.atunes.ui.library.LibraryScreen
import com.example.atunes.ui.nowplaying.NowPlayingScreen
import com.example.atunes.ui.onboarding.ScanningScreen
import com.example.atunes.ui.onboarding.WelcomeScreen
import com.example.atunes.ui.search.SearchScreen
import com.example.atunes.ui.settings.SettingsScreen
import com.example.atunes.ui.theme.AccentRed

// ── Route constants ──────────────────────────────────────────────────────────
object Routes {
    const val WELCOME   = "welcome"
    const val SCANNING  = "scanning"
    const val HOME      = "home"
    const val SEARCH    = "search"
    const val LIBRARY   = "library"
    const val SETTINGS  = "settings"
    const val NOW_PLAYING = "now_playing"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME,    "Home",    Icons.Rounded.Home,        Icons.Rounded.Home),
    BottomNavItem(Routes.SEARCH,  "Search",  Icons.Rounded.Search,      Icons.Rounded.Search),
    BottomNavItem(Routes.LIBRARY, "Library", Icons.Rounded.LibraryMusic, Icons.Rounded.LibraryMusic),
    BottomNavItem(Routes.SETTINGS,"Settings",Icons.Rounded.Settings,    Icons.Rounded.Settings)
)

@Composable
fun AppNavGraph(
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    isFirstLaunch: Boolean
) {
    val navController = rememberNavController()
    val nowPlayingState by PlaybackController.state.collectAsStateWithLifecycle()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    val showMiniPlayer = nowPlayingState.track != null && currentRoute != Routes.NOW_PLAYING

    val startDest = if (isFirstLaunch) Routes.WELCOME else Routes.HOME

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    // Mini player above nav bar
                    AnimatedVisibility(
                        visible = showMiniPlayer,
                        enter = slideInVertically { it } + fadeIn(),
                        exit  = slideOutVertically { it } + fadeOut()
                    ) {
                        MiniPlayer(
                            state = nowPlayingState,
                            onTogglePlayPause = PlaybackController::togglePlayPause,
                            onSkipNext = PlaybackController::skipNext,
                            onClick = { navController.navigate(Routes.NOW_PLAYING) }
                        )
                    }

                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 0.dp
                    ) {
                        val hierarchy = currentBackStack?.destination?.hierarchy
                        bottomNavItems.forEach { item ->
                            val selected = hierarchy?.any { it.route == item.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(item.label, style = MaterialTheme.typography.labelSmall)
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AccentRed,
                                    selectedTextColor = AccentRed,
                                    indicatorColor = AccentRed.copy(alpha = 0.15f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(tween(300)) { -it / 4 } + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(tween(300)) { -it / 4 } + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(tween(300)) { it / 4 } + fadeOut(tween(300))
            }
        ) {
            // ── Onboarding ────────────────────────────────────────────────
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Routes.SCANNING) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.SCANNING) {
                ScanningScreen(
                    onScanComplete = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SCANNING) { inclusive = true }
                        }
                    }
                )
            }

            // ── Main tabs ─────────────────────────────────────────────────
            composable(Routes.HOME) {
                HomeScreen(
                    isDark = isDark,
                    onThemeToggle = onThemeToggle,
                    onTrackClick = { tracks, idx ->
                        PlaybackController.play(tracks, idx)
                        navController.navigate(Routes.NOW_PLAYING)
                    }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen()
            }
            composable(Routes.LIBRARY) {
                LibraryScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(isDark = isDark, onThemeToggle = onThemeToggle)
            }

            // ── Now Playing (full-screen) ──────────────────────────────────
            composable(
                route = Routes.NOW_PLAYING,
                enterTransition = {
                    slideInVertically(tween(400)) { it } + fadeIn(tween(400))
                },
                exitTransition = {
                    slideOutVertically(tween(400)) { it } + fadeOut(tween(400))
                }
            ) {
                NowPlayingScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
