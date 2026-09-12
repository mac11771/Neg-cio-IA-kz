package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@Composable
fun ProfileScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var businessName by remember(profile) { mutableStateOf(profile?.businessName ?: "") }
    var businessType by remember(profile) { mutableStateOf(profile?.businessType ?: "") }
    var products by remember(profile) { mutableStateOf(profile?.productsOrServices ?: "") }
    var audience by remember(profile) { mutableStateOf(profile?.targetAudience ?: "") }
    var province by remember(profile) { mutableStateOf(profile?.province ?: "") }
    var municipality by remember(profile) { mutableStateOf(profile?.municipality ?: "") }
    var whatsapp by remember(profile) { mutableStateOf(profile?.whatsapp ?: "") }
    var mainGoal by remember(profile) { mutableStateOf(profile?.mainGoal ?: "") }

    var customApiKeyInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            // User Header Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BrandPrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.fullName?.take(1) ?: "N",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = currentUser?.fullName ?: "Empreendedor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentUser?.email ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandGoldKz.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Plano ${currentUser?.currentPlan ?: "FREE"} • ${currentUser?.credits ?: 0} Créditos",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandGoldKz,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "DADOS DO NEGÓCIO (CONTEXTO DA IA)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Nome da Empresa / Negócio") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = businessType,
                        onValueChange = { businessType = it },
                        label = { Text("Tipo de Negócio / Categoria") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = products,
                        onValueChange = { products = it },
                        label = { Text("Produtos e Serviços Vendidos") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = audience,
                        onValueChange = { audience = it },
                        label = { Text("Público-Alvo") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = province,
                            onValueChange = { province = it },
                            label = { Text("Província") },
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = municipality,
                            onValueChange = { municipality = it },
                            label = { Text("Município") },
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                        )
                    }

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("Número WhatsApp Comercial") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = mainGoal,
                        onValueChange = { mainGoal = it },
                        label = { Text("Objetivo Principal") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (profile != null) {
                                val updated = profile!!.copy(
                                    businessName = businessName,
                                    businessType = businessType,
                                    productsOrServices = products,
                                    targetAudience = audience,
                                    province = province,
                                    municipality = municipality,
                                    whatsapp = whatsapp,
                                    mainGoal = mainGoal
                                )
                                viewModel.updateProfile(updated)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salvar Perfil do Negócio")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Appearance & Settings
            Text(
                text = "PREFERÊNCIAS & SISTEMA",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Modo Escuro (Dark Mode)", fontWeight = FontWeight.Bold)
                            Text("Alternar tema visual do aplicativo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Optional Custom Gemini API Key
                    Text("Chave Gemini API (Opcional)", fontWeight = FontWeight.Bold)
                    Text(
                        "O sistema já possui o motor inteligente de Angola integrado. Se desejar usar sua própria chave de API do Gemini 3.5 Flash, introduza abaixo:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customApiKeyInput,
                        onValueChange = { customApiKeyInput = it },
                        label = { Text("Chave da API Gemini") },
                        placeholder = { Text("AIzaSy...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.setCustomApiKey(customApiKeyInput) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Salvar Chave")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.logout() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Terminar Sessão")
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
