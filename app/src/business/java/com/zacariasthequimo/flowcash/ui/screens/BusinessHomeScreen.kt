package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import com.zacariasthequimo.flowcash.ui.UserAvatar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessHomeScreen(
    viewModel: BusinessViewModel,
    onNavigateToBlock: (com.zacariasthequimo.flowcash.BusinessBlock) -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()
    val todayRevenue by viewModel.todayRevenue.collectAsState()
    val monthRevenue by viewModel.monthRevenue.collectAsState()
    val monthSalesCount by viewModel.monthSalesCount.collectAsState()
    val totalCustomers by viewModel.totalCustomers.collectAsState()
    val pendingDebtAmount by viewModel.pendingDebtAmount.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val products by viewModel.products.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshDashboard() }

    val nf = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }

    val recentSales = remember(sales) {
        sales.sortedByDescending { it.date }.take(5)
    }

    val sdf = SimpleDateFormat("d MMM, HH:mm", Locale("pt", "MZ"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        UserAvatar(photoPath = profilePhotoPath, userName = userName)
                        Column {
                            Text(
                                text = "Bem-vindo,",
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
                },
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
                    HomeStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Hoje",
                        value = "${nf.format(todayRevenue)} MT",
                        icon = Icons.Outlined.Today,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HomeStatCard(
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
                    HomeStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Vendas",
                        value = "$monthSalesCount",
                        icon = Icons.Outlined.ShoppingCart,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    HomeStatCard(
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
                    HomeStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Produtos",
                        value = "${products.size}",
                        icon = Icons.Outlined.Inventory2,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    HomeStatCard(
                        modifier = Modifier.weight(1f),
                        title = "D\u00edvidas",
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
                        Text(
                            text = "A\u00e7\u00f5es R\u00e1pidas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            QuickActionBtn(
                                icon = Icons.Outlined.ShoppingCart,
                                label = "Nova Venda",
                                color = MaterialTheme.colorScheme.primary,
                                onClick = { onNavigateToBlock(com.zacariasthequimo.flowcash.BusinessBlock.VENDAS) }
                            )
                            QuickActionBtn(
                                icon = Icons.Outlined.PersonAdd,
                                label = "Cliente",
                                color = MaterialTheme.colorScheme.tertiary,
                                onClick = { onNavigateToBlock(com.zacariasthequimo.flowcash.BusinessBlock.CLIENTES) }
                            )
                            QuickActionBtn(
                                icon = Icons.Outlined.AddBox,
                                label = "Produto",
                                color = MaterialTheme.colorScheme.secondary,
                                onClick = { onNavigateToBlock(com.zacariasthequimo.flowcash.BusinessBlock.PRODUTOS) }
                            )
                            QuickActionBtn(
                                icon = Icons.Outlined.BarChart,
                                label = "Relat\u00f3rio",
                                color = MaterialTheme.colorScheme.error,
                                onClick = { onNavigateToBlock(com.zacariasthequimo.flowcash.BusinessBlock.RELATORIOS) }
                            )
                        }
                    }
                }
            }

            if (recentSales.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "\u00daltimas Vendas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            recentSales.forEach { sale ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = sale.customerName.ifEmpty { "Cliente Geral" },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = sdf.format(Date(sale.date)),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${nf.format(sale.total)} MT",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (sale != recentSales.last()) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeStatCard(
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
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun QuickActionBtn(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
