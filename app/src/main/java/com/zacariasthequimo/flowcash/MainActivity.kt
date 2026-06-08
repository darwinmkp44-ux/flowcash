package com.zacariasthequimo.flowcash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.zacariasthequimo.flowcash.ui.FinanceViewModel
import com.zacariasthequimo.flowcash.ui.ThemeMode
import com.zacariasthequimo.flowcash.ui.screens.*
import com.zacariasthequimo.flowcash.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[FinanceViewModel::class.java]

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDarkMode = when (themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            var onboardingDone by remember { mutableStateOf(viewModel.isOnboardingComplete) }
            var notificationPermissionDone by remember { mutableStateOf(false) }

            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!onboardingDone) {
                        OnboardingScreen(
                            viewModel = viewModel,
                            onComplete = { onboardingDone = true }
                        )
                    } else if (!notificationPermissionDone) {
                        NotificationPermissionScreen(
                            onComplete = { notificationPermissionDone = true }
                        )
                    } else {
                        AppOrchestrator(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            themeMode = themeMode,
                            onSetThemeMode = { viewModel.setThemeMode(it) }
                        )
                    }
                }
            }
        }
    }
}

enum class BottomNavTab(
    val route: String,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    HISTORY("history", "Hist\u00f3rico", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    ANALYTICS("analytics", "Estat\u00edsticas", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard),
    GOALS("goals", "Metas", Icons.Filled.TrackChanges, Icons.Outlined.TrackChanges),
    PROFILE("profile", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AppOrchestrator(
    viewModel: FinanceViewModel,
    isDarkMode: Boolean,
    themeMode: ThemeMode,
    onSetThemeMode: (ThemeMode) -> Unit
) {
    var activeTab by remember { mutableStateOf(BottomNavTab.HOME) }
    var isAddingTransaction by remember { mutableStateOf(false) }
    var showAccountDetail by remember { mutableStateOf(false) }
    var showSecurity by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }

    val activeSubScreen = showAccountDetail || showSecurity || showExport

    Scaffold(
        bottomBar = {
            if (!isAddingTransaction && !activeSubScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    BottomNavTab.entries.forEach { tab ->
                        val isSelected = activeTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { activeTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.testTag("nav_item_${tab.route}")
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isAddingTransaction) 0.dp else innerPadding.calculateBottomPadding())
        ) {
            when {
                isAddingTransaction -> {
                    NewTransactionScreen(
                        viewModel = viewModel,
                        onNavigateBack = { isAddingTransaction = false }
                    )
                }
                showAccountDetail -> {
                    AccountDetailScreen(
                        viewModel = viewModel,
                        onBack = { showAccountDetail = false }
                    )
                }
                showSecurity -> {
                    SecurityScreen(
                        viewModel = viewModel,
                        onBack = { showSecurity = false }
                    )
                }
                showExport -> {
                    ExportScreen(
                        viewModel = viewModel,
                        onBack = { showExport = false }
                    )
                }
                else -> {
                    when (activeTab) {
                        BottomNavTab.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToNewTransaction = { isAddingTransaction = true },
                                onNavigateToHistory = { activeTab = BottomNavTab.HISTORY }
                            )
                        }
                        BottomNavTab.HISTORY -> {
                            HistoryScreen(
                                viewModel = viewModel
                            )
                        }
                        BottomNavTab.ANALYTICS -> {
                            AnalyticsScreen(
                                viewModel = viewModel
                            )
                        }
                        BottomNavTab.GOALS -> {
                            GoalsScreen(
                                viewModel = viewModel
                            )
                        }
                        BottomNavTab.PROFILE -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                isDarkMode = isDarkMode,
                                themeMode = themeMode,
                                onSetThemeMode = onSetThemeMode,
                                onNavigateToAccount = { showAccountDetail = true },
                                onNavigateToSecurity = { showSecurity = true },
                                onNavigateToExport = { showExport = true }
                            )
                        }
                    }
                }
            }
        }
    }
}
