package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    viewModel: AppViewModel,
    currentUser: UserEntity?,
    isDarkMode: Boolean
) {
    var showMenu by remember { mutableStateOf(false) }
    var showNotifs by remember { mutableStateOf(false) }
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.DASHBOARD) }
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(BrandPrimaryBlue, BrandPurpleAi)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Negócio IA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandGoldKz.copy(alpha = 0.2f),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "Kz 🇦🇴",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandGoldKz,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Seu negócio mais inteligente com IA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            if (currentUser != null) {
                // Credits Pill
                CreditBadge(
                    credits = currentUser.credits,
                    onClick = { viewModel.navigateTo(AppScreen.PRICING) }
                )

                // Notifications Icon with Badge
                IconButton(
                    onClick = {
                        showNotifs = !showNotifs
                        viewModel.markNotificationsAsRead()
                    },
                    modifier = Modifier.testTag("notifications_icon")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("$unreadCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Dark / Light toggle
            IconButton(
                onClick = { viewModel.toggleDarkMode() },
                modifier = Modifier.testTag("theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    contentDescription = "Tema",
                    tint = if (isDarkMode) BrandGoldKz else MaterialTheme.colorScheme.onSurface
                )
            }

            // User dropdown menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.testTag("user_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    if (currentUser != null) {
                        DropdownMenuItem(
                            text = { Text("Meu Perfil & Negócio") },
                            leadingIcon = { Icon(Icons.Outlined.Store, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.navigateTo(AppScreen.PROFILE)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Orçamentos & Propostas") },
                            leadingIcon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.navigateTo(AppScreen.DOCUMENTS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Modelos Prontos") },
                            leadingIcon = { Icon(Icons.Outlined.CollectionsBookmark, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.navigateTo(AppScreen.TEMPLATES)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Planos & Créditos") },
                            leadingIcon = { Icon(Icons.Outlined.Payments, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.navigateTo(AppScreen.PRICING)
                            }
                        )

                        if (currentUser.role == "ADMIN" || currentUser.role == "SUPERADMIN") {
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Painel Administrativo ⚙️", fontWeight = FontWeight.Bold, color = BrandPrimaryBlue) },
                                leadingIcon = { Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = BrandPrimaryBlue) },
                                onClick = {
                                    showMenu = false
                                    viewModel.navigateTo(AppScreen.ADMIN)
                                }
                            )
                        }

                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Página Inicial / Landing") },
                            leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.navigateTo(AppScreen.LANDING)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Terminar Sessão (Sair)") },
                            leadingIcon = { Icon(Icons.Outlined.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                viewModel.logout()
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Iniciar Sessão / Registar") },
                            leadingIcon = { Icon(Icons.Outlined.Login, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.navigateTo(AppScreen.AUTH)
                            }
                        )
                    }
                }
            }
        }
    )

    // Notification Dropdown Dialog
    if (showNotifs) {
        AlertDialog(
            onDismissRequest = { showNotifs = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = BrandPrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notificações do Negócio")
                }
            },
            text = {
                if (notifications.isEmpty()) {
                    Text("Nenhuma notificação nova no momento.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                    ) {
                        notifications.take(8).forEach { notif ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimaryBlue
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotifs = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}
