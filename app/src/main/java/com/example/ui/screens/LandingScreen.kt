package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@Composable
fun LandingScreen(viewModel: AppViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Hero Badge
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = BrandPrimaryBlue.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, BrandPrimaryBlue.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrandPrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "A 1ª Plataforma IA de Negócios de Angola 🇦🇴",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandPrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hero Title
            Text(
                text = "Transforme seu pequeno negócio com inteligência artificial.",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle
            Text(
                text = "Crie conteúdos, anúncios, propostas, orçamentos e estratégias de marketing em poucos segundos. 100% adaptado à realidade de Angola e ao Kwanza (Kz).",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CTA Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .testTag("start_free_button")
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Começar Gratuitamente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { viewModel.navigateTo(AppScreen.AUTH) },
                modifier = Modifier.testTag("login_link_button")
            ) {
                Text(
                    text = "Já tem uma conta? Entrar",
                    color = BrandPrimaryBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // How it Works
        item {
            Text(
                text = "COMO FUNCIONA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BrandPrimaryBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Simples, Rápido e Sem Complicações",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            val steps = listOf(
                Triple("1. Perfil do Seu Negócio", "Informe o nome, setor e o que você vende em Luanda ou províncias.", Icons.Outlined.Store),
                Triple("2. A IA Gera em Segundos", "Posts de WhatsApp, anúncios, orçamentos em Kz ou ideias de posts.", Icons.Outlined.AutoAwesome),
                Triple("3. Venda Mais Todos os Dias", "Copie, envie aos clientes e acompanhe suas vendas e lucros.", Icons.Outlined.TrendingUp)
            )

            steps.forEach { (title, desc, icon) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrandPrimaryBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = BrandPrimaryBlue)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Features Grid
        item {
            Text(
                text = "TUDO O QUE O SEU NEGÓCIO PRECISA",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BrandPrimaryBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ferramentas Especializadas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            val features = listOf(
                Pair("🤖 Assistente IA Contextualizado", "Conhece seu catálogo, público e localização para dar respostas práticas."),
                Pair("✍️ Posts para Facebook & Instagram", "Legendas persuasivas, roteiros de Reels e Stories com chamada WhatsApp."),
                Pair("📢 Anúncios & Campanhas", "4 versões para status e tráfego pago (Curta, Persuasiva, Profissional, Urgente)."),
                Pair("💬 Responder Clientes no WhatsApp", "Respostas acolhedoras com opções Multicaixa Express e prazos."),
                Pair("💰 Orçamentos Profissionais em Kz", "Cálculo automático de impostos e partilha instantânea no WhatsApp."),
                Pair("📊 Controle & Análise de Vendas", "Diagnóstico financeiro e sugestões para alavancar seu lucro.")
            )

            features.forEach { (title, desc) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Testimonials
        item {
            Text(
                text = "DEPOIMENTOS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BrandPrimaryBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Quem usa, recomenda em Angola",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            val testimonials = listOf(
                Triple("Dona Maria Silva", "Boutique em Luanda (Kilamba)", "“Antes eu passava horas pensando no que postar no WhatsApp e no Insta. Com o Negócio IA Kz, gero legendas e anúncios em 10 segundos!”"),
                Triple("Carlos Fernandes", "Técnico de Informática (Benguela)", "“Os orçamentos profissionais em Kz aumentaram muito a confiança dos meus clientes. Fechei 4 contratos logo na primeira semana.”"),
                Triple("Ana Paula Bento", "Restaurante & Sabores (Talatona)", "“A análise de vendas com IA me mostrou quais pratos davam mais lucro real e como economizar nos custos. Excelente!”")
            )

            testimonials.forEach { (author, role, quote) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BrandPrimaryBlue.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(quote, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(author, fontWeight = FontWeight.Bold, color = BrandPrimaryBlue)
                        Text(role, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // Final CTA
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BrandPrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pronto para fazer o seu negócio crescer?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Crie sua conta agora e ganhe 50 créditos grátis para começar hoje mesmo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = BrandPrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Aceder à Plataforma", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
