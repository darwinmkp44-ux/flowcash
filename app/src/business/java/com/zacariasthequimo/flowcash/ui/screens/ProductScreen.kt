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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zacariasthequimo.flowcash.data.entity.Product
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(viewModel: BusinessViewModel) {
    val products by viewModel.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val nf = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produtos & Stock", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Produto", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it; viewModel.searchProducts(it) },
                placeholder = { Text("Buscar produtos...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Inventory2, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(Modifier.height(12.dp))
                        Text("Nenhum produto encontrado", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Adicione o primeiro produto", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(products, key = { it.id }) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { selectedProduct = product },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Spacer(Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text("${nf.format(product.price)} MT", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text("Stock: ${product.stockQty}", style = MaterialTheme.typography.bodySmall, color = if (product.stockQty <= 5) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desc, price, cost, stock, cat ->
                viewModel.addProduct(name, desc, price, cost, stock, cat)
                showAddDialog = false
            }
        )
    }

    if (selectedProduct != null) {
        EditProductDialog(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null },
            onUpdate = { updated ->
                viewModel.updateProduct(updated)
                selectedProduct = null
            },
            onDelete = {
                viewModel.deleteProduct(selectedProduct!!)
                selectedProduct = null
            }
        )
    }
}

@Composable
private fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Produto", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), maxLines = 2)
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Preço (MT)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Custo (MT)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock inicial") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoria") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = price.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val c = cost.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val s = stock.toIntOrNull() ?: 0
                    if (name.isNotBlank()) onConfirm(name, description, p, c, s, category)
                },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Adicionar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onUpdate: (Product) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(product.description) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var cost by remember { mutableStateOf(product.cost.toString()) }
    var stock by remember { mutableStateOf(product.stockQty.toString()) }
    var category by remember { mutableStateOf(product.category) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Produto", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), maxLines = 2)
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Preço (MT)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Custo (MT)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoria") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                if (showDeleteConfirm) {
                    Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("Confirmar Exclusão")
                    }
                } else {
                    TextButton(onClick = { showDeleteConfirm = true }) { Text("Eliminar Produto", color = MaterialTheme.colorScheme.error) }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = price.replace(",", ".").toDoubleOrNull() ?: product.price
                    val c = cost.replace(",", ".").toDoubleOrNull() ?: product.cost
                    val s = stock.toIntOrNull() ?: product.stockQty
                    onUpdate(product.copy(name = name, description = description, price = p, cost = c, stockQty = s, category = category))
                },
                shape = RoundedCornerShape(12.dp)
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
