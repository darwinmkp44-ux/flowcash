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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.platform.testTag
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
    PESSOAL("pessoal", "Pessoal", Icons.Filled.Person, Icons.Outlined.Person),
    BUSINESS("business", "Business", Icons.Filled.Business, Icons.Outlined.Business),
    RESUMO("resumo", "Resumo", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
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
    var activeTab by remember { mutableStateOf(BizTab.PESSOAL) }
    var activeBlock by remember { mutableStateOf<BusinessBlock?>(null) }
    var showAccountDetail by remember { mutableStateOf(false) }
    var showSecurity by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }
    var showTransactionDialog by remember { mutableStateOf(false) }

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
                                .padding(start = 4.dp, end = 68.dp),
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

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = (-12).dp, y = (-25).dp)
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
                        BizTab.PESSOAL -> {
                            ProFinances(viewModel = viewModel)
                        }
                        BizTab.BUSINESS -> {
                            BusinessBlocksScreen(
                                viewModel = viewModel,
                                onBlockClick = { activeBlock = it }
                            )
                        }
                        BizTab.RESUMO -> {
                            ResumoScreen(
                                viewModel = viewModel,
                                showTopBar = false
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

    if (showTransactionDialog) {
        TransactionDialog(
            viewModel = viewModel,
            onDismiss = { showTransactionDialog = false }
        )
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

@Composable
fun ProFinances(
    viewModel: FinanceViewModel
) {
    var activeProTab by remember { mutableStateOf(0) }
    val userName by viewModel.userName.collectAsState()
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserAvatar(
                photoPath = profilePhotoPath,
                userName = userName
            )
            Column {
                Text(
                    text = "Bom dia,",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf("Home", "Hist\u00f3rico", "Metas", "Gr\u00e1ficos")
            tabs.forEachIndexed { i, title ->
                FilterChip(
                    selected = activeProTab == i,
                    onClick = { activeProTab = i },
                    label = { Text(title, style = MaterialTheme.typography.labelLarge) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (activeProTab) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { activeProTab = 1 },
                    showTopBar = false
                )
                1 -> HistoryScreen(
                    viewModel = viewModel,
                    showTopBar = false
                )
                2 -> GoalsScreen(
                    viewModel = viewModel,
                    showTopBar = false
                )
                 3 -> ResumoScreen(
                    viewModel = viewModel,
                    showTopBar = false
                )
            }
        }
    }
}
