package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    LANDING,
    AUTH,
    ONBOARDING,
    DASHBOARD,
    AI_CHAT,
    CREATE_TOOLS,
    SALES,
    DOCUMENTS,
    TEMPLATES,
    PRICING,
    ADMIN,
    PROFILE
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AppRepository(application)

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStatus = MutableStateFlow("A IA está trabalhando na sua solicitação...")
    val generationStatus: StateFlow<String> = _generationStatus.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // Active conversation in Chat
    private val _activeConversationId = MutableStateFlow<Long?>(null)
    val activeConversationId: StateFlow<Long?> = _activeConversationId.asStateFlow()

    // Sub-tool selection in Create Hub
    private val _activeCreateTool = MutableStateFlow("POST_FB")
    val activeCreateTool: StateFlow<String> = _activeCreateTool.asStateFlow()

    // Reactive lists
    val businessProfile: StateFlow<BusinessProfileEntity?> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getBusinessProfileFlow(user.id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val conversations: StateFlow<List<ConversationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getConversationsFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = _activeConversationId.flatMapLatest { convId ->
        if (convId != null) repository.getMessagesFlow(convId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val generations: StateFlow<List<AiGenerationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getGenerationsFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<SaleEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getSalesFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getBudgetsFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proposals: StateFlow<List<ProposalEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getProposalsFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getNotificationsFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: StateFlow<List<TemplateEntity>> = repository.getAllTemplatesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDefaultData()
            // Auto login with default demo user on first start
            val users = repository.getAllUsersFlow().first()
            val defaultUser = users.firstOrNull { it.role != "ADMIN" } ?: users.firstOrNull()
            if (defaultUser != null) {
                _currentUser.value = defaultUser
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key.trim()
        showSnackbar("Chave API salva.")
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun selectCreateTool(tool: String) {
        _activeCreateTool.value = tool
        _currentScreen.value = AppScreen.CREATE_TOOLS
    }

    // Authentication
    fun register(
        fullName: String,
        businessName: String,
        phone: String,
        email: String,
        password: String,
        province: String,
        municipality: String,
        businessType: String
    ) {
        viewModelScope.launch {
            val result = repository.register(
                fullName, businessName, phone, email, password, province, municipality, businessType
            )
            result.onSuccess { user ->
                _currentUser.value = user
                _currentScreen.value = AppScreen.ONBOARDING
                showSnackbar("Conta criada com sucesso! Bem-vindo.")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Erro ao criar conta.")
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _currentScreen.value = AppScreen.DASHBOARD
                showSnackbar("Bem-vindo de volta, ${user.fullName}!")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Falha ao entrar.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = AppScreen.LANDING
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
        _currentScreen.value = AppScreen.DASHBOARD
        showSnackbar("Alternado para ${user.fullName} (${user.role})")
    }

    fun completeOnboarding(
        businessName: String,
        businessType: String,
        productsOrServices: String,
        targetAudience: String,
        province: String,
        municipality: String,
        mainGoal: String,
        socialMedia: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val profile = BusinessProfileEntity(
                userId = user.id,
                businessName = businessName,
                businessType = businessType,
                productsOrServices = productsOrServices,
                targetAudience = targetAudience,
                province = province,
                municipality = municipality,
                mainGoal = mainGoal,
                socialMedia = socialMedia,
                customBio = "Negócio em $province ($municipality) especializado em $businessType com foco em $mainGoal."
            )
            repository.saveBusinessProfile(profile)
            _currentScreen.value = AppScreen.DASHBOARD
            showSnackbar("Perfil empresarial configurado com sucesso! 🎉")
        }
    }

    fun updateProfile(profile: BusinessProfileEntity) {
        viewModelScope.launch {
            repository.saveBusinessProfile(profile)
            showSnackbar("Perfil atualizado.")
        }
    }

    // AI Generation
    fun generateContent(
        toolType: String,
        title: String,
        inputs: Map<String, String>,
        credits: Int = 2,
        onSuccess: (String) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isGenerating.value = true
            _generationStatus.value = "A IA está trabalhando na sua solicitação..."
            val result = repository.executeAiGeneration(
                userId = user.id,
                toolType = toolType,
                title = title,
                inputs = inputs,
                creditsRequired = credits,
                customApiKey = _customApiKey.value
            )
            _isGenerating.value = false
            result.onSuccess { output ->
                refreshUser()
                onSuccess(output)
                showSnackbar("Conteúdo gerado com sucesso!")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Não foi possível gerar o conteúdo agora. Tente novamente.")
            }
        }
    }

    // Chat
    fun openConversation(convId: Long) {
        _activeConversationId.value = convId
    }

    fun startNewConversation(title: String = "Nova Conversa") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val convId = repository.createConversation(user.id, title)
            _activeConversationId.value = convId
        }
    }

    fun sendChatMessage(message: String) {
        val user = _currentUser.value ?: return
        var convId = _activeConversationId.value
        viewModelScope.launch {
            if (convId == null) {
                convId = repository.createConversation(user.id, message.take(30))
                _activeConversationId.value = convId
            }
            _isGenerating.value = true
            _generationStatus.value = "Assistente IA está digitando..."
            val result = repository.sendChatMessage(
                userId = user.id,
                conversationId = convId!!,
                userMessage = message,
                customApiKey = _customApiKey.value
            )
            _isGenerating.value = false
            result.onSuccess {
                refreshUser()
            }.onFailure { err ->
                showSnackbar(err.message ?: "Erro ao enviar mensagem.")
            }
        }
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_activeConversationId.value == id) {
                _activeConversationId.value = null
            }
            showSnackbar("Conversa excluída.")
        }
    }

    fun renameConversation(id: Long, newTitle: String) {
        viewModelScope.launch {
            repository.renameConversation(id, newTitle)
            showSnackbar("Conversa renomeada.")
        }
    }

    fun toggleConversationFavorite(id: Long, isFav: Boolean) {
        viewModelScope.launch {
            repository.setConversationFavorite(id, isFav)
        }
    }

    // Sales
    fun addSale(
        product: String,
        quantity: Int,
        unitPriceKz: Double,
        costPriceKz: Double,
        clientName: String,
        paymentMethod: String,
        notes: String = ""
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addSale(
                user.id, product, quantity, unitPriceKz, costPriceKz, clientName, paymentMethod, notes
            )
            showSnackbar("Venda registrada com sucesso! 💰")
        }
    }

    fun deleteSale(saleId: Long) {
        viewModelScope.launch {
            repository.deleteSale(saleId)
            showSnackbar("Venda removida.")
        }
    }

    fun analyzeSalesWithAi(onResult: (String) -> Unit) {
        val currentSales = sales.value
        val totalRev = currentSales.sumOf { it.totalPriceKz }
        val totalCost = currentSales.sumOf { it.costPriceKz * it.quantity }
        val totalProfit = totalRev - totalCost
        val topProduct = currentSales.groupBy { it.product }
            .maxByOrNull { it.value.sumOf { s -> s.quantity } }?.key ?: "Diversos"

        val summary = """
Total de vendas registradas: ${currentSales.size}
Receita Bruta: ${AppRepository.formatKz(totalRev)}
Custos Totais: ${AppRepository.formatKz(totalCost)}
Lucro Líquido: ${AppRepository.formatKz(totalProfit)}
Produto mais vendido: $topProduct
Formas de pagamento mais usadas: ${currentSales.groupBy { it.paymentMethod }.map { "${it.key}: ${it.value.size}" }.joinToString(", ")}
""".trimIndent()

        generateContent(
            toolType = "SALES_ANALYSIS",
            title = "Análise Inteligente de Vendas",
            inputs = mapOf("salesSummary" to summary),
            credits = 3,
            onSuccess = onResult
        )
    }

    // Budgets (Orçamentos)
    fun createBudget(
        clientName: String,
        clientPhone: String,
        itemsSummary: String,
        subtotalKz: Double,
        discountKz: Double,
        taxKz: Double,
        notes: String,
        validityDays: Int = 15,
        onCreated: (BudgetEntity) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val budget = repository.createBudget(
                user.id, clientName, clientPhone, itemsSummary, subtotalKz, discountKz, taxKz, notes, validityDays
            )
            onCreated(budget)
            showSnackbar("Orçamento ${budget.budgetNumber} criado!")
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            repository.deleteBudget(id)
            showSnackbar("Orçamento removido.")
        }
    }

    // Proposals (Propostas Comerciais)
    fun createProposal(
        clientName: String,
        clientPhone: String,
        presentation: String,
        problem: String,
        solution: String,
        services: String,
        totalAmountKz: Double,
        deadline: String,
        paymentTerms: String,
        terms: String,
        onCreated: (ProposalEntity) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val proposal = repository.createProposal(
                user.id, clientName, clientPhone, presentation, problem, solution, services,
                totalAmountKz, deadline, paymentTerms, terms
            )
            onCreated(proposal)
            showSnackbar("Proposta ${proposal.proposalNumber} criada!")
        }
    }

    fun deleteProposal(id: Long) {
        viewModelScope.launch {
            repository.deleteProposal(id)
            showSnackbar("Proposta removida.")
        }
    }

    // Credits & Plans
    fun buyCreditPackage(amount: Int, priceKz: Double) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addCredits(user.id, amount, "Compra de pacote (${AppRepository.formatKz(priceKz)}) via Multicaixa Express")
            refreshUser()
            showSnackbar("Pagamento aprovado! +$amount créditos adicionados.")
        }
    }

    fun upgradePlan(planName: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val bonusCredits = when (planName) {
                "PREMIUM" -> 150
                "BUSINESS" -> 500
                else -> 0
            }
            repository.updateUserPlan(user.id, planName)
            if (bonusCredits > 0) {
                repository.addCredits(user.id, bonusCredits, "Bónus de subscrição do Plano $planName")
            }
            refreshUser()
            showSnackbar("Subscrição do Plano $planName ativada!")
        }
    }

    fun markNotificationsAsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markNotificationsAsRead(user.id)
        }
    }

    fun deleteGeneration(id: Long) {
        viewModelScope.launch {
            repository.deleteGeneration(id)
            showSnackbar("Registro de histórico removido.")
        }
    }

    fun toggleGenerationFavorite(id: Long, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleGenerationFavorite(id, isFav)
        }
    }

    fun adminAddCredits(userId: Long, amount: Int, reason: String) {
        viewModelScope.launch {
            repository.addCredits(userId, amount, reason)
            refreshUser()
            showSnackbar("Foram adicionados $amount créditos ao usuário.")
        }
    }

    private fun refreshUser() {
        val current = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = repository.getUserById(current.id)
            if (updated != null) {
                _currentUser.value = updated
            }
        }
    }
}
