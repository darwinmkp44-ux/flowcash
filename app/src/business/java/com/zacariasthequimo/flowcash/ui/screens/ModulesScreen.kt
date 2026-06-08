package com.zacariasthequimo.flowcash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zacariasthequimo.flowcash.ui.BusinessViewModel

data class BusinessModule(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String
)

@Composable
fun ModulesScreen(viewModel: BusinessViewModel) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("flowcash_modules", android.content.Context.MODE_PRIVATE)

    val allModules = remember {
        listOf(
            BusinessModule("CLIENTES", "Clientes", Icons.Outlined.People, "Gest\u00e3o de clientes e contactos"),
            BusinessModule("VENDAS", "Vendas", Icons.Outlined.ShoppingCart, "Registo e acompanhamento de vendas"),
            BusinessModule("DIVIDAS", "D\u00edvidas", Icons.Outlined.MonetizationOn, "Controlo de d\u00edvidas a receber"),
            BusinessModule("PRODUTOS", "Produtos", Icons.Outlined.Inventory2, "Stock e gest\u00e3o de produtos"),
            BusinessModule("AGENDA", "Agenda", Icons.Outlined.CalendarMonth, "Compromissos e lembretes"),
            BusinessModule("EQUIPA", "Equipa", Icons.Outlined.Groups, "Gest\u00e3o de membros da equipa"),
            BusinessModule("RELATORIOS", "Relat\u00f3rios", Icons.Outlined.BarChart, "Relat\u00f3rios de lucros e desempenho")
        )
    }

    var disabledSet by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(Unit) {
        val saved = prefs.getString("disabled_modules", "") ?: ""
        disabledSet = if (saved.isBlank()) emptySet() else saved.split(",").toSet()
    }

    fun toggleModule(id: String, enabled: Boolean) {
        val newSet = if (enabled) disabledSet - id else disabledSet + id
        disabledSet = newSet
        prefs.edit().putString("disabled_modules", newSet.joinToString(",")).apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "M\u00f3dulos Ativ\u00e1veis",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Ative ou desative os m\u00f3dulos conforme a sua necessidade. Os m\u00f3dulos desativados n\u00e3o aparecem na tela Business.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        allModules.forEach { module ->
            val enabled = module.id !in disabledSet
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (enabled)
                        MaterialTheme.colorScheme.surfaceContainerLowest
                    else
                        MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (enabled) 0.5f.dp else 0.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = null,
                        tint = if (enabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (enabled)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = module.description,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = enabled,
                        onCheckedChange = { toggleModule(module.id, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
