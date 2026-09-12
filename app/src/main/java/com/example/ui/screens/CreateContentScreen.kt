package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ResultCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

data class ToolTab(
    val id: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val credits: Int = 2
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContentScreen(viewModel: AppViewModel) {
    val activeTool by viewModel.activeCreateTool.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    val tabs = listOf(
        ToolTab("POST_FB", "Facebook & WhatsApp", Icons.Default.Campaign, 2),
        ToolTab("INSTA_POST", "Instagram & Reels", Icons.Default.PhotoCamera, 2),
        ToolTab("AD_CAMPAIGN", "Anúncios (4 Versões)", Icons.Default.RocketLaunch, 3),
        ToolTab("CLIENT_REPLY", "Responder Clientes", Icons.Default.Forum, 1),
        ToolTab("PROD_DESC", "Descrição de Produto", Icons.Default.ShoppingBag, 2),
        ToolTab("NAME_GEN", "Gerador de Nomes", Icons.Default.Lightbulb, 2),
        ToolTab("SLOGAN_GEN", "Gerador de Slogans", Icons.Default.AutoFixHigh, 1),
        ToolTab("MKT_PLAN", "Plano de Marketing", Icons.Default.CalendarMonth, 3),
        ToolTab("CONTENT_IDEAS", "Ideias de Conteúdo", Icons.Default.TipsAndUpdates, 2)
    )

    var currentResult by remember { mutableStateOf<String?>(null) }
    var resultTitle by remember { mutableStateOf("") }

    // Form states
    var productInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var goalInput by remember { mutableStateOf("Vendas imediatas") }
    var toneInput by remember { mutableStateOf("Amigável e persuasivo") }
    var styleInput by remember { mutableStateOf("Moderno e Profissional") }
    var clientMessageInput by remember { mutableStateOf("Olá, quanto custa e como posso pagar?") }
    var clientIntentInput by remember { mutableStateOf("Pedido de preço e pagamento") }
    var durationPlanInput by remember { mutableStateOf("30") }
    var keywordsInput by remember { mutableStateOf("qualidade, confiança, rápido") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Criar Conteúdo & Anúncios",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Gere campanhas, respostas e materiais comerciais sob medida para Angola.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tool Switcher Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tabs) { tab ->
                    val isSelected = activeTool == tab.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectCreateTool(tab.id)
                            currentResult = null
                        },
                        label = {
                            Text(tab.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) BrandPrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimaryBlue.copy(alpha = 0.15f),
                            selectedLabelColor = BrandPrimaryBlue
                        ),
                        modifier = Modifier.testTag("tool_chip_${tab.id.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Active Tool Form Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    val currentTab = tabs.firstOrNull { it.id == activeTool } ?: tabs.first()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentTab.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BrandGoldKz.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Consome ${currentTab.credits} créditos",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandGoldKz,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    when (activeTool) {
                        "POST_FB" -> {
                            OutlinedTextField(
                                value = productInput,
                                onValueChange = { productInput = it },
                                label = { Text("Produto / Oferta em Destaque") },
                                placeholder = { Text("Ex: Vestido de Festa / Almoço Executivo") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            OutlinedTextField(
                                value = priceInput,
                                onValueChange = { priceInput = it },
                                label = { Text("Preço em Kwanza (opcional)") },
                                placeholder = { Text("Ex: 25.000 Kz") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            OutlinedTextField(
                                value = goalInput,
                                onValueChange = { goalInput = it },
                                label = { Text("Objetivo do Post") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }

                        "INSTA_POST" -> {
                            OutlinedTextField(
                                value = productInput,
                                onValueChange = { productInput = it },
                                label = { Text("Tema ou Produto do Conteúdo") },
                                placeholder = { Text("Ex: Novidades da semana / Dicas de uso") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            OutlinedTextField(
                                value = toneInput,
                                onValueChange = { toneInput = it },
                                label = { Text("Tom de Voz") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }

                        "AD_CAMPAIGN" -> {
                            OutlinedTextField(
                                value = productInput,
                                onValueChange = { productInput = it },
                                label = { Text("O que você vai anunciar?") },
                                placeholder = { Text("Ex: Sapatos sociais italianos / Kit manicure") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            OutlinedTextField(
                                value = priceInput,
                                onValueChange = { priceInput = it },
                                label = { Text("Preço ou Condição Especial") },
                                placeholder = { Text("Ex: De 35.000 Kz por 28.000 Kz") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }

                        "CLIENT_REPLY" -> {
                            OutlinedTextField(
                                value = clientMessageInput,
                                onValueChange = { clientMessageInput = it },
                                label = { Text("Mensagem enviada pelo cliente") },
                                placeholder = { Text("Cole aqui o que o cliente perguntou no WhatsApp") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                minLines = 2
                            )
                            OutlinedTextField(
                                value = productInput,
                                onValueChange = { productInput = it },
                                label = { Text("Produto referente") },
                                placeholder = { Text("Ex: Cabaz de Natal / Reparação de PC") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            OutlinedTextField(
                                value = priceInput,
                                onValueChange = { priceInput = it },
                                label = { Text("Preço do produto (Kz)") },
                                placeholder = { Text("Ex: 15.000 Kz") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }

                        "PROD_DESC" -> {
                            OutlinedTextField(
                                value = productInput,
                                onValueChange = { productInput = it },
                                label = { Text("Nome do Produto / Serviço") },
                                placeholder = { Text("Ex: Smartphone Samsung Galaxy A54") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            OutlinedTextField(
                                value = priceInput,
                                onValueChange = { priceInput = it },
                                label = { Text("Preço em Kz") },
                                placeholder = { Text("Ex: 195.000 Kz") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }

                        "NAME_GEN" -> {
                            OutlinedTextField(
                                value = keywordsInput,
                                onValueChange = { keywordsInput = it },
                                label = { Text("Palavras-chave ou Essência") },
                                placeholder = { Text("Ex: luxo, tecnologia, angolano, rápido") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            OutlinedTextField(
                                value = styleInput,
                                onValueChange = { styleInput = it },
                                label = { Text("Estilo do Nome") },
                                placeholder = { Text("Ex: Moderno, Tradicional, Internacional") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }

                        "SLOGAN_GEN" -> {
                            OutlinedTextField(
                                value = productInput,
                                onValueChange = { productInput = it },
                                label = { Text("Principal Diferencial do Negócio") },
                                placeholder = { Text("Ex: Entrega em 24h, melhor preço, simpatia") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }

                        "MKT_PLAN" -> {
                            OutlinedTextField(
                                value = durationPlanInput,
                                onValueChange = { durationPlanInput = it },
                                label = { Text("Duração do Plano (dias)") },
                                placeholder = { Text("Ex: 7, 15, 30 ou 90 dias") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                            OutlinedTextField(
                                value = goalInput,
                                onValueChange = { goalInput = it },
                                label = { Text("Meta Principal") },
                                placeholder = { Text("Ex: Faturar 1.500.000 Kz este mês") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }

                        "CONTENT_IDEAS" -> {
                            OutlinedTextField(
                                value = productInput,
                                onValueChange = { productInput = it },
                                label = { Text("Foco ou Linha Editorial") },
                                placeholder = { Text("Ex: Tendências de moda angolana / Gastronomia") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val inputs = mutableMapOf<String, String>()
                            inputs["product"] = productInput
                            inputs["price"] = priceInput
                            inputs["goal"] = goalInput
                            inputs["tone"] = toneInput
                            inputs["style"] = styleInput
                            inputs["message"] = clientMessageInput
                            inputs["intent"] = clientIntentInput
                            inputs["duration"] = durationPlanInput
                            inputs["keywords"] = keywordsInput
                            inputs["phone"] = profile?.whatsapp?.ifBlank { profile?.phone } ?: "923 000 000"

                            val title = "${currentTab.name}: ${productInput.ifBlank { profile?.businessName ?: "Conteúdo" }}"
                            resultTitle = title

                            viewModel.generateContent(
                                toolType = activeTool,
                                title = title,
                                inputs = inputs,
                                credits = currentTab.credits
                            ) { result ->
                                currentResult = result
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_content_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gerar com IA Agora",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Result Display
        if (currentResult != null) {
            item {
                Text(
                    text = "RESULTADO GERADO PELA IA",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimaryBlue,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                ResultCard(
                    title = resultTitle,
                    content = currentResult!!
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
