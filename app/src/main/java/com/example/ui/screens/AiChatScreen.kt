package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ConversationEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val conversations by viewModel.conversations.collectAsState()
    val activeConversationId by viewModel.activeConversationId.collectAsState()
    val messages by viewModel.activeChatMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var showConversationsDrawer by remember { mutableStateOf(false) }
    var renameDialogConv by remember { mutableStateOf<ConversationEntity?>(null) }
    var newTitleInput by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "Como vender mais pelo WhatsApp em Luanda?",
        "Como lidar com clientes que pedem fiado?",
        "Qual é a melhor estratégia de entrega e motoboy?",
        "Como divulgar promoções com Multicaixa Express?",
        "Como calcular a margem de lucro em Kwanza?"
    )

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Header Bar
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showConversationsDrawer = true },
                        modifier = Modifier.testTag("chat_history_button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Histórico de conversas")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Assistente Empresarial IA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (profile != null) "Contexto: ${profile!!.businessName} (${profile!!.businessType})" else "Consultor Angolano",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandPrimaryBlue,
                            maxLines = 1
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.startNewConversation() },
                    modifier = Modifier.testTag("new_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddComment,
                        contentDescription = "Nova Conversa",
                        tint = BrandPrimaryBlue
                    )
                }
            }
        }

        // Quick Suggestions Row (if no messages or few)
        if (messages.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(BrandPrimaryBlue, BrandPurpleAi))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Como posso ajudar o seu negócio hoje?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pergunte sobre estratégias de vendas, precificação em Kz, atendimento no WhatsApp ou atração de clientes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "SUGESTÕES RÁPIDAS:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimaryBlue
                )
                Spacer(modifier = Modifier.height(10.dp))

                quickQuestions.forEach { question ->
                    OutlinedButton(
                        onClick = {
                            viewModel.sendChatMessage(question)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(question, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        } else {
            // Chat Messages LazyColumn
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { message ->
                    ChatMessageBubble(message = message)
                }

                if (isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = BrandPrimaryBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "O assistente está pensando...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Input Field Bar
        Surface(
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Escreva a sua mensagem...") },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank() && !isGenerating) {
                            val msg = textInput
                            textInput = ""
                            viewModel.sendChatMessage(msg)
                        }
                    },
                    enabled = textInput.isNotBlank() && !isGenerating,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (textInput.isNotBlank()) BrandPrimaryBlue else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = if (textInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Conversations Modal Sheet / Dialog
    if (showConversationsDrawer) {
        AlertDialog(
            onDismissRequest = { showConversationsDrawer = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Forum, contentDescription = null, tint = BrandPrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Conversas Salvas")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.startNewConversation()
                            showConversationsDrawer = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Iniciar Nova Conversa")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (conversations.isEmpty()) {
                        Text("Nenhuma conversa salva ainda.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(conversations) { conv ->
                                val isSelected = conv.id == activeConversationId
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) BrandPrimaryBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.openConversation(conv.id)
                                            showConversationsDrawer = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = conv.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    renameDialogConv = conv
                                                    newTitleInput = conv.title
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Outlined.Edit, contentDescription = "Renomear", modifier = Modifier.size(16.dp))
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteConversation(conv.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Outlined.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showConversationsDrawer = false }) {
                    Text("Fechar")
                }
            }
        )
    }

    // Rename Dialog
    if (renameDialogConv != null) {
        AlertDialog(
            onDismissRequest = { renameDialogConv = null },
            title = { Text("Renomear Conversa") },
            text = {
                OutlinedTextField(
                    value = newTitleInput,
                    onValueChange = { newTitleInput = it },
                    label = { Text("Título da Conversa") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newTitleInput.isNotBlank()) {
                        viewModel.renameConversation(renameDialogConv!!.id, newTitleInput.trim())
                    }
                    renameDialogConv = null
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameDialogConv = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageEntity) {
    val isUser = message.role == "user"
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) BrandPrimaryBlue else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(14.dp)
        ) {
            Column {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BrandPrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Assistente IA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimaryBlue
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Mensagem IA", message.content))
                                Toast.makeText(context, "Copiado!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copiar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
