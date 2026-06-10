package com.zacariasthequimo.flowcash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.zacariasthequimo.flowcash.ui.FinanceViewModel
import com.zacariasthequimo.flowcash.ui.screens.*
import com.zacariasthequimo.flowcash.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[FinanceViewModel::class.java]

        setContent {
            var onboardingDone by remember { mutableStateOf(viewModel.isOnboardingComplete) }
            var notificationPermissionDone by remember { mutableStateOf(false) }

            MyApplicationTheme {
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
                        AppOrchestrator(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

enum class ProTab(
    val route: String,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    HISTORY("history", "Hist\u00f3rico", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    ESTATISTICAS("estatisticas", "Estat\u00edsticas", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard),
    GOALS("goals", "Metas", Icons.Filled.TrackChanges, Icons.Outlined.TrackChanges),
    PROFILE("profile", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AppOrchestrator(
    viewModel: FinanceViewModel
) {
    var activeTab by remember { mutableStateOf(ProTab.HOME) }
    var showAccountDetail by remember { mutableStateOf(false) }
    var showSecurity by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }

    val activeSubScreen = showAccountDetail || showSecurity || showExport

    BackHandler(enabled = activeSubScreen) {
        when {
            showAccountDetail -> showAccountDetail = false
            showSecurity -> showSecurity = false
            showExport -> showExport = false
        }
    }

    var showTransactionDialog by remember { mutableStateOf(false) }

    val navItems = ProTab.entries.toList()

    Scaffold(
        bottomBar = {
            if (!activeSubScreen) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            navItems.forEachIndexed { index, tab ->
                                if (index == 2) {
                                    Spacer(modifier = Modifier.width(60.dp))
                                } else {
                                    val isSelected = activeTab == tab
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { activeTab = tab }
                                            .padding(vertical = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
                                                    else Color.Transparent
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                                contentDescription = tab.title,
                                                tint = if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = tab.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                fontSize = 10.sp
                                            ),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-25).dp)
                            .size(64.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { showTransactionDialog = true }
                            .testTag("add_button_nav"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nova Transa\u00e7\u00e3o",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
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
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when {
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
                        ProTab.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToHistory = { activeTab = ProTab.HISTORY }
                            )
                        }
                        ProTab.HISTORY -> {
                            HistoryScreen(
                                viewModel = viewModel
                            )
                        }
                        ProTab.ESTATISTICAS -> {
                            ResumoScreen(
                                viewModel = viewModel
                            )
                        }
                        ProTab.GOALS -> {
                            GoalsScreen(
                                viewModel = viewModel
                            )
                        }
                        ProTab.PROFILE -> {
                            ProfileScreen(
                                viewModel = viewModel,
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

    if (showTransactionDialog) {
        TransactionDialog(
            viewModel = viewModel,
            onDismiss = { showTransactionDialog = false }
        )
    }
}
