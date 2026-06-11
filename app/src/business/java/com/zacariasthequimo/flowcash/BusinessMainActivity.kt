package com.zacariasthequimo.flowcash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.zacariasthequimo.flowcash.data.entity.Goal
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import com.zacariasthequimo.flowcash.ui.FinanceViewModel
import com.zacariasthequimo.flowcash.ui.UserAvatar
import com.zacariasthequimo.flowcash.ui.screens.*
import com.zacariasthequimo.flowcash.ui.theme.MyApplicationTheme

enum class BizTab(
    val route: String,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BUSINESS("business", "Business", Icons.Filled.Business, Icons.Outlined.Business),
    ESTATISTICAS("estatisticas", "Estat\u00edsticas", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    DEFINICOES("definicoes", "Defini\u00e7\u00f5es", Icons.Filled.Settings, Icons.Outlined.Settings)
}

enum class BusinessBlock(val title: String, val icon: ImageVector, val description: String) {
    CLIENTES("Clientes", Icons.Outlined.People, "Gerir clientes e contactos"),
    VENDAS("Vendas", Icons.Outlined.ShoppingCart, "Registar e acompanhar vendas"),
    DIVIDAS("D\u00edvidas", Icons.Outlined.MonetizationOn, "Controlo de d\u00edvidas"),
    PRODUTOS("Produtos", Icons.Outlined.Inventory2, "Stock e produtos"),
    AGENDA("Agenda", Icons.Outlined.CalendarMonth, "Compromissos e lembretes"),
    MODULOS("M\u00f3dulos", Icons.Outlined.Extension, "Ativar/desativar m\u00f3dulos"),
    RELATORIOS("Relat\u00f3rios", Icons.Outlined.BarChart, "Lucros e desempenho")
}

class BusinessMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val businessViewModel = ViewModelProvider(this)[BusinessViewModel::class.java]
        val financeViewModel = businessViewModel as FinanceViewModel

        setContent {
            var onboardingDone by remember { mutableStateOf(financeViewModel.isOnboardingComplete) }
            var notificationPermissionDone by remember { mutableStateOf(false) }

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!onboardingDone) {
                        OnboardingScreen(
                            viewModel = financeViewModel,
                            onComplete = { onboardingDone = true }
                        )
                    } else if (!notificationPermissionDone) {
                        NotificationPermissionScreen(
                            onComplete = { notificationPermissionDone = true }
                        )
                    } else {
                        BusinessOrchestrator(viewModel = businessViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessOrchestrator(
    viewModel: BusinessViewModel
) {
    var activeTab by remember { mutableStateOf(BizTab.HOME) }
    var activeBlock by remember { mutableStateOf<BusinessBlock?>(null) }
    var showAccountDetail by remember { mutableStateOf(false) }
    var showSecurity by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }

    val activeSubScreen = activeBlock != null || showAccountDetail || showSecurity || showExport

    BackHandler(enabled = activeSubScreen) {
        when {
            activeBlock != null -> activeBlock = null
            showAccountDetail -> showAccountDetail = false
            showSecurity -> showSecurity = false
            showExport -> showExport = false
        }
    }

    val bizNavItems = BizTab.entries.toList()

    Scaffold(
        topBar = {
            if (activeBlock != null) {
                TopAppBar(
                    title = { Text(activeBlock!!.title, style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { activeBlock = null }) {
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
            if (!activeSubScreen) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
                    )) {
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
                            bizNavItems.forEach { tab ->
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
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                activeBlock != null -> {
                    BusinessBlockContent(
                        block = activeBlock!!,
                        viewModel = viewModel
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
                        BizTab.HOME -> {
                            BusinessHomeScreen(
                                viewModel = viewModel,
                                onNavigateToBlock = { activeBlock = it }
                            )
                        }
                        BizTab.BUSINESS -> {
                            BusinessBlocksScreen(
                                viewModel = viewModel,
                                onBlockClick = { activeBlock = it }
                            )
                        }
                        BizTab.ESTATISTICAS -> {
                            BusinessEstatisticasScreen(
                                viewModel = viewModel
                            )
                        }
                        BizTab.DEFINICOES -> {
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
}

@Composable
fun BusinessBlockContent(
    block: BusinessBlock,
    viewModel: BusinessViewModel
) {
    when (block) {
        BusinessBlock.CLIENTES -> CrmScreen(viewModel = viewModel)
        BusinessBlock.VENDAS -> SaleScreen(viewModel = viewModel, onNewSale = { })
        BusinessBlock.DIVIDAS -> DebtScreen(viewModel = viewModel)
        BusinessBlock.PRODUTOS -> ProductScreen(viewModel = viewModel)
        BusinessBlock.AGENDA -> AgendaScreen()
        BusinessBlock.MODULOS -> ModulesScreen(viewModel = viewModel)
        BusinessBlock.RELATORIOS -> ProfitLossScreen(viewModel = viewModel)
    }
}


