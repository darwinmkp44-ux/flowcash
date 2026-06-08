package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zacariasthequimo.flowcash.data.dao.PaymentMethodRevenue
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import com.zacariasthequimo.flowcash.ui.UserAvatar
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

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

    val paymentMethodRevenue by viewModel.paymentMethodRevenue.collectAsState()

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

            if (paymentMethodRevenue.isNotEmpty()) {
                item {
                    PaymentMethodPieChart(data = paymentMethodRevenue, nf = nf)
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodPieChart(
    data: List<PaymentMethodRevenue>,
    nf: NumberFormat
) {
    val total = data.sumOf { it.total }
    if (total <= 0) return

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.inversePrimary
    )

            val chartColors = remember(data) {
                data.mapIndexed { i, _ -> colors[i % colors.size] }
            }

    val strokeWidth = 24.dp
    val bgColor = MaterialTheme.colorScheme.surfaceContainerLowest

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Receitas por Pagamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    val canvasSize = this.size
                    val d = max(canvasSize.width, canvasSize.height)
                    val topLeft = Offset(
                        (canvasSize.width - d) / 2f,
                        (canvasSize.height - d) / 2f
                    )
                    val arcSize = d
                    val sw = strokeWidth.toPx()

                    var startAngle = -90f
                    data.forEachIndexed { i, item ->
                        val sweep = (item.total / total * 360f).toFloat()
                        drawArc(
                            color = chartColors[i],
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = sw, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                    drawCircle(
                        color = bgColor,
                        radius = d / 2 - sw / 2,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            data.forEachIndexed { i, item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier.size(10.dp).background(
                            chartColors[i],
                            RoundedCornerShape(2.dp)
                        )
                    )
                    Text(item.paymentMethod, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text("${nf.format(item.total)} MT", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    val pct = (item.total / total * 100)
                    Text("${"%.1f".format(pct)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
