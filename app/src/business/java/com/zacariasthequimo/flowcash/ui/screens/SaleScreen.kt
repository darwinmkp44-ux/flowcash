package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacariasthequimo.flowcash.data.entity.Product
import com.zacariasthequimo.flowcash.data.entity.Sale
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SaleItem(val productId: Long, val name: String, val quantity: Int, val unitPrice: Double, val total: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(
    viewModel: BusinessViewModel,
    onNewSale: () -> Unit
) {
    val sales by viewModel.sales.collectAsState()
    var showNewSale by remember { mutableStateOf(false) }

    val nf = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }
    val df = SimpleDateFormat("dd/MM/yy HH:mm", Locale("pt", "MZ"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendas", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewSale = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.AddShoppingCart, contentDescription = "Nova Venda", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (showNewSale) {
            NewSaleSheet(
                viewModel = viewModel,
                onDismiss = { showNewSale = false }
            )
        } else if (sales.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(12.dp))
                    Text("Nenhuma venda registada", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Registe a primeira venda", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("Histórico de Vendas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp, top = 8.dp))
                }
                items(sales, key = { it.id }) { sale ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(sale.customerName.ifEmpty { "Cliente Avulso" }, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                Text("${nf.format(sale.total)} MT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(df.format(Date(sale.date)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(sale.paymentMethod, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewSaleSheet(
    viewModel: BusinessViewModel,
    onDismiss: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val customers by viewModel.customers.collectAsState()

    var step by remember { mutableStateOf(0) }
    var selectedCustomerId by remember { mutableStateOf<Long?>(null) }
    var customerName by remember { mutableStateOf("") }
    var selectedItems by remember { mutableStateOf<List<SaleItem>>(emptyList()) }
    var showProductPicker by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("Dinheiro") }
    var discount by remember { mutableStateOf("") }

    val subtotal = selectedItems.sumOf { it.total }
    val discountVal = discount.replace(",", ".").toDoubleOrNull() ?: 0.0
    val total = subtotal - discountVal

    val nf = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDismiss) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar") }
            Text("Nova Venda", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Cliente", "Produtos", "Pagamento").forEachIndexed { i, label ->
                FilterChip(
                    selected = step == i,
                    onClick = { step = i },
                    label = { Text(label, fontSize = 10.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (step) {
                0 -> {
                    Column {
                        CustomerSelector(
                            customers = customers,
                            selectedCustomerId = selectedCustomerId,
                            customerName = customerName,
                            onSelect = { id, name ->
                                selectedCustomerId = id
                                customerName = name
                            }
                        )
                    }
                }
                1 -> {
                    Column {
                        OutlinedCard(
                            onClick = { showProductPicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text("Adicionar Produtos", fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        if (selectedItems.isEmpty()) {
                            Text("Nenhum produto selecionado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(selectedItems, key = { "${it.productId}_${it.name}" }) { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.name, fontWeight = FontWeight.SemiBold)
                                                Text("${item.quantity}x ${nf.format(item.unitPrice)} MT", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text("${nf.format(item.total)} MT", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Método de Pagamento", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Dinheiro", "Cartão", "Transferência", "Outro").forEach { method ->
                                FilterChip(
                                    selected = paymentMethod == method,
                                    onClick = { paymentMethod = method },
                                    label = { Text(method, fontSize = 11.sp) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = discount,
                            onValueChange = { discount = it },
                            label = { Text("Desconto (MT)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text("${nf.format(subtotal)} MT", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
                if (discountVal > 0) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Desconto", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                        Text("-${nf.format(discountVal)} MT", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${nf.format(total)} MT", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(8.dp))
                if (step < 2) {
                    Button(
                        onClick = { step++ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Próximo", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (selectedItems.isNotEmpty()) {
                                val itemsJson = selectedItems.joinToString("|") { "${it.productId}:${it.name}:${it.quantity}:${it.unitPrice}:${it.total}" }
                                viewModel.addSale(
                                    customerId = selectedCustomerId,
                                    customerName = customerName,
                                    itemsJson = itemsJson,
                                    subtotal = subtotal,
                                    discount = discountVal,
                                    total = total,
                                    paymentMethod = paymentMethod
                                )
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        enabled = selectedItems.isNotEmpty(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Finalizar Venda", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showProductPicker) {
        ProductPickerDialog(
            products = products,
            onDismiss = { showProductPicker = false },
            onSelect = { product, qty ->
                val existing = selectedItems.indexOfFirst { it.productId == product.id }
                val newItems = if (existing >= 0) {
                    selectedItems.toMutableList().apply {
                        val old = this[existing]
                        this[existing] = old.copy(quantity = old.quantity + qty, total = (old.quantity + qty) * old.unitPrice)
                    }
                } else {
                    selectedItems + SaleItem(product.id, product.name, qty, product.price, qty * product.price)
                }
                selectedItems = newItems
                showProductPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerSelector(
    customers: List<com.zacariasthequimo.flowcash.data.entity.Customer>,
    selectedCustomerId: Long?,
    customerName: String,
    onSelect: (Long?, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = if (selectedCustomerId != null) customerName else "Cliente Avulso",
            onValueChange = {},
            readOnly = true,
            label = { Text("Cliente") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Cliente Avulso") },
                onClick = { onSelect(null, ""); expanded = false }
            )
            customers.forEach { c ->
                DropdownMenuItem(
                    text = { Text(c.name) },
                    onClick = { onSelect(c.id, c.name); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun ProductPickerDialog(
    products: List<Product>,
    onDismiss: () -> Unit,
    onSelect: (Product, Int) -> Unit
) {
    var qty by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar Produto", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Quantidade") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(products) { product ->
                        TextButton(
                            onClick = { onSelect(product, qty.toIntOrNull() ?: 1) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(product.name, fontWeight = FontWeight.SemiBold)
                                Text("${product.price} MT", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
