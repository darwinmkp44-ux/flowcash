package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
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
import com.zacariasthequimo.flowcash.ui.UserAvatar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: FinanceViewModel,
    showTopBar: Boolean = true
) {
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("M\u00eas") }

    val filteredTransactions = remember(transactions, searchQuery, selectedFilterTab) {
        transactions.filter { tx ->
            val matchesSearch = tx.title.lowercase().contains(searchQuery.lowercase()) ||
                                tx.description.lowercase().contains(searchQuery.lowercase()) ||
                                tx.category.lowercase().contains(searchQuery.lowercase())

            val now = System.currentTimeMillis()
            val dayMills = 24 * 3600 * 1000L
            val matchesTab = when (selectedFilterTab) {
                "Hoje" -> now - tx.date <= dayMills
                "Semana" -> now - tx.date <= 7 * dayMills
                "M\u00eas" -> now - tx.date <= 30 * dayMills
                else -> true
            }

            matchesSearch && matchesTab
        }
    }

    val iosInputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    val content = @Composable {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Buscar transa\u00e7\u00f5es...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outlineVariant)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpar busca")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                shape = RoundedCornerShape(10.dp),
                colors = iosInputColors
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Hoje", "Semana", "M\u00eas").forEach { tab ->
                    val isSelected = selectedFilterTab == tab
                    val tabBgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                    val tabTextColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFilterTab = tab }
                            .testTag("filter_tab_$tab"),
                        colors = CardDefaults.cardColors(containerColor = tabBgColor),
                        shape = RoundedCornerShape(50)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelLarge,
                                color = tabTextColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Nenhuma transa\u00e7\u00e3o encontrada",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tente alterar os termos de busca ou filtros.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredTransactions, key = { it.id }) { tx ->
                        HistoryTransactionItem(
                            tx = tx,
                            onDelete = { viewModel.deleteTransaction(tx) }
                        )
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
                                "Hist\u00f3rico",
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
            Box(modifier = Modifier.padding(innerPadding).padding(top = 8.dp)) {
                content()
            }
        }
    } else {
        content()
    }
}

@Composable
fun HistoryTransactionItem(
    tx: Transaction,
    onDelete: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale("pt", "MZ")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
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
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f) to MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) to MaterialTheme.colorScheme.primary
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
                        modifier = Modifier.size(20.dp)
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
                        text = dateFormatter.format(Date(tx.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val prefix = if (tx.type == "RECEITA") "+ " else "- "
                val color = if (tx.type == "RECEITA") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface

                Text(
                    text = prefix + numberFormat.format(tx.amount) + " MT",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_tx_button_${tx.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Deletar Transa\u00e7\u00e3o",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
