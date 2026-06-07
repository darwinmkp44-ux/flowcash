package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zacariasthequimo.flowcash.ui.BusinessViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitLossScreen(viewModel: BusinessViewModel) {
    val scope = rememberCoroutineScope()
    var report by remember { mutableStateOf<com.zacariasthequimo.flowcash.ui.ProfitLossReport?>(null) }
    var selectedMonth by remember { mutableStateOf(0) } // 0 = current, -1 = last, etc.

    val nf = NumberFormat.getNumberInstance(Locale("pt", "MZ")).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }

    LaunchedEffect(selectedMonth) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, selectedMonth)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59)
        val end = cal.timeInMillis
        report = viewModel.getProfitLossReport(start, end)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatórios", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Text("Lucro & Prejuízo", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selectedMonth == 0, onClick = { selectedMonth = 0 }, label = { Text("Este Mês") })
                FilterChip(selected = selectedMonth == -1, onClick = { selectedMonth = -1 }, label = { Text("Mês Passado") })
            }

            if (report != null) {
                val r = report!!

                // Revenue card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Receita Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("${nf.format(r.revenue)} MT", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp), color = Color(0xFF22C55E))
                        Spacer(Modifier.height(4.dp))
                        Text("$r.totalSales vendas realizadas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Resumo Financeiro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        ReportRow("Receita", "${nf.format(r.revenue)} MT", Color(0xFF22C55E))
                        ReportRow("Custo Estimado", "${nf.format(r.revenue * 0.6)} MT", Color(0xFFF59E0B))
                        HorizontalDivider()
                        ReportRow("Lucro Estimado", "${nf.format(r.revenue * 0.4)} MT", Color(0xFF22C55E))
                        HorizontalDivider()
                        ReportRow("Dívidas Pendentes", "${nf.format(r.pendingDebts)} MT", Color(0xFFEF4444))
                    }
                }

                // Insights
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        if (r.revenue > 0) {
                            Text(
                                "• Margem de lucro estimada: ${String.format("%.0f", 40.0)}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "• Média por venda: ${nf.format(r.revenue / r.totalSales)} MT",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                "• Nenhuma venda registada neste período",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "• Dívidas a receber: ${nf.format(r.pendingDebts)} MT",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = color)
    }
}
