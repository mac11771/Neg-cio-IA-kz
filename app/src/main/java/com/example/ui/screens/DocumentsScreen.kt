package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.ProposalEntity
import com.example.data.repository.AppRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DocumentsScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val budgets by viewModel.budgets.collectAsState()
    val proposals by viewModel.proposals.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Orçamentos, 1: Propostas
    var showCreateBudgetDialog by remember { mutableStateOf(false) }
    var showCreateProposalDialog by remember { mutableStateOf(false) }
    var previewText by remember { mutableStateOf<String?>(null) }
    var previewTitle by remember { mutableStateOf("") }

    // Budget form states
    var bClientName by remember { mutableStateOf("") }
    var bClientPhone by remember { mutableStateOf("") }
    var bItemsSummary by remember { mutableStateOf("") }
    var bSubtotal by remember { mutableStateOf("") }
    var bDiscount by remember { mutableStateOf("0") }
    var bNotes by remember { mutableStateOf("Pagamento: 50% na aprovação via Multicaixa Express e 50% na entrega.") }

    // Proposal form states
    var pClientName by remember { mutableStateOf("") }
    var pClientContact by remember { mutableStateOf("") }
    var pProblem by remember { mutableStateOf("") }
    var pSolution by remember { mutableStateOf("") }
    var pServices by remember { mutableStateOf("") }
    var pTotalAmount by remember { mutableStateOf("") }
    var pDeadline by remember { mutableStateOf("7 a 10 dias úteis") }
    var pPaymentTerms by remember { mutableStateOf("50% entrada e 50% conclusão via Multicaixa Express / Bancária") }

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
                        text = "Documentos Comerciais",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Orçamentos e propostas formais em Kwanza (Kz)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        if (selectedTab == 0) showCreateBudgetDialog = true
                        else showCreateProposalDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                    modifier = Modifier.testTag("create_document_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (selectedTab == 0) "Novo Orçamento" else "Nova Proposta")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrandPrimaryBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Orçamentos (${budgets.size})", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Propostas (${proposals.size})", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Description, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display preview card if active
        if (previewText != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BrandPrimaryBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = previewTitle,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimaryBlue
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Documento", previewText))
                                        Toast.makeText(context, "Copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copiar")
                                }

                                IconButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, previewText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Enviar via WhatsApp"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Partilhar", tint = BrandSuccessGreen)
                                }

                                IconButton(onClick = { previewText = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Fechar")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = previewText!!,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // Budgets list
            if (budgets.isEmpty()) {
                item {
                    EmptyDocumentsPlaceholder(
                        title = "Nenhum orçamento criado",
                        desc = "Clique em 'Novo Orçamento' para gerar seu primeiro documento formal com valores em Kz."
                    )
                }
            } else {
                items(budgets) { budget ->
                    BudgetItemCard(
                        budget = budget,
                        bizName = profile?.businessName ?: "Nossa Empresa",
                        onPreview = { text, title ->
                            previewText = text
                            previewTitle = title
                        },
                        onDelete = { viewModel.deleteBudget(budget.id) }
                    )
                }
            }
        } else {
            // Proposals list
            if (proposals.isEmpty()) {
                item {
                    EmptyDocumentsPlaceholder(
                        title = "Nenhuma proposta comercial criada",
                        desc = "Gere propostas completas com apresentação, entendimento do problema e valores para clientes corporativos."
                    )
                }
            } else {
                items(proposals) { proposal ->
                    ProposalItemCard(
                        proposal = proposal,
                        bizName = profile?.businessName ?: "Nossa Empresa",
                        onPreview = { text, title ->
                            previewText = text
                            previewTitle = title
                        },
                        onDelete = { viewModel.deleteProposal(proposal.id) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Create Budget Dialog
    if (showCreateBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showCreateBudgetDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = BrandPrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Criar Novo Orçamento")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = bClientName,
                        onValueChange = { bClientName = it },
                        label = { Text("Nome do Cliente") },
                        placeholder = { Text("Ex: Sr. Gaspar Miranda") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = bClientPhone,
                        onValueChange = { bClientPhone = it },
                        label = { Text("WhatsApp do Cliente") },
                        placeholder = { Text("Ex: 923 000 000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = bItemsSummary,
                        onValueChange = { bItemsSummary = it },
                        label = { Text("Itens / Serviços (Discriminação)") },
                        placeholder = { Text("Ex: 2x Vestidos Seda (50.000 Kz) + Entrega (3.000 Kz)") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        minLines = 2
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bSubtotal,
                            onValueChange = { bSubtotal = it },
                            label = { Text("Subtotal (Kz)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = bDiscount,
                            onValueChange = { bDiscount = it },
                            label = { Text("Desconto (Kz)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                        )
                    }
                    OutlinedTextField(
                        value = bNotes,
                        onValueChange = { bNotes = it },
                        label = { Text("Condições de Pagamento & Notas") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sub = bSubtotal.toDoubleOrNull() ?: 0.0
                        val disc = bDiscount.toDoubleOrNull() ?: 0.0
                        if (bClientName.isNotBlank() && sub > 0) {
                            viewModel.createBudget(
                                clientName = bClientName,
                                clientPhone = bClientPhone,
                                itemsSummary = bItemsSummary,
                                subtotalKz = sub,
                                discountKz = disc,
                                taxKz = 0.0,
                                notes = bNotes
                            ) {
                                showCreateBudgetDialog = false
                                bClientName = ""
                                bSubtotal = ""
                                bItemsSummary = ""
                            }
                        } else {
                            viewModel.showSnackbar("Preencha o nome do cliente e o subtotal em Kz.")
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text("Gerar Orçamento")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateBudgetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Create Proposal Dialog
    if (showCreateProposalDialog) {
        AlertDialog(
            onDismissRequest = { showCreateProposalDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = BrandPurpleAi)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nova Proposta Comercial")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = pClientName,
                        onValueChange = { pClientName = it },
                        label = { Text("Nome do Cliente / Empresa") },
                        placeholder = { Text("Ex: Clínica Dentária Kilamba") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = pProblem,
                        onValueChange = { pProblem = it },
                        label = { Text("Necessidade / Problema do Cliente") },
                        placeholder = { Text("Ex: Falta de presença no Instagram e agendamento de consultas") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = pSolution,
                        onValueChange = { pSolution = it },
                        label = { Text("Solução Proposta") },
                        placeholder = { Text("Ex: Gestão completa de redes sociais e anúncios pagos no WhatsApp") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = pTotalAmount,
                        onValueChange = { pTotalAmount = it },
                        label = { Text("Investimento Total (Kz)") },
                        placeholder = { Text("Ex: 180.000 Kz") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = pDeadline,
                        onValueChange = { pDeadline = it },
                        label = { Text("Prazo de Execução") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = pTotalAmount.toDoubleOrNull() ?: 0.0
                        if (pClientName.isNotBlank() && amount > 0) {
                            viewModel.createProposal(
                                clientName = pClientName,
                                clientPhone = pClientContact,
                                presentation = "Apresentamos a proposta de prestação de serviços com foco em resultados.",
                                problem = pProblem,
                                solution = pSolution,
                                services = pSolution,
                                totalAmountKz = amount,
                                deadline = pDeadline,
                                paymentTerms = pPaymentTerms,
                                terms = "Validade de 30 dias a partir da data de emissão."
                            ) {
                                showCreateProposalDialog = false
                                pClientName = ""
                                pTotalAmount = ""
                                pProblem = ""
                                pSolution = ""
                            }
                        } else {
                            viewModel.showSnackbar("Preencha o cliente e o valor do investimento.")
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurpleAi)
                ) {
                    Text("Gerar Proposta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateProposalDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun BudgetItemCard(
    budget: BudgetEntity,
    bizName: String,
    onPreview: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(budget.createdAt))

    val formattedContent = """
📄 ORÇAMENTO COMERCIAL: ${budget.budgetNumber}
🏢 Emitido por: $bizName
👤 Cliente: ${budget.clientName}
📅 Data: $dateStr | ⏳ Validade: ${budget.validityDays} dias

📋 ITENS & SERVIÇOS:
${budget.itemsSummary}

💰 VALORES:
• Subtotal: ${AppRepository.formatKz(budget.subtotalKz)}
• Desconto: -${AppRepository.formatKz(budget.discountKz)}
• TOTAL FINAL: ${AppRepository.formatKz(budget.totalKz)}

📌 CONDIÇÕES:
${budget.notes}
Aceitamos pagamentos via Multicaixa Express e Transferência Bancária.
""".trimIndent()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(budget.budgetNumber, fontWeight = FontWeight.Bold, color = BrandPrimaryBlue)
                    Text("Cliente: ${budget.clientName} • $dateStr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(
                    text = AppRepository.formatKz(budget.totalKz),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onPreview(formattedContent, "Orçamento ${budget.budgetNumber}") },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver & Enviar no WhatsApp", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ProposalItemCard(
    proposal: ProposalEntity,
    bizName: String,
    onPreview: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(proposal.createdAt))

    val formattedContent = """
📑 PROPOSTA COMERCIAL: ${proposal.proposalNumber}
🏢 Empresa: $bizName
👤 Para: ${proposal.clientName}
📅 Data: $dateStr

1. APRESENTAÇÃO
${proposal.presentation}

2. DESAFIO IDENTIFICADO
${proposal.problem}

3. SOLUÇÃO PROPOSTA & ESCOPO
${proposal.solution}

4. PRAZO DE EXECUÇÃO
${proposal.deadline}

5. INVESTIMENTO & FORMAS DE PAGAMENTO
Valor Total: ${AppRepository.formatKz(proposal.totalAmountKz)}
Condições: ${proposal.paymentTerms}

6. TERMOS & VALIDADE
${proposal.terms}
""".trimIndent()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BrandPurpleAi.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(proposal.proposalNumber, fontWeight = FontWeight.Bold, color = BrandPurpleAi)
                    Text("Para: ${proposal.clientName} • $dateStr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(
                    text = AppRepository.formatKz(proposal.totalAmountKz),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onPreview(formattedContent, "Proposta ${proposal.proposalNumber}") },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver Proposta", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun EmptyDocumentsPlaceholder(title: String, desc: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.FolderOpen, contentDescription = null, tint = BrandPrimaryBlue, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
