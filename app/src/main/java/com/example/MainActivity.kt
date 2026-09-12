package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AiProcessingDialog
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = viewModel()
            val isDarkMode by appViewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                MainAppContent(viewModel = appViewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AppViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationStatus by viewModel.generationStatus.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(snackbarMsg) {
        if (snackbarMsg != null) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = snackbarMsg!!,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearSnackbar()
            }
        }
    }

    val showBottomNav = currentScreen in listOf(
        AppScreen.DASHBOARD,
        AppScreen.AI_CHAT,
        AppScreen.CREATE_TOOLS,
        AppScreen.SALES,
        AppScreen.DOCUMENTS
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                viewModel = viewModel,
                currentUser = currentUser,
                isDarkMode = isDarkMode
            )
        },
        bottomBar = {
            if (showBottomNav) {
                AppBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.LANDING -> LandingScreen(viewModel = viewModel)
                    AppScreen.AUTH -> AuthScreen(viewModel = viewModel)
                    AppScreen.ONBOARDING -> OnboardingScreen(viewModel = viewModel)
                    AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    AppScreen.AI_CHAT -> AiChatScreen(viewModel = viewModel)
                    AppScreen.CREATE_TOOLS -> CreateContentScreen(viewModel = viewModel)
                    AppScreen.SALES -> SalesScreen(viewModel = viewModel)
                    AppScreen.DOCUMENTS -> DocumentsScreen(viewModel = viewModel)
                    AppScreen.TEMPLATES -> TemplatesScreen(viewModel = viewModel)
                    AppScreen.PRICING -> PricingPlansScreen(viewModel = viewModel)
                    AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
                    AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                }
            }

            if (isGenerating) {
                AiProcessingDialog(statusText = generationStatus)
            }
        }
    }
}
