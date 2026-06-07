package com.zacariasthequimo.flowcash.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.zacariasthequimo.flowcash.data.entity.Transaction
import com.zacariasthequimo.flowcash.ui.FinanceViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val context = LocalContext.current
    var exportState by remember { mutableStateOf("") } // "", "exporting", "done", "error"
    var exportMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Exportar Dados",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                "Exporte os seus dados para CSV. Os arquivos são gerados localmente e você pode compartilhá-los.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (exportState == "done") {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            exportMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (exportState == "error") {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        exportMessage,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Export Transactions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                "Transações",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${transactions.size} registos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = {
                            exportState = "exporting"
                            try {
                                val file = exportTransactionsToCsv(context, transactions)
                                shareFile(context, file)
                                exportState = "done"
                                exportMessage = "Transações exportadas com sucesso!"
                            } catch (e: Exception) {
                                exportState = "error"
                                exportMessage = "Erro: ${e.message}"
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        enabled = transactions.isNotEmpty() && exportState != "exporting"
                    ) {
                        Text("Exportar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Export Goals
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Filled.InsertChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                "Metas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${goals.size} metas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = {
                            exportState = "exporting"
                            try {
                                val file = exportGoalsToCsv(context, goals)
                                shareFile(context, file)
                                exportState = "done"
                                exportMessage = "Metas exportadas com sucesso!"
                            } catch (e: Exception) {
                                exportState = "error"
                                exportMessage = "Erro: ${e.message}"
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        enabled = goals.isNotEmpty() && exportState != "exporting"
                    ) {
                        Text("Exportar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Export All
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                "Exportar Tudo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Transações + Metas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = {
                            exportState = "exporting"
                            try {
                                val txFile = exportTransactionsToCsv(context, transactions)
                                val goalFile = exportGoalsToCsv(context, goals)
                                exportState = "done"
                                exportMessage = "Arquivos gerados: ${txFile.name}, ${goalFile.name}"
                            } catch (e: Exception) {
                                exportState = "error"
                                exportMessage = "Erro: ${e.message}"
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        enabled = (transactions.isNotEmpty() || goals.isNotEmpty()) && exportState != "exporting"
                    ) {
                        Text("Exportar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                "Os dados nunca saem do seu dispositivo sem a sua autorização.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

private fun exportTransactionsToCsv(context: android.content.Context, transactions: List<Transaction>): File {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val file = File(context.cacheDir, "flowcash_transacoes_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.csv")
    file.bufferedWriter().use { writer ->
        writer.write("ID,Título,Categoria,Tipo,Valor,Data,Descrição\n")
        transactions.sortedByDescending { it.date }.forEach { tx ->
            val line = listOf(
                tx.id.toString(),
                escapeCsv(tx.title),
                escapeCsv(tx.category),
                tx.type,
                tx.amount.toString(),
                dateFormat.format(Date(tx.date)),
                escapeCsv(tx.description)
            ).joinToString(",")
            writer.write(line + "\n")
        }
    }
    return file
}

private fun exportGoalsToCsv(context: android.content.Context, goals: List<com.zacariasthequimo.flowcash.data.entity.Goal>): File {
    val file = File(context.cacheDir, "flowcash_metas_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.csv")
    file.bufferedWriter().use { writer ->
        writer.write("ID,Título,Categoria,Valor Atual,Valor Alvo,Progresso\n")
        goals.forEach { goal ->
            val progress = if (goal.targetAmount > 0) "${"%.0f".format(goal.currentAmount / goal.targetAmount * 100)}%" else "0%"
            val line = listOf(
                goal.id.toString(),
                escapeCsv(goal.title),
                escapeCsv(goal.category),
                goal.currentAmount.toString(),
                goal.targetAmount.toString(),
                progress
            ).joinToString(",")
            writer.write(line + "\n")
        }
    }
    return file
}

private fun escapeCsv(value: String): String {
    return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
        "\"${value.replace("\"", "\"\"")}\""
    } else value
}

private fun shareFile(context: android.content.Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartilhar"))
}
