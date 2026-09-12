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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

data class PlanItem(
    val name: String,
    val priceKz: String,
    val period: String,
    val isPopular: Boolean,
    val features: List<String>,
    val color: Color
)

data class CreditPackage(
    val credits: Int,
    val priceKz: Double,
    val bonus: String = ""
)

@Composable
fun PricingPlansScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()

    var showMulticaixaModal by remember { mutableStateOf(false) }
    var selectedPackageToBuy by remember { mutableStateOf<CreditPackage?>(null) }
    var selectedPlanToUpgrade by remember { mutableStateOf<PlanItem?>(null) }

    val plans = listOf(
        PlanItem(
            name = "FREE",
            priceKz = "0 Kz",
            period = "/mês",
            isPopular = false,
            features = listOf(
                "50 créditos iniciais",
                "Acesso ao Assistente IA",
                "Gerador de posts e legendas",
                "Até 3 orçamentos salvos"
            ),
            color = MaterialTheme.colorScheme.outline
        ),
        PlanItem(
            name = "PREMIUM",
            priceKz = "15.000 Kz",
            period = "/mês",
            isPopular = true,
            features = listOf(
                "150 créditos mensais (+ bónus)",
                "Todos os 9 geradores de conteúdo",
                "Anúncios persuasivos (4 versões)",
                "Orçamentos e propostas ilimitadas",
                "Análise inteligente de vendas",
                "Exportação direta para WhatsApp"
            ),
            color = BrandPrimaryBlue
        ),
        PlanItem(
            name = "BUSINESS",
            priceKz = "35.000 Kz",
            period = "/mês",
            isPopular = false,
            features = listOf(
                "500 créditos mensais",
                "Consultoria estratégica com IA",
                "Planos de marketing de 90 dias",
                "Diagnóstico completo de faturamento",
                "Suporte VIP dedicado",
                "Múltiplos utilizadores e filiais"
            ),
            color = BrandPurpleAi
        )
    )

    val packages = listOf(
        CreditPackage(50, 1500.0),
        CreditPackage(100, 2500.0, "Mais vendido"),
        CreditPackage(300, 6000.0, "Economize 1.500 Kz"),
        CreditPackage(1000, 18000.0, "Melhor valor")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BrandGoldKz.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Seu Saldo: ${currentUser?.credits ?: 0} Créditos",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandGoldKz,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Planos & Recarga de Créditos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Escolha o melhor plano ou compre créditos avulsos com Multicaixa Express.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "PACOTES DE RECARGA RÁPIDA",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Credit Packages Row
        items(packages) { pkg ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${pkg.credits} Créditos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (pkg.bonus.isNotBlank()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandGoldKz.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = pkg.bonus,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandGoldKz,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = AppRepository.formatKz(pkg.priceKz),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandPrimaryBlue
                        )
                    }

                    Button(
                        onClick = {
                            selectedPackageToBuy = pkg
                            showMulticaixaModal = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                        modifier = Modifier.testTag("buy_credits_${pkg.credits}")
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Comprar")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "PLANOS MENSAIS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Subscription Plans List
        items(plans) { plan ->
            val isCurrentPlan = currentUser?.currentPlan?.uppercase() == plan.name.uppercase()

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (plan.isPopular) BrandPrimaryBlue.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    if (plan.isPopular) 2.dp else 1.dp,
                    if (plan.isPopular) BrandPrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (plan.isPopular) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandPrimaryBlue,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "MAIS ESCOLHIDO EM ANGOLA 🔥",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plano ${plan.name}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = plan.priceKz + plan.period,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = plan.color
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    plan.features.forEach { feat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BrandSuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feat,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (!isCurrentPlan) {
                                selectedPlanToUpgrade = plan
                                showMulticaixaModal = true
                            }
                        },
                        enabled = !isCurrentPlan,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (plan.isPopular) BrandPrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (plan.isPopular) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text(
                            text = if (isCurrentPlan) "Plano Atual" else "Subscrever Plano ${plan.name}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Multicaixa Express Simulation Checkout Dialog
    if (showMulticaixaModal) {
        val itemName = selectedPackageToBuy?.let { "${it.credits} Créditos" } ?: selectedPlanToUpgrade?.let { "Plano ${it.name}" } ?: "Assinatura"
        val price = selectedPackageToBuy?.let { AppRepository.formatKz(it.priceKz) } ?: selectedPlanToUpgrade?.priceKz ?: "0 Kz"

        AlertDialog(
            onDismissRequest = { showMulticaixaModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = BrandPrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pagamento Multicaixa Express")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Item: $itemName",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Valor: $price",
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandPrimaryBlue,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("DADOS PARA PAGAMENTO:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Entidade Multicaixa: 00452")
                            Text("• Referência: 928 412 003")
                            Text("• Ou Multicaixa Express: 924 999 000")
                            Text("• Titular: Negócio IA Kz Soluções Lda")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ambiente de Simulação Integrado: clique em 'Confirmar Pagamento' para liberar os créditos instantaneamente na sua conta.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedPackageToBuy != null) {
                            viewModel.buyCreditPackage(selectedPackageToBuy!!.credits, selectedPackageToBuy!!.priceKz)
                        } else if (selectedPlanToUpgrade != null) {
                            viewModel.upgradePlan(selectedPlanToUpgrade!!.name)
                        }
                        showMulticaixaModal = false
                        selectedPackageToBuy = null
                        selectedPlanToUpgrade = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSuccessGreen)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Confirmar Pagamento")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMulticaixaModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
