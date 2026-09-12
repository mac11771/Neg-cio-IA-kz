package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppRepository
import com.example.ui.components.ResultCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val generations by viewModel.generations.collectAsState()

    val totalRevenueKz = sales.sumOf { it.totalPriceKz }
    val totalProfitKz = sales.sumOf { it.profitKz }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Welcome Header
        item {
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BrandPrimaryBlue.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    BrandPrimaryBlue.copy(alpha = 0.12f),
                                    BrandPurpleAi.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Olá, ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Empreendedor"}! 👋",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = profile?.businessName ?: "O seu negócio em Angola",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = BrandPrimaryBlue
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandGoldKz.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, BrandGoldKz.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "Plano ${currentUser?.currentPlan ?: "FREE"}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGoldKz,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Como podemos fazer o seu negócio crescer hoje?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Fast KPI Metrics
        item {
            Text(
                text = "RESUMO DO NEGÓCIO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Créditos IA",
                    value = "${currentUser?.credits ?: 0}",
                    subtitle = "Recarregar",
                    color = BrandGoldKz,
                    icon = Icons.Default.Bolt,
                    onClick = { viewModel.navigateTo(AppScreen.PRICING) },
                    modifier = Modifier.weight(1f)
                )

                KpiCard(
                    title = "Vendas Totais",
                    value = AppRepository.formatKz(totalRevenueKz),
                    subtitle = "Lucro: ${AppRepository.formatKz(totalProfitKz)}",
                    color = BrandSuccessGreen,
                    icon = Icons.Default.TrendingUp,
                    onClick = { viewModel.navigateTo(AppScreen.SALES) },
                    modifier = Modifier.weight(1.4f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Conteúdos Criados",
                    value = "${generations.size}",
                    subtitle = "Histórico salvo",
                    color = BrandPurpleAi,
                    icon = Icons.Default.AutoAwesome,
                    onClick = { viewModel.navigateTo(AppScreen.CREATE_TOOLS) },
                    modifier = Modifier.weight(1f)
                )

                KpiCard(
                    title = "Província & Local",
                    value = currentUser?.province ?: "Luanda",
                    subtitle = currentUser?.municipality ?: "Angola",
                    color = BrandElectricCyan,
                    icon = Icons.Default.LocationOn,
                    onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Quick Hub Cards
        item {
            Text(
                text = "FERRAMENTAS PRINCIPAIS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            val hubTools = listOf(
                HubItem("Assistente IA", "Converse e peça conselhos estratégicos para o seu negócio.", Icons.Default.SmartToy, BrandPurpleAi) {
                    viewModel.navigateTo(AppScreen.AI_CHAT)
                },
                HubItem("Post Facebook & WhatsApp", "Textos com gancho, preço em Kz e CTA direto para mensagem.", Icons.Default.Campaign, BrandPrimaryBlue) {
                    viewModel.selectCreateTool("POST_FB")
                },
                HubItem("Anúncios de Alta Conversão", "4 versões para status e tráfego pago.", Icons.Default.RocketLaunch, BrandGoldKz) {
                    viewModel.selectCreateTool("AD_CAMPAIGN")
                },
                HubItem("Orçamentos em Kz", "Gere orçamentos formais e envie em segundos pelo WhatsApp.", Icons.Default.ReceiptLong, BrandSuccessGreen) {
                    viewModel.navigateTo(AppScreen.DOCUMENTS)
                },
                HubItem("Responder Clientes", "Respostas acolhedoras com opções Multicaixa Express.", Icons.Default.Forum, BrandElectricCyan) {
                    viewModel.selectCreateTool("CLIENT_REPLY")
                },
                HubItem("Plano de Marketing", "Estratégia completa de 30 dias para vender mais.", Icons.Default.CalendarMonth, BrandPurpleLight) {
                    viewModel.selectCreateTool("MKT_PLAN")
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                hubTools.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { tool ->
                            HubCard(
                                item = tool,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Recent Generations / Activities
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ÚLTIMOS CONTEÚDOS GERADOS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { viewModel.navigateTo(AppScreen.CREATE_TOOLS) }) {
                    Text("Ver Todos", color = BrandPrimaryBlue, fontWeight = FontWeight.SemiBold)
                }
            }

            if (generations.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = BrandPrimaryBlue, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ainda não gerou nenhum conteúdo.", fontWeight = FontWeight.Bold)
                        Text("Clique em 'Criar' para gerar seu primeiro post ou anúncio com IA.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                generations.take(3).forEach { gen ->
                    ResultCard(
                        title = gen.title,
                        content = gen.outputContent,
                        isFavorite = gen.isFavorite,
                        onFavoriteToggle = {
                            viewModel.toggleGenerationFavorite(gen.id, !gen.isFavorite)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

data class HubItem(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun HubCard(item: HubItem, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
        modifier = modifier
            .height(145.dp)
            .clickable { item.onClick() }
            .testTag("hub_card_${item.title.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(22.dp))
            }

            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
            .clickable { onClick() }
            .testTag("kpi_card_${title.lowercase().replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
