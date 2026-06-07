package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Transaction
import com.example.ui.FinanceViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FinanceViewModel,
    onNavigateToNewTransaction: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val poupancaSum by viewModel.poupancaSum.collectAsState()

    val currentBalance = totalIncome - totalExpenses

    // Format currency to MZN style (e.g. 145.280,00)
    val numberFormat = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // User Profile Circle Placeholder styled to match design
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "FC",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = "Bom dia,",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .testTag("add_transaction_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nova Transação",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // MAIN BALANCE CARD (SALDO TOTAL) - Styled with Clean Minimalism design rules
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SALDO TOTAL",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { viewModel.toggleBalanceVisibility() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isBalanceVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = "Ocultar/Mostrar saldo",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isBalanceVisible) "Ocultar" else "Mostrar",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "MZN",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            if (isBalanceVisible) {
                                Text(
                                    text = numberFormat.format(currentBalance),
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    text = "••••••",
                                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.blur(2.dp)
                                )
                            }
                        }

                        // Real month-over-month trend
                        val trendInfo = remember(transactions) {
                            val now = System.currentTimeMillis()
                            val cal = java.util.Calendar.getInstance()
                            cal.timeInMillis = now
                            cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                            cal.set(java.util.Calendar.MINUTE, 0)
                            cal.set(java.util.Calendar.SECOND, 0)
                            cal.set(java.util.Calendar.MILLISECOND, 0)
                            val thisMonthStart = cal.timeInMillis
                            cal.add(java.util.Calendar.MONTH, -1)
                            val lastMonthStart = cal.timeInMillis

                            val thisTx = transactions.filter { it.date >= thisMonthStart }
                            val lastTx = transactions.filter { it.date in lastMonthStart until thisMonthStart }
                            val thisNet = thisTx.sumOf { if (it.type == "RECEITA") it.amount else -it.amount }
                            val lastNet = lastTx.sumOf { if (it.type == "RECEITA") it.amount else -it.amount }
                            val pct = if (lastNet != 0.0) ((thisNet - lastNet) / kotlin.math.abs(lastNet) * 100) else if (thisNet != 0.0) 100.0 else 0.0
                            Triple(pct, pct >= 0, kotlin.math.abs(pct))
                        }

                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (trendInfo.second) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (trendInfo.second) Color(0xFF15803D) else Color(0xFFB91C1C),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (trendInfo.second) "+${"%.1f".format(trendInfo.third)}% este mês" else "-${"%.1f".format(trendInfo.third)}% este mês",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (trendInfo.second) Color(0xFF166534) else Color(0xFFB91C1C),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Rendimentos reais
                            val incomeGrowth = remember(transactions) {
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
                                if (lastInc != 0.0) ((thisInc - lastInc) / lastInc * 100) else 0.0
                            }
                            val economiaPct = if (totalIncome > 0) (poupancaSum / totalIncome * 100) else 0.0

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.05f))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "RENDIMENTOS",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (incomeGrowth >= 0) "+${"%.1f".format(incomeGrowth)}%" else "${"%.1f".format(incomeGrowth)}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Economia real
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.05f))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "ECONOMIA",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${"%.0f".format(economiaPct)}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SUMMARY GRID (ENTRADAS / SAÍDAS / POUPANÇA)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Entradas
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Entradas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatKValue(totalIncome),
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Saídas
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Saídas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatKValue(totalExpenses),
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Poupança
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Poupança",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatKValue(poupancaSum),
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // FINANCIAL CHART SECTION (Evolução Financeira)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Evolução financeira",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Últimos 30 dias",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Styled Custom Canvas Line Graph
                        val strokeColor = MaterialTheme.colorScheme.primary
                        val gradientFill = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.0f)
                            )
                        )

                        // Dynamic date formatting for x-axis
                        val sdf = java.text.SimpleDateFormat("d MMM", java.util.Locale("pt", "MZ"))
                        val dateToday = sdf.format(java.util.Date(System.currentTimeMillis()))
                        val date15DaysAgo = sdf.format(java.util.Date(System.currentTimeMillis() - 15L * 24 * 3600 * 1000))
                        val date30DaysAgo = sdf.format(java.util.Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000))

                        // Real trend calculations over 30 days
                        val tx30Days = transactions
                            .filter { it.date >= System.currentTimeMillis() - 30L * 24 * 3600 * 1000 }
                            .sortedBy { it.date }

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        ) {
                            val path = Path().apply {
                                val w = size.width
                                val h = size.height

                                if (tx30Days.isEmpty()) {
                                    moveTo(0f, h * 0.5f)
                                    lineTo(w, h * 0.5f)
                                } else {
                                    val startTime = System.currentTimeMillis() - 30L * 24 * 3600 * 1000
                                    val endTime = System.currentTimeMillis()
                                    val timeRange = (endTime - startTime).coerceAtLeast(1).toDouble()

                                    val points = ArrayList<Pair<Long, Double>>()
                                    var bal = currentBalance - tx30Days.sumOf { if (it.type == "RECEITA") it.amount else -it.amount }
                                    points.add(Pair(startTime, bal))

                                    for (tx in tx30Days) {
                                        bal += if (tx.type == "RECEITA") tx.amount else -tx.amount
                                        points.add(Pair(tx.date, bal))
                                    }
                                    points.add(Pair(endTime, currentBalance))

                                    val minBal = points.minOf { it.second }
                                    val maxBal = points.maxOf { it.second }
                                    val balRange = (maxBal - minBal).coerceAtLeast(1.0)

                                    val firstX = ((points[0].first - startTime) / timeRange * w).toFloat()
                                    val firstY = (h - ((points[0].second - minBal) / balRange * (h * 0.8f) + h * 0.1f)).toFloat()
                                    moveTo(firstX, firstY)

                                    for (i in 1 until points.size) {
                                        val pt = points[i]
                                        val x = ((pt.first - startTime) / timeRange * w).toFloat()
                                        val y = (h - ((pt.second - minBal) / balRange * (h * 0.8f) + h * 0.1f)).toFloat()
                                        lineTo(x, y)
                                    }
                                }
                            }

                            // Draw the gradient beneath path
                            val fillPath = Path().apply {
                                addPath(path)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }

                            drawPath(path = fillPath, brush = gradientFill)
                            drawPath(path = path, color = strokeColor, style = Stroke(width = 3.dp.toPx()))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(date30DaysAgo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            Text(date15DaysAgo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            Text(dateToday, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // RECENT TRANSACTIONS HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transações recentes",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToHistory) {
                        Text(
                            text = "Ver tudo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // RENDER RECENT TRANSACTIONS
            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Nenhuma transação ainda.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(transactions.take(5)) { tx ->
                    TransactionItem(tx = tx)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Detail if any */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Category icon selector
                val icon = when (tx.category.lowercase()) {
                    "compras" -> Icons.Default.ShoppingCart
                    "alimentação" -> Icons.Default.Restaurant
                    "transporte" -> Icons.Default.DirectionsCar
                    "utilidades" -> Icons.Default.Bolt
                    "rendimento" -> Icons.Default.Payments
                    else -> Icons.Default.Category
                }

                // Clean Minimalist categorical colors matching Design spec
                val isReceita = tx.type == "RECEITA"
                val (iconBg, iconColor) = if (isReceita) {
                    Color(0xFFDCFCE7) to Color(0xFF15803D) // green-100 to green-700
                } else {
                    when (tx.category.lowercase()) {
                        "compras" -> Color(0xFFFFEDD5) to Color(0xFFC2410C) // orange-100 to orange-700
                        "alimentação" -> Color(0xFFFEE2E2) to Color(0xFFB91C1C) // red-100 to red-700
                        "transporte" -> Color(0xFFFEF9C3) to Color(0xFFA16207) // yellow-100 to yellow-800
                        "utilidades" -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8) // blue-100 to blue-700
                        else -> Color(0xFFF3F4F9) to Color(0xFF535F70) // gray-100 to gray-700
                    }
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tx.category,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tx.description.ifEmpty { tx.category },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                val prefix = if (tx.type == "RECEITA") "+ " else "- "
                val color = if (tx.type == "RECEITA") Color(0xFF15803D) else MaterialTheme.colorScheme.onSurface

                Text(
                    text = prefix + numberFormat.format(tx.amount) + " MT",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Utility to format values like 24.5k to match screenshots exactly
fun formatKValue(amount: Double): String {
    return if (amount >= 1000.0) {
        val valueInK = amount / 1000.0
        String.format(Locale.US, "%.1fk", valueInK)
    } else {
        String.format(Locale.US, "%.0f", amount)
    }
}
