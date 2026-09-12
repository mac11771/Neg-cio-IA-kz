package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@Composable
fun AdminScreen(viewModel: AppViewModel) {
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var editingUser by remember { mutableStateOf<UserEntity?>(null) }
    var addCreditsDialogUser by remember { mutableStateOf<UserEntity?>(null) }
    var creditsToAddInput by remember { mutableStateOf("100") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BrandPurpleAi.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = BrandPurpleAi, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Área Restrita da Administração", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = BrandPurpleAi)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Painel Administrativo Geral",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Gestão de usuários, concessão de créditos e estatísticas do sistema.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Admin KPI Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FinancialKpiCard(
                    title = "Total de Usuários",
                    value = "${allUsers.size}",
                    color = BrandPrimaryBlue,
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f)
                )

                FinancialKpiCard(
                    title = "Planos Ativos",
                    value = "${allUsers.count { it.currentPlan != "FREE" }} Premium",
                    color = BrandGoldKz,
                    icon = Icons.Default.Stars,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "GESTÃO DE CONTAS & CRÉDITOS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        items(allUsers) { user ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (user.role == "ADMIN") {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = BrandPurpleAi.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "ADMIN",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandPurpleAi,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${user.businessName} • ${user.province} (${user.municipality})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "E-mail: ${user.email} • Tel: ${user.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandGoldKz.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${user.credits} Créditos",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGoldKz,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Plano ${user.currentPlan}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.switchUser(user)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Entrar como", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = {
                                addCreditsDialogUser = user
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Créditos", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Add Credits Dialog
    if (addCreditsDialogUser != null) {
        AlertDialog(
            onDismissRequest = { addCreditsDialogUser = null },
            title = { Text("Adicionar Créditos a ${addCreditsDialogUser!!.fullName}") },
            text = {
                Column {
                    Text("Quantidade de créditos a conceder:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = creditsToAddInput,
                        onValueChange = { creditsToAddInput = it },
                        label = { Text("Créditos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amount = creditsToAddInput.toIntOrNull() ?: 100
                    viewModel.adminAddCredits(addCreditsDialogUser!!.id, amount, "Bónus Administrativo")
                    addCreditsDialogUser = null
                }) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { addCreditsDialogUser = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
