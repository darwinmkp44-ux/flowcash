package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
                title = { Text("Resumo", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Hoje",
                        value = "${nf.format(todayRevenue)} MT",
                        icon = Icons.Outlined.Today,
                        color = MaterialTheme.colorScheme.primary
                    )
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Este M\u00eas",
                        value = "${nf.format(monthRevenue)} MT",
                        icon = Icons.Outlined.DateRange,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Lucro Bruto",
                        value = "${nf.format(grossProfit)} MT",
                        icon = Icons.Outlined.TrendingUp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "D\u00edvidas Pend.",
                        value = "${nf.format(pendingDebtAmount)} MT",
                        icon = Icons.Outlined.MonetizationOn,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Resumo R\u00e1pido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        BulletPoint("Receitas do m\u00eas: ${nf.format(monthRevenue)} MT", Icons.Outlined.TrendingUp, MaterialTheme.colorScheme.secondary)
                        BulletPoint("Vendas realizadas: $monthSalesCount", Icons.Outlined.ShoppingCart, MaterialTheme.colorScheme.primary)
                        BulletPoint("Clientes registados: $totalCustomers", Icons.Outlined.People, MaterialTheme.colorScheme.tertiary)
                        BulletPoint("D\u00edvidas a receber: ${nf.format(pendingDebtAmount)} MT", Icons.Outlined.MonetizationOn, MaterialTheme.colorScheme.error)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
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
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
