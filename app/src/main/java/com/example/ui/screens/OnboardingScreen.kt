package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimaryBlue
import com.example.ui.theme.BrandPurpleAi
import com.example.ui.viewmodel.AppViewModel

@Composable
fun OnboardingScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val initialProfile by viewModel.businessProfile.collectAsState()

    var businessName by remember(initialProfile) { mutableStateOf(initialProfile?.businessName ?: currentUser?.businessName ?: "") }
    var businessType by remember(initialProfile) { mutableStateOf(initialProfile?.businessType ?: currentUser?.businessType ?: "Loja de Roupas") }
    var products by remember(initialProfile) { mutableStateOf(initialProfile?.productsOrServices ?: "") }
    var audience by remember(initialProfile) { mutableStateOf(initialProfile?.targetAudience ?: "") }
    var province by remember(initialProfile) { mutableStateOf(initialProfile?.province ?: currentUser?.province ?: "Luanda") }
    var municipality by remember(initialProfile) { mutableStateOf(initialProfile?.municipality ?: currentUser?.municipality ?: "Maianga") }
    var mainGoal by remember(initialProfile) { mutableStateOf(initialProfile?.mainGoal ?: "Aumentar as vendas pelo WhatsApp") }
    var socialMedia by remember(initialProfile) { mutableStateOf(initialProfile?.socialMedia ?: "WhatsApp Business e Instagram") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BrandPurpleAi.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandPurpleAi, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Assistente de Configuração Inteligente",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandPurpleAi
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Vamos Personalizar a Sua IA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Responda às 7 perguntas abaixo. A IA criará o perfil do seu negócio para gerar respostas altamente personalizadas para você.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("1. Qual é o nome do seu negócio?") },
                placeholder = { Text("Ex: Boutique Kilamba Fashion") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("onboarding_q1")
            )

            OutlinedTextField(
                value = businessType,
                onValueChange = { businessType = it },
                label = { Text("2. Qual é o tipo de negócio?") },
                placeholder = { Text("Ex: Loja de Calçados, Restaurante, Salão") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("onboarding_q2")
            )

            OutlinedTextField(
                value = products,
                onValueChange = { products = it },
                label = { Text("3. O que você vende?") },
                placeholder = { Text("Ex: Vestidos, comida caseira, serviços de design gráfico") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("onboarding_q3")
            )

            OutlinedTextField(
                value = audience,
                onValueChange = { audience = it },
                label = { Text("4. Quem são seus clientes?") },
                placeholder = { Text("Ex: Mulheres de 20 a 45 anos, empresas, famílias") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("onboarding_q4")
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = province,
                    onValueChange = { province = it },
                    label = { Text("5. Província") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp)
                )
                OutlinedTextField(
                    value = municipality,
                    onValueChange = { municipality = it },
                    label = { Text("Município") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp)
                )
            }

            OutlinedTextField(
                value = mainGoal,
                onValueChange = { mainGoal = it },
                label = { Text("6. Qual é o seu principal objetivo?") },
                placeholder = { Text("Ex: Aumentar faturamento, atrair clientes no WhatsApp") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("onboarding_q6")
            )

            OutlinedTextField(
                value = socialMedia,
                onValueChange = { socialMedia = it },
                label = { Text("7. Quais redes sociais utiliza?") },
                placeholder = { Text("Ex: WhatsApp Business, Instagram, Facebook, TikTok") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("onboarding_q7")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (businessName.isNotBlank() && products.isNotBlank()) {
                        viewModel.completeOnboarding(
                            businessName, businessType, products, audience,
                            province, municipality, mainGoal, socialMedia
                        )
                    } else {
                        viewModel.showSnackbar("Por favor preencha pelo menos o nome do negócio e o que vende.")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_finish_button")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Criar Meu Perfil Empresarial IA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
