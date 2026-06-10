package com.zacariasthequimo.flowcash.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
                HomeFilterRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}

@Composable
private fun HomeFilterRow(
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

data class TransactionGroup(
    val dateLabel: String,
    val transactions: List<Transaction>
)

fun formatKValue(amount: Double): String {
    return if (amount >= 1000.0) {
        val valueInK = amount / 1000.0
        String.format(Locale.US, "%.1fk", valueInK)
    } else {
        String.format(Locale.US, "%.0f", amount)
    }
}
