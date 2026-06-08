package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacariasthequimo.flowcash.ui.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTransactionScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("DESPESA") }
    var selectedCategory by remember { mutableStateOf("Compras") }
    var description by remember { mutableStateOf("") }

    val expenseCategories = listOf("Compras", "Alimenta\u00e7\u00e3o", "Transporte", "Outros")
    val incomeCategories = listOf("Sal\u00e1rio", "Freelance", "Investimentos", "Presente", "Outros")
    val categories = if (type == "DESPESA") expenseCategories else incomeCategories

    LaunchedEffect(type) {
        selectedCategory = categories.first()
    }

    val iosInputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nova Transa\u00e7\u00e3o",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "VALOR DA TRANSA\u00c7\u00c3O",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "MZN",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    TextField(
                        value = amountStr,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                amountStr = input
                            }
                        },
                        placeholder = {
                            Text(
                                "0,00",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                textAlign = TextAlign.Center
                            )
                        },
                        textStyle = MaterialTheme.typography.displayLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .width(200.dp)
                            .testTag("amount_input")
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val despesaBg by animateColorAsState(
                    targetValue = if (type == "DESPESA") MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    label = "despesa"
                )
                val despesaTextColor by animateColorAsState(
                    targetValue = if (type == "DESPESA") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "despesaText"
                )

                Button(
                    onClick = { type = "DESPESA" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = despesaBg
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("type_despesa"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (type == "DESPESA") MaterialTheme.colorScheme.error else despesaTextColor
                        )
                        Text(
                            text = "DESPESA",
                            style = MaterialTheme.typography.labelLarge,
                            color = despesaTextColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                val receitaBg by animateColorAsState(
                    targetValue = if (type == "RECEITA") MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    label = "receita"
                )
                val receitaTextColor by animateColorAsState(
                    targetValue = if (type == "RECEITA") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "receitaText"
                )

                Button(
                    onClick = { type = "RECEITA" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = receitaBg
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("type_receita"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = if (type == "RECEITA") MaterialTheme.colorScheme.secondary else receitaTextColor
                        )
                        Text(
                            text = "RECEITA",
                            style = MaterialTheme.typography.labelLarge,
                            color = receitaTextColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "CATEGORIA",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val catBgColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                            label = "catBg"
                        )

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedCategory = cat
                                }
                                .testTag("cat_card_$cat"),
                            colors = CardDefaults.cardColors(containerColor = catBgColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 4.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val catIcon = when (cat) {
                                    "Compras" -> Icons.Default.ShoppingCart
                                    "Alimenta\u00e7\u00e3o" -> Icons.Default.Restaurant
                                    "Transporte" -> Icons.Default.DirectionsCar
                                    "Sal\u00e1rio" -> Icons.Default.AccountBalance
                                    "Freelance" -> Icons.Default.Computer
                                    "Investimentos" -> Icons.Default.TrendingUp
                                    "Presente" -> Icons.Default.CardGiftcard
                                    else -> Icons.Default.MoreHoriz
                                }
                                val iconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                Icon(
                                    imageVector = catIcon,
                                    contentDescription = cat,
                                    tint = iconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "T\u00cdTULO DA TRANSA\u00c7\u00c3O",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Ex: Supermercado, Aluguel, EDM", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outlineVariant)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = iosInputColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "DESCRI\u00c7\u00c3O",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Ex: Mercado mensal ou Jantar com amigos", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outlineVariant)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("description_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = iosInputColors
                )
            }

            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && title.isNotEmpty()) {
                        viewModel.addTransaction(
                            title = title,
                            category = selectedCategory,
                            type = type,
                            amount = amount,
                            description = description
                        )
                        onNavigateBack()
                    }
                },
                enabled = title.isNotEmpty() && (amountStr.toDoubleOrNull() ?: 0.0) > 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_transaction_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Salvar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
