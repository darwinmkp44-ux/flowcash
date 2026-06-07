package com.zacariasthequimo.flowcash

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import com.zacariasthequimo.flowcash.ui.FinanceViewModel
import com.zacariasthequimo.flowcash.ui.ThemeMode
import com.zacariasthequimo.flowcash.ui.screens.*
import com.zacariasthequimo.flowcash.ui.theme.MyApplicationTheme

enum class BusinessTab(
    val route: String,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    DASHBOARD("dashboard", "Painel", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    CRM("crm", "Clientes", Icons.Filled.People, Icons.Outlined.People),
    SALES("sales", "Vendas", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    STOCK("stock", "Stock", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    MORE("more", "Mais", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}

enum class BusinessSubScreen(
    val title: String,
    val icon: ImageVector
) {
    PRODUCTS("Produtos", Icons.Outlined.Sell),
    DEBTS("Dívidas", Icons.Outlined.MonetizationOn),
    TEAM("Equipa", Icons.Outlined.Groups),
    REPORTS("Relatórios", Icons.Outlined.BarChart),
    FINANCES("Finanças Pessoais", Icons.Outlined.AccountBalanceWallet),
    PROFILE("Perfil", Icons.Outlined.Person)
}

class BusinessMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val businessViewModel = ViewModelProvider(this)[BusinessViewModel::class.java]
        val financeViewModel = businessViewModel as FinanceViewModel

        setContent {
            val themeMode by financeViewModel.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDarkMode = when (themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            var onboardingDone by remember { mutableStateOf(financeViewModel.isOnboardingComplete) }

            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!onboardingDone) {
                        OnboardingScreen(
                            viewModel = financeViewModel,
                            onComplete = { onboardingDone = true }
                        )
                    } else {
                        BusinessOrchestrator(
                            viewModel = businessViewModel,
                            isDarkMode = isDarkMode,
                            themeMode = themeMode,
                            onSetThemeMode = { financeViewModel.setThemeMode(it) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessOrchestrator(
    viewModel: BusinessViewModel,
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

    var activeTab by remember { mutableStateOf(BusinessTab.DASHBOARD) }
    var isAddingTransaction by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var subScreen by remember { mutableStateOf<BusinessSubScreen?>(null) }
    var showAccountDetail by remember { mutableStateOf(false) }
    var showSecurity by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }

    val activeSubScreen = subScreen != null || showAccountDetail || showSecurity || showExport

    Scaffold(
        topBar = {
            if (showMoreMenu || subScreen != null) {
                TopAppBar(
                    title = {
                        Text(
                            subScreen?.title ?: "Mais",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            subScreen = null
                            showMoreMenu = false
                            showAccountDetail = false
                            showSecurity = false
                            showExport = false
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            if (!isAddingTransaction && !activeSubScreen && !showMoreMenu) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    BusinessTab.entries.forEach { tab ->
                        val isSelected = activeTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                activeTab = tab
                                if (tab == BusinessTab.MORE) showMoreMenu = true
                            },
                            icon = {
                                Icon(
                                    if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    tab.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                showMoreMenu -> {
                    MoreMenu(
                        onSelect = { screen ->
                            subScreen = screen
                            showMoreMenu = false
                        }
                    )
                }
                subScreen != null -> {
                    BusinessSubScreenContent(
                        screen = subScreen!!,
                        viewModel = viewModel,
                        isDarkMode = isDarkMode,
                        themeMode = themeMode,
                        onSetThemeMode = onSetThemeMode,
                        showAccountDetail = { showAccountDetail = true },
                        showSecurity = { showSecurity = true },
                        showExport = { showExport = true }
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
                isAddingTransaction -> {
                    NewTransactionScreen(
                        viewModel = viewModel,
                        onNavigateBack = { isAddingTransaction = false }
                    )
                }
                else -> {
                    when (activeTab) {
                        BusinessTab.DASHBOARD -> {
                            BusinessDashboardScreen(
                                viewModel = viewModel,
                                onNewSale = { activeTab = BusinessTab.SALES }
                            )
                        }
                        BusinessTab.CRM -> {
                            CrmScreen(viewModel = viewModel)
                        }
                        BusinessTab.SALES -> {
                            SaleScreen(
                                viewModel = viewModel,
                                onNewSale = { isAddingTransaction = false }
                            )
                        }
                        BusinessTab.STOCK -> {
                            ProductScreen(viewModel = viewModel)
                        }
                        BusinessTab.MORE -> {
                            MoreMenu(
                                onSelect = { screen ->
                                    subScreen = screen
                                    showMoreMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoreMenu(onSelect: (BusinessSubScreen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Mais Opções",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BusinessSubScreen.entries.forEach { screen ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(screen) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        screen.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        screen.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun BusinessSubScreenContent(
    screen: BusinessSubScreen,
    viewModel: BusinessViewModel,
    isDarkMode: Boolean,
    themeMode: ThemeMode,
    onSetThemeMode: (ThemeMode) -> Unit,
    showAccountDetail: () -> Unit,
    showSecurity: () -> Unit,
    showExport: () -> Unit
) {
    when (screen) {
        BusinessSubScreen.PRODUCTS -> ProductScreen(viewModel = viewModel)
        BusinessSubScreen.DEBTS -> DebtScreen(viewModel = viewModel)
        BusinessSubScreen.TEAM -> TeamScreen(viewModel = viewModel)
        BusinessSubScreen.REPORTS -> ProfitLossScreen(viewModel = viewModel)
        BusinessSubScreen.FINANCES -> ProFinances(viewModel = viewModel)
        BusinessSubScreen.PROFILE -> {
            ProfileScreen(
                viewModel = viewModel,
                isDarkMode = isDarkMode,
                themeMode = themeMode,
                onSetThemeMode = onSetThemeMode,
                onNavigateToAccount = showAccountDetail,
                onNavigateToSecurity = showSecurity,
                onNavigateToExport = showExport
            )
        }
    }
}

@Composable
fun ProFinances(viewModel: FinanceViewModel) {
    var activeProTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Home", "Histórico", "Metas", "Gráficos").forEachIndexed { i, title ->
                FilterChip(
                    selected = activeProTab == i,
                    onClick = { activeProTab = i },
                    label = { Text(title, fontSize = 12.sp) }
                )
            }
        }

        when (activeProTab) {
            0 -> HomeScreen(
                viewModel = viewModel,
                onNavigateToNewTransaction = {},
                onNavigateToHistory = { activeProTab = 1 }
            )
            1 -> HistoryScreen(viewModel = viewModel)
            2 -> GoalsScreen(viewModel = viewModel)
            3 -> AnalyticsScreen(viewModel = viewModel)
        }
    }
}
