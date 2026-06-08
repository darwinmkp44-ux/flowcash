package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacariasthequimo.flowcash.ui.FinanceViewModel
import com.zacariasthequimo.flowcash.ui.UserAvatar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: FinanceViewModel,
    showTopBar: Boolean = true
) {
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val poupancaSum by viewModel.poupancaSum.collectAsState()
    val userName by viewModel.userName.collectAsState()
    var selectedPeriod by remember { mutableStateOf("Mensal") }

    val netBalance = totalIncome - totalExpenses - poupancaSum

    val (incomeChange, expenseChange) = remember(transactions) {
        val now = System.currentTimeMillis()
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val thisStart = cal.timeInMillis
        cal.add(java.util.Calendar.MONTH, -1)
        val lastStart = cal.timeInMillis

        val thisInc = transactions.filter { it.date >= thisStart && it.type == "RECEITA" }.sumOf { it.amount }
        val lastInc = transactions.filter { it.date in lastStart until thisStart && it.type == "RECEITA" }.sumOf { it.amount }
        val thisExp = transactions.filter { it.date >= thisStart && it.type == "DESPESA" }.sumOf { it.amount }
        val lastExp = transactions.filter { it.date in lastStart until thisStart && it.type == "DESPESA" }.sumOf { it.amount }

        val incPct = if (lastInc != 0.0) ((thisInc - lastInc) / lastInc * 100) else 0.0
        val expPct = if (lastExp != 0.0) ((thisExp - lastExp) / lastExp * 100) else 0.0
        Pair(incPct, expPct)
    }

    val numberFormat = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    val content = @Composable {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "ANALYTICS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Estat\u00edsticas",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Mensal", "Anual").forEach { period ->
                            val isSelected = selectedPeriod == period
                            val periodBg = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                            val periodText = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(periodBg)
                                    .clickable { selectedPeriod = period }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                    .testTag("period_tab_$period"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = period,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = periodText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Column {
                                    Text(
                                        "RECEITAS TOTAIS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        "MZN " + numberFormat.format(totalIncome),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Text(
                                if (incomeChange >= 0) "+${"%.1f".format(incomeChange)}%" else "${"%.1f".format(incomeChange)}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (incomeChange >= 0) Color(0xFF15803D) else Color(0xFFB91C1C),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                                Column {
                                    Text(
                                        "DESPESAS TOTAIS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        "MZN " + numberFormat.format(totalExpenses),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Text(
                                if (expenseChange >= 0) "+${"%.1f".format(expenseChange)}%" else "${"%.1f".format(expenseChange)}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (expenseChange <= 0) Color(0xFF15803D) else Color(0xFFB91C1C),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column {
                                    Text(
                                        "SALDO L\u00cdQUIDO",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        "MZN " + numberFormat.format(netBalance),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Entradas vs Sa\u00eddas",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer))
                                    Text("Entradas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer))
                                    Text("Sa\u00eddas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        val calendar = java.util.Calendar.getInstance()
                        val months = ArrayList<String>()
                        val monthlyIncomes = DoubleArray(6)
                        val monthlyExpenses = DoubleArray(6)

                        val brackets = Array(6) { LongRange(0, 0) }
                        for (i in 5 downTo 0) {
                            calendar.timeInMillis = System.currentTimeMillis()
                            calendar.add(java.util.Calendar.MONTH, -i)

                            val name = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.SHORT, java.util.Locale("pt", "MZ")) ?: ""
                            months.add(name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale("pt", "MZ")) else it.toString() })

                            val startCal = java.util.Calendar.getInstance().apply {
                                timeInMillis = calendar.timeInMillis
                                set(java.util.Calendar.DAY_OF_MONTH, 1)
                                set(java.util.Calendar.HOUR_OF_DAY, 0)
                                set(java.util.Calendar.MINUTE, 0)
                                set(java.util.Calendar.SECOND, 0)
                                set(java.util.Calendar.MILLISECOND, 0)
                            }
                            val endCal = java.util.Calendar.getInstance().apply {
                                timeInMillis = calendar.timeInMillis
                                set(java.util.Calendar.DAY_OF_MONTH, getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
                                set(java.util.Calendar.HOUR_OF_DAY, 23)
                                set(java.util.Calendar.MINUTE, 59)
                                set(java.util.Calendar.SECOND, 59)
                                set(java.util.Calendar.MILLISECOND, 999)
                            }
                            brackets[5 - i] = LongRange(startCal.timeInMillis, endCal.timeInMillis)
                        }

                        for (idx in 0..5) {
                            val range = brackets[idx]
                            val txInMonth = transactions.filter { it.date in range }
                            monthlyIncomes[idx] = txInMonth.filter { it.type == "RECEITA" }.sumOf { it.amount }
                            monthlyExpenses[idx] = txInMonth.filter { it.type == "DESPESA" }.sumOf { it.amount }
                        }

                        val maxVal = (monthlyIncomes.maxOrNull() ?: 0.0).coerceAtLeast(monthlyExpenses.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
                        val incomeFactors = monthlyIncomes.map { if (it > 0.0) (it / maxVal).toFloat().coerceIn(0.05f, 1.0f) else 0.0f }
                        val expenseFactors = monthlyExpenses.map { if (it > 0.0) (it / maxVal).toFloat().coerceIn(0.05f, 1.0f) else 0.0f }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            months.forEachIndexed { idx, m ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .fillMaxHeight(incomeFactors[idx])
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .fillMaxHeight(expenseFactors[idx])
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                        )
                                    }

                                    Text(
                                        text = m,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Mix de Gastos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        val expensesList = transactions.filter { it.type == "DESPESA" }
                        val totalExpenseAmount = expensesList.sumOf { it.amount }

                        val categoryAmounts = mapOf(
                            "Compras" to expensesList.filter { it.category.equals("Compras", ignoreCase = true) }.sumOf { it.amount },
                            "Alimenta\u00e7\u00e3o" to expensesList.filter { it.category.equals("Alimenta\u00e7\u00e3o", ignoreCase = true) }.sumOf { it.amount },
                            "Transporte" to expensesList.filter { it.category.equals("Transporte", ignoreCase = true) }.sumOf { it.amount },
                            "Outros" to expensesList.filter { !it.category.equals("Compras", ignoreCase = true) &&
                                                                !it.category.equals("Alimenta\u00e7\u00e3o", ignoreCase = true) &&
                                                                !it.category.equals("Transporte", ignoreCase = true) }.sumOf { it.amount }
                        )

                        val comprasPercent = if (totalExpenseAmount > 0) ((categoryAmounts["Compras"] ?: 0.0) / totalExpenseAmount).toFloat() else 0.0f
                        val alimentacaoPercent = if (totalExpenseAmount > 0) ((categoryAmounts["Alimenta\u00e7\u00e3o"] ?: 0.0) / totalExpenseAmount).toFloat() else 0.0f
                        val transportePercent = if (totalExpenseAmount > 0) ((categoryAmounts["Transporte"] ?: 0.0) / totalExpenseAmount).toFloat() else 0.0f
                        val outrosPercent = if (totalExpenseAmount > 0) ((categoryAmounts["Outros"] ?: 0.0) / totalExpenseAmount).toFloat() else 0.0f

                        val categoriesMix = listOf(
                            Pair("Compras", if (totalExpenseAmount > 0) String.format(java.util.Locale.US, "%.0f%%", comprasPercent * 100) else "0%"),
                            Pair("Alimenta\u00e7\u00e3o", if (totalExpenseAmount > 0) String.format(java.util.Locale.US, "%.0f%%", alimentacaoPercent * 100) else "0%"),
                            Pair("Transporte", if (totalExpenseAmount > 0) String.format(java.util.Locale.US, "%.0f%%", transportePercent * 100) else "0%"),
                            Pair("Outros", if (totalExpenseAmount > 0) String.format(java.util.Locale.US, "%.0f%%", outrosPercent * 100) else "0%")
                        )
                        val legendColors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.error,
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.secondary
                        )

                        Box(
                            modifier = Modifier.size(170.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val color1 = MaterialTheme.colorScheme.primary
                            val color2 = MaterialTheme.colorScheme.error
                            val color3 = MaterialTheme.colorScheme.tertiary
                            val color4 = MaterialTheme.colorScheme.secondary

                            val strokeWidthVal = 14.dp

                            val comprasSweep = comprasPercent * 360f
                            val alimentacaoSweep = alimentacaoPercent * 360f
                            val transporteSweep = transportePercent * 360f
                            val outrosSweep = outrosPercent * 360f

                            fun formatKValue(amount: Double): String {
                                return if (amount >= 1000.0) {
                                    val valueInK = amount / 1000.0
                                    String.format(java.util.Locale.US, "%.1fk", valueInK)
                                } else {
                                    String.format(java.util.Locale.US, "%.0f", amount)
                                }
                            }

                            val bgCircleColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = bgCircleColor,
                                    style = Stroke(width = strokeWidthVal.toPx())
                                )

                                if (totalExpenseAmount > 0) {
                                    drawArc(
                                        color = color1,
                                        startAngle = -90f,
                                        sweepAngle = comprasSweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidthVal.toPx(), cap = StrokeCap.Round)
                                    )

                                    drawArc(
                                        color = color2,
                                        startAngle = -90f + comprasSweep,
                                        sweepAngle = alimentacaoSweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidthVal.toPx(), cap = StrokeCap.Round)
                                    )

                                    drawArc(
                                        color = color3,
                                        startAngle = -90f + comprasSweep + alimentacaoSweep,
                                        sweepAngle = transporteSweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidthVal.toPx(), cap = StrokeCap.Round)
                                    )

                                    drawArc(
                                        color = color4,
                                        startAngle = -90f + comprasSweep + alimentacaoSweep + transporteSweep,
                                        sweepAngle = outrosSweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidthVal.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = formatKValue(totalExpenses),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "TOTAL DESP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categoriesMix.forEachIndexed { index, pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(legendColors[index])
                                        )
                                        Text(
                                            text = pair.first,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = pair.second,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Insights",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    val expInsight = remember(transactions, expenseChange) {
                        if (expenseChange < 0) {
                            "Despesas reduzidas em ${"%.0f".format(kotlin.math.abs(expenseChange))}%"
                        } else if (expenseChange > 0) {
                            "Despesas aumentaram ${"%.0f".format(expenseChange)}%"
                        } else {
                            "Despesas est\u00e1veis este m\u00eas"
                        }
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = expInsight,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (expenseChange < 0) "Continue assim! Est\u00e1 a gerir bem o seu or\u00e7amento." else if (expenseChange > 0) "Reveja os seus gastos para equilibrar as contas." else "Mantenha o foco nos seus objetivos financeiros.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    val topGoal = remember(goals) {
                        goals.maxByOrNull { g -> if (g.targetAmount > 0) g.currentAmount / g.targetAmount else 0.0 }
                    }
                    if (topGoal != null) {
                        val goalPct = if (topGoal.targetAmount > 0) (topGoal.currentAmount / topGoal.targetAmount * 100) else 0.0
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Meta \"${topGoal.title}\" em ${"%.0f".format(goalPct)}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (goalPct >= 100) "Parab\u00e9ns! Meta atingida!" else "Continue contribuindo para atingir este objetivo.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            UserAvatar(
                                photoPath = profilePhotoPath,
                                userName = userName
                            )
                            Text(
                                "Estat\u00edsticas",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifica\u00e7\u00f5es",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    } else {
        content()
    }
}
