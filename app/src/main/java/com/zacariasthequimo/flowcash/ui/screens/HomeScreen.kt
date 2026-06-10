package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacariasthequimo.flowcash.data.entity.Transaction
import com.zacariasthequimo.flowcash.ui.FinanceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FinanceViewModel,
    onNavigateToHistory: () -> Unit,
    showTopBar: Boolean = true
) {
    val userName by viewModel.userName.collectAsState()
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()
    val allTransactions by viewModel.transactions.collectAsState()
    val totalIncomeAll by viewModel.totalIncome.collectAsState()
    val totalExpensesAll by viewModel.totalExpenses.collectAsState()
    val poupancaSum by viewModel.poupancaSum.collectAsState()

    var selectedFilter by remember { mutableStateOf(0) }
    var showTransactionDialog by remember { mutableStateOf(false) }

    val now = System.currentTimeMillis()
    val filteredTransactions = remember(allTransactions, selectedFilter) {
        when (selectedFilter) {
            1 -> {
                val cutoff = now - 7L * 24 * 60 * 60 * 1000
                allTransactions.filter { it.date >= cutoff }
            }
            2 -> {
                val cutoff = now - 15L * 24 * 60 * 60 * 1000
                allTransactions.filter { it.date >= cutoff }
            }
            3 -> {
                val cutoff = now - 30L * 24 * 60 * 60 * 1000
                allTransactions.filter { it.date >= cutoff }
            }
            else -> allTransactions
        }
    }

    val totalIncome = filteredTransactions.filter { it.type == "RECEITA" }.sumOf { it.amount }
    val totalExpenses = filteredTransactions.filter { it.type == "DESPESA" }.sumOf { it.amount }
    val balance = totalIncome - totalExpenses

    val numberFormat = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    val content = @Composable {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SALDO TOTAL",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                            TextButton(
                                onClick = { viewModel.toggleBalanceVisibility() },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isBalanceVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = "Ocultar/Mostrar saldo",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isBalanceVisible) "Ocultar" else "Mostrar",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val currentBalanceAll = totalIncomeAll - totalExpensesAll - poupancaSum
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "MZN",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            if (isBalanceVisible) {
                                Text(
                                    text = numberFormat.format(currentBalanceAll),
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    text = "\u2022\u2022\u2022\u2022\u2022\u2022",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        val trendInfo = remember(allTransactions) {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = now
                            cal.set(Calendar.DAY_OF_MONTH, 1)
                            cal.set(Calendar.HOUR_OF_DAY, 0)
                            cal.set(Calendar.MINUTE, 0)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            val thisMonthStart = cal.timeInMillis
                            cal.add(Calendar.MONTH, -1)
                            val lastMonthStart = cal.timeInMillis

                            val thisTx = allTransactions.filter { it.date >= thisMonthStart }
                            val lastTx = allTransactions.filter { it.date in lastMonthStart until thisMonthStart }
                            val thisNet = thisTx.sumOf { if (it.type == "RECEITA") it.amount else -it.amount }
                            val lastNet = lastTx.sumOf { if (it.type == "RECEITA") it.amount else -it.amount }
                            val pct = if (lastNet != 0.0) ((thisNet - lastNet) / kotlin.math.abs(lastNet) * 100) else if (thisNet != 0.0) 100.0 else 0.0
                            Triple(pct, pct >= 0, kotlin.math.abs(pct))
                        }

                        Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (trendInfo.second) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (trendInfo.second) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (trendInfo.second) "+${"%.1f".format(trendInfo.third)}% este m\u00eas" else "-${"%.1f".format(trendInfo.third)}% este m\u00eas",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (trendInfo.second) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val incomeGrowth = remember(allTransactions) {
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = now
                                cal.set(Calendar.DAY_OF_MONTH, 1)
                                cal.set(Calendar.HOUR_OF_DAY, 0)
                                cal.set(Calendar.MINUTE, 0)
                                cal.set(Calendar.SECOND, 0)
                                cal.set(Calendar.MILLISECOND, 0)
                                val thisStart = cal.timeInMillis
                                cal.add(Calendar.MONTH, -1)
                                val lastStart = cal.timeInMillis
                                val thisInc = allTransactions.filter { it.date >= thisStart && it.type == "RECEITA" }.sumOf { it.amount }
                                val lastInc = allTransactions.filter { it.date in lastStart until thisStart && it.type == "RECEITA" }.sumOf { it.amount }
                                if (lastInc != 0.0) ((thisInc - lastInc) / lastInc * 100) else 0.0
                            }
                            val economiaPct = if (totalIncomeAll > 0) (poupancaSum / totalIncomeAll * 100) else 0.0

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "RENDIMENTOS",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (incomeGrowth >= 0) "+${"%.1f".format(incomeGrowth)}%" else "${"%.1f".format(incomeGrowth)}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "ECONOMIA",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${"%.0f".format(economiaPct)}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                SummaryFilterRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Saldo",
                        value = formatKValue(balance),
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.AccountBalanceWallet
                    )
                    SmallMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Entradas",
                        value = formatKValue(totalIncome),
                        color = Color(0xFF15803D),
                        icon = Icons.Default.TrendingUp
                    )
                    SmallMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Gastos",
                        value = formatKValue(totalExpenses),
                        color = MaterialTheme.colorScheme.error,
                        icon = Icons.Default.TrendingDown
                    )
                }
            }

            item {
                IncomeExpenseChart(
                    transactions = filteredTransactions,
                    filter = selectedFilter
                )
            }

            item {
                ExpenseCategoryChart(
                    transactions = filteredTransactions,
                    numberFormat = numberFormat
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredTransactions.size} transa\u00e7\u00f5es",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = onNavigateToHistory) {
                        Text(
                            text = "Ver tudo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Nenhuma transa\u00e7\u00e3o ainda.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                val sdf = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("pt", "MZ"))
                val cal = Calendar.getInstance()
                val groups = filteredTransactions.groupBy { tx ->
                    cal.timeInMillis = tx.date
                    cal.get(Calendar.YEAR) to cal.get(Calendar.DAY_OF_YEAR)
                }.map { (_, txs) ->
                    TransactionGroup(
                        dateLabel = sdf.format(Date(txs.first().date)),
                        transactions = txs
                    )
                }.sortedByDescending { it.transactions.first().date }

                groups.forEach { group ->
                    item {
                        Text(
                            text = group.dateLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(group.transactions) { tx ->
                        TransactionItem(tx = tx)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            com.zacariasthequimo.flowcash.ui.UserAvatar(
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
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTransactionDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .testTag("add_transaction_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nova Transa\u00e7\u00e3o",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }

    if (showTransactionDialog) {
        TransactionDialog(
            viewModel = viewModel,
            onDismiss = { showTransactionDialog = false }
        )
    }
}

data class TransactionGroup(
    val dateLabel: String,
    val transactions: List<Transaction>
)

@Composable
private fun SummaryFilterRow(
    selectedFilter: Int,
    onFilterSelected: (Int) -> Unit
) {
    val filters = listOf("Tudo", "7d", "15d", "30d")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        filters.forEachIndexed { index, label ->
            val isSelected = selectedFilter == index

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable { onFilterSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun SmallMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun IncomeExpenseChart(
    transactions: List<Transaction>,
    filter: Int
) {
    val cal = Calendar.getInstance()
    val now = System.currentTimeMillis()

    val dailyData = remember(transactions, filter) {
        val days = when (filter) {
            1 -> 7
            2 -> 15
            3 -> 30
            else -> 7
        }
        val bars = mutableListOf<Triple<String, Double, Double>>()
        val sdf = SimpleDateFormat("dd/MM", Locale("pt", "MZ"))
        for (i in days - 1 downTo 0) {
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_MONTH, -i)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val dayStart = cal.timeInMillis
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val dayEnd = cal.timeInMillis
            val inc = transactions.filter { it.date in dayStart until dayEnd && it.type == "RECEITA" }.sumOf { it.amount }
            val exp = transactions.filter { it.date in dayStart until dayEnd && it.type == "DESPESA" }.sumOf { it.amount }
            bars.add(Triple(sdf.format(Date(dayStart)), inc, exp))
        }
        bars
    }

    val maxVal = dailyData.maxOfOrNull { maxOf(it.second, it.third) }?.coerceAtLeast(1.0) ?: 1.0

    val animatedIncFactors = dailyData.mapIndexed { index, (_, inc, _) ->
        val anim by animateFloatAsState(
            targetValue = (inc / maxVal).toFloat().coerceIn(0f, 1f),
            animationSpec = tween(600, delayMillis = index * 50),
            label = "incBar_$index"
        )
        anim
    }
    val animatedExpFactors = dailyData.mapIndexed { index, (_, _, exp) ->
        val anim by animateFloatAsState(
            targetValue = (exp / maxVal).toFloat().coerceIn(0f, 1f),
            animationSpec = tween(600, delayMillis = index * 50),
            label = "expBar_$index"
        )
        anim
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Entradas vs Gastos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            val incomeColor = Color(0xFF15803D)
            val expenseColor = MaterialTheme.colorScheme.error

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val w = size.width
                val h = size.height
                val barCount = dailyData.size
                val totalGap = (barCount - 1) * 6f
                val barWidth = ((w - totalGap) / barCount) * 0.65f
                val halfBar = barWidth / 2f
                val chartTop = h * 0.1f
                val chartHeight = h * 0.8f

                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(0f, h * 0.9f),
                    end = Offset(w, h * 0.9f),
                    strokeWidth = 1f
                )

                dailyData.forEachIndexed { index, _ ->
                    val x = index * (barWidth + 6f)
                    val incHeight = animatedIncFactors[index] * chartHeight
                    val expHeight = animatedExpFactors[index] * chartHeight
                    val baseY = h * 0.9f

                    if (incHeight > 0f) {
                        drawRect(
                            color = incomeColor,
                            topLeft = Offset(x, baseY - incHeight),
                            size = Size(halfBar, incHeight)
                        )
                    }
                    if (expHeight > 0f) {
                        drawRect(
                            color = expenseColor,
                            topLeft = Offset(x + halfBar, baseY - expHeight),
                            size = Size(halfBar, expHeight)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(incomeColor))
                        Text("Entradas", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(expenseColor))
                        Text("Gastos", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                val periodLabel = when (filter) {
                    1 -> "\u00daltimos 7 dias"
                    2 -> "\u00daltimos 15 dias"
                    3 -> "\u00daltimos 30 dias"
                    else -> "\u00daltimos 7 dias"
                }
                Text(periodLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun ExpenseCategoryChart(
    transactions: List<Transaction>,
    numberFormat: NumberFormat
) {
    val expenses = transactions.filter { it.type == "DESPESA" }
    val totalExpenseAmount = expenses.sumOf { it.amount }

    val categoryData = remember(expenses) {
        val cats = linkedMapOf(
            "Compras" to 0.0,
            "Alimenta\u00e7\u00e3o" to 0.0,
            "Transporte" to 0.0,
            "Outros" to 0.0
        )
        expenses.forEach { tx ->
            val key = if (cats.containsKey(tx.category)) tx.category else "Outros"
            cats[key] = (cats[key] ?: 0.0) + tx.amount
        }
        cats.filter { it.value > 0.0 }
    }

    val categoryColors = listOf(
        Color(0xFF15803D),
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Gastos por Categoria",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (totalExpenseAmount <= 0) {
                Text(
                    text = "Nenhum gasto registrado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                val categoryList = categoryData.toList()

                val animatedSweeps = categoryList.mapIndexed { index, (_, amount) ->
                    val sweep by animateFloatAsState(
                        targetValue = (amount / totalExpenseAmount * 360f).toFloat().coerceAtLeast(0.5f),
                        animationSpec = tween(800, delayMillis = index * 100),
                        label = "pieSweep_$index"
                    )
                    sweep
                }

                val strokeWidthPx = 22.dp
                val bgCircleColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        val canvasSize = this.size
                        val d = minOf(canvasSize.width, canvasSize.height)
                        val topLeft = Offset(
                            (canvasSize.width - d) / 2f,
                            (canvasSize.height - d) / 2f
                        )
                        val arcSize = d
                        val sw = strokeWidthPx.toPx()

                        drawCircle(
                            color = bgCircleColor,
                            style = Stroke(width = sw)
                        )

                        var startAngle = -90f
                        categoryList.forEachIndexed { i, _ ->
                            val sweep = animatedSweeps[i]
                            drawArc(
                                color = categoryColors[i % categoryColors.size],
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
                            text = formatKValue(totalExpenseAmount),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "TOTAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                categoryList.forEachIndexed { index, (category, amount) ->
                    val fraction = (amount / totalExpenseAmount).toFloat()
                    val color = categoryColors[index % categoryColors.size]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${"%.1f".format(fraction * 100)}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val animatedProgress by animateFloatAsState(
                                targetValue = fraction,
                                animationSpec = tween(600, delayMillis = index * 100),
                                label = "progress_$index"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animatedProgress)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(color)
                                )
                            }
                        }

                        Text(
                            text = numberFormat.format(amount) + " MT",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
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
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                val icon = when (tx.category.lowercase()) {
                    "compras" -> Icons.Default.ShoppingCart
                    "alimenta\u00e7\u00e3o" -> Icons.Default.Restaurant
                    "transporte" -> Icons.Default.DirectionsCar
                    "utilidades" -> Icons.Default.Bolt
                    "rendimento" -> Icons.Default.Payments
                    "sal\u00e1rio" -> Icons.Default.AccountBalance
                    "freelance" -> Icons.Default.Computer
                    "investimentos" -> Icons.Default.TrendingUp
                    "presente" -> Icons.Default.CardGiftcard
                    else -> Icons.Default.Category
                }

                val isReceita = tx.type == "RECEITA"
                val (iconBg, iconColor) = if (isReceita) {
                    Color(0xFF15803D).copy(alpha = 0.12f) to Color(0xFF15803D)
                } else {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) to MaterialTheme.colorScheme.error
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tx.category,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tx.description.ifEmpty { tx.category },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun formatKValue(amount: Double): String {
    return if (amount >= 1000.0) {
        val valueInK = amount / 1000.0
        String.format(Locale.US, "%.1fk", valueInK)
    } else {
        String.format(Locale.US, "%.0f", amount)
    }
}
