package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacariasthequimo.flowcash.data.dao.PaymentMethodRevenue
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import com.zacariasthequimo.flowcash.ui.UserAvatar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessEstatisticasScreen(
    viewModel: BusinessViewModel
) {
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val poupancaSum by viewModel.poupancaSum.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val products by viewModel.products.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val debts by viewModel.debts.collectAsState()
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

    val balance = totalIncome - totalExpenses - poupancaSum
    val totalPendingDebts = debts.filter { !it.paid }.sumOf { it.amount - it.paidAmount }
    val totalProducts = products.size

    val content = @Composable {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    EstatCard(
                        modifier = Modifier.weight(1f),
                        title = "Saldo",
                        value = "${nf.format(balance)} MT",
                        color = MaterialTheme.colorScheme.primary
                    )
                    EstatCard(
                        modifier = Modifier.weight(1f),
                        title = "D\u00edvidas",
                        value = "${nf.format(totalPendingDebts)} MT",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    EstatCard(
                        modifier = Modifier.weight(1f),
                        title = "Clientes",
                        value = "$totalCustomers",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    EstatCard(
                        modifier = Modifier.weight(1f),
                        title = "Produtos",
                        value = "$totalProducts",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                BusinessRevenueChart(
                    sales = sales,
                    nf = nf
                )
            }

            if (paymentMethodRevenue.isNotEmpty()) {
                item {
                    BusinessPieChart(
                        data = paymentMethodRevenue,
                        nf = nf
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
                            text = "Indicadores",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        IndicadorLinha("Receitas do m\u00eas", "${nf.format(monthRevenue)} MT", MaterialTheme.colorScheme.primary)
                        IndicadorLinha("Vendas no m\u00eas", "$monthSalesCount", MaterialTheme.colorScheme.secondary)
                        IndicadorLinha("Lucro Bruto", "${nf.format(grossProfit)} MT", MaterialTheme.colorScheme.tertiary)
                        IndicadorLinha("Receita Hoje", "${nf.format(todayRevenue)} MT", MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        UserAvatar(photoPath = profilePhotoPath, userName = userName)
                        Text(
                            "Estat\u00edsticas",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}

@Composable
private fun EstatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
private fun BusinessRevenueChart(
    sales: List<com.zacariasthequimo.flowcash.data.entity.Sale>,
    nf: NumberFormat
) {
    val monthlyData = remember(sales) {
        val cal = Calendar.getInstance()
        val months = mutableListOf<Pair<String, Double>>()
        val sdf = SimpleDateFormat("MMM", Locale("pt", "MZ"))
        for (i in 5 downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.MONTH, -i)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val monthStart = cal.timeInMillis
            cal.add(Calendar.MONTH, 1)
            val monthEnd = cal.timeInMillis
            val revenue = sales.filter { it.date in monthStart until monthEnd }.sumOf { it.total }
            months.add(sdf.format(Date(monthStart)) to revenue)
        }
        months
    }

    val maxVal = monthlyData.maxOfOrNull { it.second }?.coerceAtLeast(1.0) ?: 1.0

    val targets = remember(monthlyData, maxVal) {
        monthlyData.map { (it.second / maxVal).toFloat().coerceIn(0f, 1f) }
    }
    val animatedFactors = remember { targets.map { Animatable(0f) } }
    LaunchedEffect(targets) {
        targets.forEachIndexed { index, target ->
            kotlinx.coroutines.delay(index * 50L)
            animatedFactors[index].animateTo(
                targetValue = target,
                animationSpec = tween(600)
            )
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Receitas Mensais",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            Canvas(
                modifier = Modifier.fillMaxWidth().height(160.dp)
            ) {
                val w = size.width
                val h = size.height
                val barCount = monthlyData.size
                val totalGap = (barCount - 1) * 8f
                val barWidth = (w - totalGap) / barCount * 0.7f
                val chartTop = h * 0.05f
                val chartHeight = h * 0.85f
                val barColor = primaryColor

                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(0f, h * 0.9f),
                    end = Offset(w, h * 0.9f),
                    strokeWidth = 1f
                )

                monthlyData.forEachIndexed { index, _ ->
                    val x = index * (barWidth + 8f) + (w - (barWidth * barCount + totalGap)) / 2f
                    val factor = animatedFactors[index].value
                    val barH = factor * chartHeight
                    val baseY = h * 0.9f

                    if (barH > 0f) {
                        drawRect(
                            color = barColor.copy(alpha = 0.8f),
                            topLeft = Offset(x + 2f, baseY - barH),
                            size = Size(barWidth - 4f, barH),
                            style = Stroke(width = 0f)
                        )
                        drawRect(
                            color = barColor.copy(alpha = 0.8f),
                            topLeft = Offset(x + 2f, baseY - barH),
                            size = Size(barWidth - 4f, barH)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                monthlyData.forEach { (label, _) ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BusinessPieChart(
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

    val bgPieStrokeColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
    val strokeWidth = 24.dp

    val sweepTargets = remember(data, total) {
        data.map { (it.total / total * 360f).toFloat().coerceAtLeast(0.5f) }
    }
    val animatedSweeps = remember { sweepTargets.map { Animatable(0f) } }
    LaunchedEffect(sweepTargets) {
        sweepTargets.forEachIndexed { index, target ->
            kotlinx.coroutines.delay(index * 100L)
            animatedSweeps[index].animateTo(
                targetValue = target,
                animationSpec = tween(800)
            )
        }
    }

    val totalAnimated by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = "total_anim"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Receitas por Pagamento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    val canvasSize = this.size
                    val d = maxOf(canvasSize.width, canvasSize.height)
                    val topLeft = Offset(
                        (canvasSize.width - d) / 2f,
                        (canvasSize.height - d) / 2f
                    )
                    val arcSize = d
                    val sw = strokeWidth.toPx()
                    val bgStrokeColor = bgPieStrokeColor

                    drawCircle(color = bgStrokeColor, style = Stroke(width = sw))

                    var startAngle = -90f
                    data.forEachIndexed { i, _ ->
                        val sweep = animatedSweeps[i].value * totalAnimated
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
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = nf.format(total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "TOTAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            data.forEachIndexed { i, item ->
                val fraction = (item.total / total).toFloat()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).background(chartColors[i], RoundedCornerShape(2.dp)))
                    Text(item.paymentMethod, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${nf.format(item.total)} MT", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Text("${"%.1f".format(fraction * 100)}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadorLinha(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
