package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zacariasthequimo.flowcash.data.entity.Debt
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(viewModel: BusinessViewModel) {
    val debts by viewModel.debts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDebt by remember { mutableStateOf<Debt?>(null) }
    var filterPaid by remember { mutableStateOf(false) }

    val nf = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }
    val df = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "MZ"))

    val filteredDebts = if (filterPaid) debts.filter { it.paid } else debts.filter { !it.paid }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dívidas", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nova Dívida", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(selected = !filterPaid, onClick = { filterPaid = false }, label = { Text("Pendentes") })
                FilterChip(selected = filterPaid, onClick = { filterPaid = true }, label = { Text("Pagas") })
            }

            if (filteredDebts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.MonetizationOn, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(Modifier.height(12.dp))
                        Text(if (filterPaid) "Nenhuma dívida paga" else "Nenhuma dívida pendente", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredDebts, key = { it.id }) { debt ->
                        DebtCard(
                            debt = debt,
                            nf = nf,
                            df = df,
                            onClick = { selectedDebt = debt }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDebtDialog(
            customers = viewModel.customers.collectAsState().value,
            onDismiss = { showAddDialog = false },
            onConfirm = { customerId, customerName, amount, desc, dueDate ->
                viewModel.addDebt(customerId, customerName, amount, desc, dueDate)
                showAddDialog = false
            }
        )
    }

    if (selectedDebt != null) {
        DebtActionsDialog(
            debt = selectedDebt!!,
            nf = nf,
            onDismiss = { selectedDebt = null },
            onPay = { amount ->
                viewModel.payDebt(selectedDebt!!, amount)
                selectedDebt = null
            },
            onDelete = {
                viewModel.deleteDebt(selectedDebt!!)
                selectedDebt = null
            }
        )
    }
}

@Composable
private fun DebtCard(
    debt: Debt,
    nf: NumberFormat,
    df: SimpleDateFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (debt.paid) MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = if (debt.paid) BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(debt.customerName.ifEmpty { "Cliente" }, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    if (debt.paid) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(16.dp))
                    }
                }
                if (debt.description.isNotBlank()) {
                    Text(debt.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text("Vencimento: ${df.format(Date(debt.dueDate))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${nf.format(debt.amount)} MT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (debt.paid) Color(0xFF22C55E) else MaterialTheme.colorScheme.error)
                if (debt.paidAmount > 0 && !debt.paid) {
                    Text("Pago: ${nf.format(debt.paidAmount)} MT", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDebtDialog(
    customers: List<com.zacariasthequimo.flowcash.data.entity.Customer>,
    onDismiss: () -> Unit,
    onConfirm: (Long?, String, Double, String, Long) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf<Long?>(null) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var customerExpanded by remember { mutableStateOf(false) }

    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.DAY_OF_MONTH, 30)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Dívida", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(expanded = customerExpanded, onExpandedChange = { customerExpanded = it }) {
                    OutlinedTextField(
                        value = customerName.ifEmpty { "Cliente Avulso" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cliente") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(expanded = customerExpanded, onDismissRequest = { customerExpanded = false }) {
                        DropdownMenuItem(text = { Text("Cliente Avulso") }, onClick = { customerId = null; customerName = ""; customerExpanded = false })
                        customers.forEach { c ->
                            DropdownMenuItem(text = { Text(c.name) }, onClick = { customerId = c.id; customerName = c.name; customerExpanded = false })
                        }
                    }
                }
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Valor (MT) *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), maxLines = 2)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.replace(",", ".").toDoubleOrNull() ?: return@Button
                    onConfirm(customerId, customerName, amt, description, cal.timeInMillis)
                },
                enabled = amount.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Adicionar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun DebtActionsDialog(
    debt: Debt,
    nf: NumberFormat,
    onDismiss: () -> Unit,
    onPay: (Double) -> Unit,
    onDelete: () -> Unit
) {
    var payAmount by remember { mutableStateOf(nf.format(debt.remaining).replace(".", "").replace(",", ".")) }
    var showPayInput by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(debt.customerName.ifEmpty { "Dívida" }, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Valor total: ${nf.format(debt.amount)} MT", fontWeight = FontWeight.SemiBold)
                if (debt.paidAmount > 0) Text("Já pago: ${nf.format(debt.paidAmount)} MT", color = Color(0xFF22C55E))
                Text("Restante: ${nf.format(debt.remaining)} MT", color = if (debt.paid) Color(0xFF22C55E) else MaterialTheme.colorScheme.error)
                if (!debt.paid && showPayInput) {
                    OutlinedTextField(
                        value = payAmount,
                        onValueChange = { payAmount = it },
                        label = { Text("Valor a pagar") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                if (showDeleteConfirm) {
                    Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("Confirmar Exclusão")
                    }
                }
            }
        },
        confirmButton = {
            if (!debt.paid) {
                if (showPayInput) {
                    Button(
                        onClick = {
                            val amt = payAmount.replace(",", ".").toDoubleOrNull() ?: debt.remaining
                            onPay(amt)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Registar Pagamento") }
                } else {
                    TextButton(onClick = { showPayInput = true }) {
                        Text("Registar Pagamento", color = Color(0xFF22C55E))
                    }
                }
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!showDeleteConfirm) {
                    TextButton(onClick = { showDeleteConfirm = true }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                }
                TextButton(onClick = onDismiss) { Text("Fechar") }
            }
        }
    )
}
