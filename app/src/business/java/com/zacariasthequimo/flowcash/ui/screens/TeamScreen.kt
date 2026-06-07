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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zacariasthequimo.flowcash.data.entity.TeamMember
import com.zacariasthequimo.flowcash.ui.BusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(viewModel: BusinessViewModel) {
    val members by viewModel.teamMembers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<TeamMember?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equipa", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = "Adicionar Membro", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (members.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Groups, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(12.dp))
                    Text("Nenhum membro na equipa", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Adicione o primeiro membro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members, key = { it.id }) { member ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedMember = member },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    member.name.split(" ").filter { it.isNotBlank() }.take(2).map { it.first().uppercase() }.joinToString(""),
                                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(member.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(member.role, style = MaterialTheme.typography.bodySmall, color = when (member.role) {
                                    "OWNER" -> Color(0xFFF59E0B)
                                    "ADMIN" -> Color(0xFF3B82F6)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTeamMemberDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, email, role, permissions ->
                viewModel.addTeamMember(name, email, role, permissions)
                showAddDialog = false
            }
        )
    }

    if (selectedMember != null) {
        TeamMemberDetailDialog(
            member = selectedMember!!,
            onDismiss = { selectedMember = null },
            onRoleChange = { newRole ->
                viewModel.updateTeamMember(selectedMember!!.copy(role = newRole))
                selectedMember = null
            },
            onDelete = {
                viewModel.deleteTeamMember(selectedMember!!)
                selectedMember = null
            }
        )
    }
}

@Composable
private fun AddTeamMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("STAFF") }
    var permissions by remember { mutableStateOf("ver_vendas,ver_clientes") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Membro", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Text("Função", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("OWNER" to "Proprietário", "ADMIN" to "Admin", "STAFF" to "Staff").forEach { (key, label) ->
                        FilterChip(selected = role == key, onClick = { role = key }, label = { Text(label) })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name, email, role, permissions) }, enabled = name.isNotBlank(), shape = RoundedCornerShape(12.dp)) {
                Text("Adicionar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun TeamMemberDetailDialog(
    member: TeamMember,
    onDismiss: () -> Unit,
    onRoleChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(member.name, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Email: ${member.email}", style = MaterialTheme.typography.bodyMedium)
                Text("Função atual: ${member.role}", style = MaterialTheme.typography.bodyMedium)

                Text("Alterar função:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ADMIN" to "Admin", "STAFF" to "Staff").forEach { (key, label) ->
                        FilterChip(selected = member.role == key, onClick = { onRoleChange(key) }, label = { Text(label) })
                    }
                }

                if (showDeleteConfirm) {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Confirmar Exclusão") }
                } else {
                    TextButton(onClick = { showDeleteConfirm = true }) {
                        Text("Remover da Equipa", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Fechar") } }
    )
}
