package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SaleEntity
import com.example.data.repository.AppRepository
import com.example.ui.components.ResultCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(viewModel: AppViewModel) {
    val sales by viewModel.sales.collectAsState()
    var showAddSaleDialog by remember { mutableStateOf(false) }
    var aiAnalysisResult by remember { mutableStateOf<String?>(null) }

    val totalRevenue = sales.sumOf { it.totalPriceKz }
    val totalProfit = sales.sumOf { it.profitKz }
    val averageTicket = if (sales.isNotEmpty()) totalRevenue / sales.size else 0.0
    val topProduct = sales.groupBy { it.product }.maxByOrNull { it.value.sumOf { s -> s.quantity } }?.key ?: "Nenhum"

    // Form states for adding sale
    var product by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unitPrice by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Multicaixa Express") }
    var paymentExpanded by remember { mutableStateOf(false) }

    val paymentOptions = listOf(
        "Multicaixa Express", "Transferência Bancária", "Dinheiro no Ato", "TPA / Cartão"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Minhas Vendas & Lucro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Controle financeiro simples e análise com IA",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddSaleDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                    modifier = Modifier.testTag("add_sale_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nova Venda")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // KPI Financial Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FinancialKpiCard(
                    title = "Faturamento Total",
                    value = AppRepository.formatKz(totalRevenue),
                    color = BrandPrimaryBlue,
                    icon = Icons.Default.MonetizationOn,
                    modifier = Modifier.weight(1f)
                )
                FinancialKpiCard(
                    title = "Lucro Líquido",
                    value = AppRepository.formatKz(totalProfit),
                    color = BrandSuccessGreen,
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FinancialKpiCard(
                    title = "Ticket Médio",
                    value = AppRepository.formatKz(averageTicket),
                    color = BrandPurpleAi,
                    icon = Icons.Default.Receipt,
                    modifier = Modifier.weight(1f)
                )
                FinancialKpiCard(
                    title = "Produto Estrela",
                    value = topProduct,
                    color = BrandGoldKz,
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Analysis Trigger Button
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandPurpleAi.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, BrandPurpleAi.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Diagnóstico Financeiro com IA",
                            fontWeight = FontWeight.Bold,
                            color = BrandPurpleAi
                        )
                        Text(
                            text = "A IA analisa suas vendas, custos e sugere onde você pode lucrar mais em Angola.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            viewModel.analyzeSalesWithAi { analysis ->
                                aiAnalysisResult = analysis
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurpleAi),
                        modifier = Modifier.testTag("analyze_sales_ai_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analisar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // AI Analysis Result Display if triggered
        if (aiAnalysisResult != null) {
            item {
                Text(
                    text = "RELATÓRIO DE ANÁLISE DE VENDAS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandPurpleAi,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                ResultCard(
                    title = "Recomendações Estratégicas para o Negócio",
                    content = aiAnalysisResult!!
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Sales List Header
        item {
            Text(
                text = "HISTÓRICO DE VENDAS (${sales.size})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (sales.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = BrandPrimaryBlue, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nenhuma venda registrada ainda.", fontWeight = FontWeight.Bold)
                        Text("Clique em 'Nova Venda' para registrar seus recebimentos em Kz.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(sales) { sale ->
                SaleItemCard(sale = sale, onDelete = { viewModel.deleteSale(sale.id) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Add Sale Modal Dialog
    if (showAddSaleDialog) {
        AlertDialog(
            onDismissRequest = { showAddSaleDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = BrandPrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Venda")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = product,
                        onValueChange = { product = it },
                        label = { Text("Produto ou Serviço") },
                        placeholder = { Text("Ex: Vestido Festa Kilamba") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Qtd") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = unitPrice,
                            onValueChange = { unitPrice = it },
                            label = { Text("Preço Venda (Kz)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(2f).padding(vertical = 4.dp)
                        )
                    }

                    OutlinedTextField(
                        value = costPrice,
                        onValueChange = { costPrice = it },
                        label = { Text("Preço Custo (Kz)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Nome do Cliente") },
                        placeholder = { Text("Ex: Sr. Francisco") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = paymentExpanded,
                        onExpandedChange = { paymentExpanded = !paymentExpanded },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Forma de Pagamento") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = paymentExpanded,
                            onDismissRequest = { paymentExpanded = false }
                        ) {
                            paymentOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        paymentMethod = opt
                                        paymentExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = quantity.toIntOrNull() ?: 1
                        val unit = unitPrice.toDoubleOrNull() ?: 0.0
                        val cost = costPrice.toDoubleOrNull() ?: 0.0
                        if (product.isNotBlank() && unit > 0) {
                            viewModel.addSale(
                                product = product,
                                quantity = qty,
                                unitPriceKz = unit,
                                costPriceKz = cost,
                                clientName = clientName.ifBlank { "Cliente Balcão" },
                                paymentMethod = paymentMethod
                            )
                            // Reset
                            product = ""
                            unitPrice = ""
                            costPrice = ""
                            clientName = ""
                            showAddSaleDialog = false
                        } else {
                            viewModel.showSnackbar("Preencha o produto e o preço de venda.")
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text("Salvar Venda")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSaleDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SaleItemCard(sale: SaleEntity, onDelete: () -> Unit) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(sale.dateTimestamp))

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
                    Text(
                        text = sale.product,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cliente: ${sale.clientName} • $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Remover",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandPrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${sale.quantity}x ${AppRepository.formatKz(sale.unitPriceKz)} (${sale.paymentMethod})",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandPrimaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = AppRepository.formatKz(sale.totalPriceKz),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Lucro: +${AppRepository.formatKz(sale.profitKz)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandSuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialKpiCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
