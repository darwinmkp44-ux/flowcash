package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import com.zacariasthequimo.flowcash.ui.UserAvatar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDashboardScreen(
    viewModel: BusinessViewModel,
    onNewSale: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()
    val todayRevenue by viewModel.todayRevenue.collectAsState()
    val monthRevenue by viewModel.monthRevenue.collectAsState()
    val monthSalesCount by viewModel.monthSalesCount.collectAsState()
    val totalCustomers by viewModel.totalCustomers.collectAsState()
    val pendingDebtAmount by viewModel.pendingDebtAmount.collectAsState()
    val grossProfit by viewModel.grossProfit.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshDashboard() }

    val nf = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        UserAvatar(photoPath = profilePhotoPath, userName = userName)
                        Column {
                            Text("Bom dia,", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(userName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewSale,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nova Venda") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text(
                    "Painel Empresarial",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Hoje",
                        value = "${nf.format(todayRevenue)} MT",
                        icon = Icons.Outlined.Today,
                        color = MaterialTheme.colorScheme.primary
                    )
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Este Mês",
                        value = "${nf.format(monthRevenue)} MT",
                        icon = Icons.Outlined.DateRange,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Vendas",
                        value = "$monthSalesCount",
                        icon = Icons.Outlined.ShoppingCart,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Clientes",
                        value = "$totalCustomers",
                        icon = Icons.Outlined.People,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Lucro Bruto",
                        value = "${nf.format(grossProfit)} MT",
                        icon = Icons.Outlined.TrendingUp,
                        color = Color(0xFF22C55E)
                    )
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Dívidas Pend.",
                        value = "${nf.format(pendingDebtAmount)} MT",
                        icon = Icons.Outlined.MonetizationOn,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Resumo Rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        BulletPoint("Receitas do mês: ${nf.format(monthRevenue)} MT", Icons.Outlined.TrendingUp, Color(0xFF22C55E))
                        BulletPoint("Vendas realizadas: $monthSalesCount", Icons.Outlined.ShoppingCart, MaterialTheme.colorScheme.primary)
                        BulletPoint("Clientes registados: $totalCustomers", Icons.Outlined.People, MaterialTheme.colorScheme.tertiary)
                        BulletPoint("Dívidas a receber: ${nf.format(pendingDebtAmount)} MT", Icons.Outlined.MonetizationOn, Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun BulletPoint(text: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
