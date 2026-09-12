package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimaryBlue
import com.example.ui.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AppBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    val navItems = listOf(
        NavItem(
            screen = AppScreen.DASHBOARD,
            label = "Início",
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard
        ),
        NavItem(
            screen = AppScreen.AI_CHAT,
            label = "Assistente",
            selectedIcon = Icons.Filled.SmartToy,
            unselectedIcon = Icons.Outlined.SmartToy
        ),
        NavItem(
            screen = AppScreen.CREATE_TOOLS,
            label = "Criar",
            selectedIcon = Icons.Filled.AutoAwesome,
            unselectedIcon = Icons.Outlined.AutoAwesome
        ),
        NavItem(
            screen = AppScreen.SALES,
            label = "Vendas",
            selectedIcon = Icons.Filled.TrendingUp,
            unselectedIcon = Icons.Outlined.TrendingUp
        ),
        NavItem(
            screen = AppScreen.DOCUMENTS,
            label = "Documentos",
            selectedIcon = Icons.Filled.ReceiptLong,
            unselectedIcon = Icons.Outlined.ReceiptLong
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("app_bottom_nav")
    ) {
        navItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = if (isSelected) BrandPrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BrandPrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = BrandPrimaryBlue.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("nav_item_${item.label.lowercase()}")
            )
        }
    }
}
