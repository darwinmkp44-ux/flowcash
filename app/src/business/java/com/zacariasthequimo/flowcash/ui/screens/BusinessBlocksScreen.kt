package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zacariasthequimo.flowcash.BusinessBlock
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessBlocksScreen(
    viewModel: BusinessViewModel,
    onBlockClick: (BusinessBlock) -> Unit
) {
    val todayRevenue by viewModel.todayRevenue.collectAsState()
    val monthRevenue by viewModel.monthRevenue.collectAsState()
    val pendingDebtAmount by viewModel.pendingDebtAmount.collectAsState()
    val totalCustomers by viewModel.totalCustomers.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.refreshDashboard() }

    val prefs = context.getSharedPreferences("flowcash_modules", android.content.Context.MODE_PRIVATE)
    val disabledModules = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        val disabled = prefs.getString("disabled_modules", "") ?: ""
        disabledModules.clear()
        if (disabled.isNotBlank()) {
            disabledModules.addAll(disabled.split(","))
        }
    }

    val activeBlocks = BusinessBlock.entries.filter { it.name !in disabledModules }

    val nf = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Business",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Receita Hoje", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("${nf.format(todayRevenue)} MT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Este M\u00eas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("${nf.format(monthRevenue)} MT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val chunks = activeBlocks.chunked(2)
            chunks.forEach { rowBlocks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowBlocks.forEach { block ->
                        Box(modifier = Modifier.weight(1f)) {
                            BlockCard(
                                block = block,
                                metric = when (block) {
                                    BusinessBlock.CLIENTES -> "$totalCustomers clientes"
                                    BusinessBlock.VENDAS -> "${sales.size} vendas"
                                    BusinessBlock.DIVIDAS -> "${nf.format(pendingDebtAmount)} MT pendentes"
                                    BusinessBlock.PRODUTOS -> "Gerir stock"
                                    BusinessBlock.AGENDA -> "Compromissos"
                                    BusinessBlock.MODULOS -> "Ativar/desativar"
                                    BusinessBlock.RELATORIOS -> "Lucros e desempenho"
                                },
                                onClick = { onBlockClick(block) }
                            )
                        }
                    }
                    if (rowBlocks.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fluxo Financeiro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                val dailyRevenue = remember(sales) {
                    val cal = java.util.Calendar.getInstance()
                    val today = java.util.Calendar.getInstance()
                    val bars = mutableListOf<Double>()
                    for (i in 6 downTo 0) {
                        cal.timeInMillis = today.timeInMillis
                        cal.add(java.util.Calendar.DAY_OF_MONTH, -i)
                        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                        cal.set(java.util.Calendar.MINUTE, 0)
                        cal.set(java.util.Calendar.SECOND, 0)
                        cal.set(java.util.Calendar.MILLISECOND, 0)
                        val dayStart = cal.timeInMillis
                        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
                        val dayEnd = cal.timeInMillis
                        val rev = sales.filter { it.date in dayStart until dayEnd }.sumOf { it.total }
                        bars.add(rev)
                    }
                    bars
                }

                val maxVal = dailyRevenue.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
                val barColor = MaterialTheme.colorScheme.primary
                val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val barCount = dailyRevenue.size
                    val gap = 6f
                    val barW = (w - gap * (barCount - 1)) / barCount * 0.65f

                    dailyRevenue.forEachIndexed { index, value ->
                        val x = index * (barW + gap) + (w - (barCount * (barW + gap) - gap)) / 2f
                        val barH = (value / maxVal * h * 0.8f).toFloat().coerceAtLeast(0f)
                        val baseY = h * 0.9f

                        drawRect(
                            color = barColor,
                            topLeft = Offset(x, baseY - barH),
                            size = Size(barW, barH),
                            alpha = 0.7f
                        )
                    }

                    drawLine(
                        color = gridColor,
                        start = Offset(0f, h * 0.9f),
                        end = Offset(w, h * 0.9f),
                        strokeWidth = 1f
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Receita hoje: ${nf.format(todayRevenue)} MT",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "M\u00eas: ${nf.format(monthRevenue)} MT",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun BlockCard(
    block: BusinessBlock,
    metric: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = block.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = block.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = metric,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
