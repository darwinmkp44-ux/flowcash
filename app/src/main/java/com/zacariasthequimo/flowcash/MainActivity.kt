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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
                    .launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize the central FinanceViewModel
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
    HISTORY("history", "Histórico", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    ANALYTICS("analytics", "Estatísticas", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard),
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
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* user granted or denied */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

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
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
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
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
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
            // Screen state controller
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

// Simple extension helper for MaterialTheme color customization inside compose blocks
@Composable
fun ColorScheme.withAlpha(alpha: Float): Color {
    return this.onSurface.copy(alpha = alpha)
}
