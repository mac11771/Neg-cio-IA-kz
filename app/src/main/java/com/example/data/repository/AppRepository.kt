package com.example.data.repository

import android.content.Context
import com.example.data.ai.AngolaBusinessAiEngine
import com.example.data.local.AppDatabase
import com.example.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class AppRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val profileDao = db.businessProfileDao()
    private val conversationDao = db.conversationDao()
    private val messageDao = db.chatMessageDao()
    private val aiGenDao = db.aiGenerationDao()
    private val saleDao = db.saleDao()
    private val budgetDao = db.budgetDao()
    private val proposalDao = db.proposalDao()
    private val notificationDao = db.notificationDao()
    private val creditTxDao = db.creditTransactionDao()
    private val templateDao = db.templateDao()

    companion object {
        private val kzFormatter: DecimalFormat by lazy {
            val symbols = DecimalFormatSymbols(Locale.GERMAN).apply {
                groupingSeparator = '.'
                decimalSeparator = ','
            }
            DecimalFormat("#,##0", symbols)
        }

        fun formatKz(amount: Double): String {
            return "${kzFormatter.format(amount)} Kz"
        }
    }

    suspend fun initializeDefaultData() = withContext(Dispatchers.IO) {
        // Seed default templates if empty
        if (templateDao.countTemplates() == 0) {
            val templates = listOf(
                TemplateEntity(
                    category = "Loja de Roupas",
                    toolType = "POST_FB",
                    title = "Post de Coleção Nova & Tendências",
                    description = "Ideal para boutiques e lojas de vestuário em Luanda e províncias",
                    content = "🔥 NOVA COLEÇÃO DISPONÍVEL!\nChegaram as peças mais elegantes da estação. Tecidos frescos, caimento impecável e preços em Kz que cabem no seu bolso.\n👗 Tamanhos do 36 ao 46.\n🛵 Entregamos em Luanda e despachamos para as províncias.\n📲 Reserve pelo WhatsApp: 923 000 000\n💳 Aceitamos Multicaixa Express!"
                ),
                TemplateEntity(
                    category = "Restaurante",
                    toolType = "POST_FB",
                    title = "Cardápio do Dia / Almoço Executivo",
                    description = "Divulgação de pratos típicos angolanos e menu executivo",
                    content = "🍲 HOJE TEM COMIDA COM SABOR DE CASA!\nPrato do Dia: Muamba de Galinha com Funge e Calulu de Peixe Fresco.\n🍴 Acompanha sobremesa e bebida gelada.\n📍 Visite-nos ou peça já o seu take-away!\n📞 WhatsApp encomendas: 912 000 000"
                ),
                TemplateEntity(
                    category = "Salão & Barbearia",
                    toolType = "SLOGAN",
                    title = "Slogan & Promoção de Estética",
                    description = "Foco em beleza, tranças, cortes modernos e cuidados capilares",
                    content = "💇‍♀️ 'A sua autoestima no mais alto nível!'\nAgende a sua sessão de tranças, alisamento ou barba desenhada com nossos mestres do estilo.\n✨ Atendimento vip com ambiente climatizado."
                ),
                TemplateEntity(
                    category = "Designer & Fotógrafo",
                    toolType = "PROPOSAL",
                    title = "Proposta de Cobertura de Eventos / Casamentos",
                    description = "Modelo para orçamentos rápidos de fotografia e identidade visual",
                    content = "📸 PROPOSTA DE FOTOGRAFIA & VÍDEO HD\n• Cobertura completa de 6 horas\n• Álbum encadernado de luxo + 150 fotos tratadas\n• Entrega rápida em Pen Drive e nuvem\n💰 Valor Promocional: 280.000 Kz\n💳 Sinal de 50% via Multicaixa Express para reservar data."
                ),
                TemplateEntity(
                    category = "Informática & Serviços",
                    toolType = "CLIENT_REPLY",
                    title = "Resposta de Assistência Técnica & Suporte",
                    description = "Mensagem para clientes que pedem orçamento de reparação ou software",
                    content = "Olá! 👋 O diagnóstico do seu equipamento é gratuito. Fazemos formatação, troca de ecrãs, recuperação de ficheiros e instalação de antivírus.\n⏱️ Prazo de entrega: 24h a 48h.\n📍 Localização central em Luanda com garantia de 90 dias."
                ),
                TemplateEntity(
                    category = "Venda de Alimentos",
                    toolType = "AD",
                    title = "Anúncio de Cabazes & Produtos da Cesta Básica",
                    description = "Preço direto ao consumidor para alimentos frescos e cestas",
                    content = "📦 CABAZ ECONÓMICO DA FAMÍLIA!\nArroz, óleo, açúcar, feijão e farinha com os melhores preços de Luanda.\n🚚 Entrega grátis para compras acima de 40.000 Kz!\n💬 Peça no WhatsApp agora mesmo."
                )
            )
            templateDao.insertAll(templates)
        }

        // Seed demo accounts if no users exist
        val existingUsers = userDao.getAllUsers()
        if (existingUsers.isEmpty()) {
            val demoUserId = userDao.insertUser(
                UserEntity(
                    fullName = "Manuel dos Santos",
                    businessName = "Boutique Kilamba Fashion",
                    phone = "923 456 789",
                    email = "manuel@kilamba.ao",
                    passwordHash = "123456",
                    province = "Luanda",
                    municipality = "Belas (Kilamba)",
                    businessType = "Loja de Roupas & Acessórios",
                    role = "USER",
                    currentPlan = "PREMIUM",
                    credits = 120
                )
            )

            profileDao.insertOrUpdateProfile(
                BusinessProfileEntity(
                    userId = demoUserId,
                    businessName = "Boutique Kilamba Fashion",
                    businessType = "Loja de Roupas & Calçados",
                    productsOrServices = "Vestuário casual, vestidos de festa, camisas executivas e calçados",
                    targetAudience = "Jovens profissionais e famílias da centralidade do Kilamba e arredores",
                    province = "Luanda",
                    municipality = "Belas",
                    mainGoal = "Aumentar as vendas pelo WhatsApp e fidelizar clientes da centralidade",
                    socialMedia = "Instagram (@kilamba_fashion) e WhatsApp Business",
                    customBio = "Boutique de referência no Kilamba com moda de qualidade para quem valoriza elegância e bom preço.",
                    phone = "923 456 789",
                    whatsapp = "923 456 789"
                )
            )

            // Seed demo sales
            saleDao.insertSale(
                SaleEntity(
                    userId = demoUserId,
                    product = "Vestido de Festa Seda Luanda",
                    quantity = 2,
                    unitPriceKz = 28000.0,
                    costPriceKz = 16000.0,
                    totalPriceKz = 56000.0,
                    profitKz = 24000.0,
                    clientName = "Dona Teresa Gonçalves",
                    paymentMethod = "Multicaixa Express",
                    dateTimestamp = System.currentTimeMillis() - 86400000L * 2
                )
            )
            saleDao.insertSale(
                SaleEntity(
                    userId = demoUserId,
                    product = "Camisa Executiva Slim Fit",
                    quantity = 3,
                    unitPriceKz = 15000.0,
                    costPriceKz = 8500.0,
                    totalPriceKz = 45000.0,
                    profitKz = 19500.0,
                    clientName = "Eng. Pedro Afonso",
                    paymentMethod = "Transferência Bancária",
                    dateTimestamp = System.currentTimeMillis() - 86400000L * 1
                )
            )
            saleDao.insertSale(
                SaleEntity(
                    userId = demoUserId,
                    product = "Conjunto Casual Linho",
                    quantity = 1,
                    unitPriceKz = 32000.0,
                    costPriceKz = 18000.0,
                    totalPriceKz = 32000.0,
                    profitKz = 14000.0,
                    clientName = "Dra. Beatriz Neto",
                    paymentMethod = "Multicaixa Express",
                    dateTimestamp = System.currentTimeMillis()
                )
            )

            // Seed demo notification
            notificationDao.insertNotification(
                NotificationEntity(
                    userId = demoUserId,
                    title = "Bem-vindo ao Negócio IA Kz! 🇦🇴",
                    message = "Você tem 120 créditos e o plano Premium ativo para acelerar suas vendas.",
                    type = "PLAN"
                )
            )

            // Also seed an Admin user
            userDao.insertUser(
                UserEntity(
                    fullName = "Administrador do Sistema",
                    businessName = "Negócio IA Kz Central",
                    phone = "924 999 000",
                    email = "admin@negocioia.ao",
                    passwordHash = "admin123",
                    province = "Luanda",
                    municipality = "Maianga",
                    businessType = "Tecnologia & IA",
                    role = "ADMIN",
                    currentPlan = "BUSINESS",
                    credits = 9999
                )
            )
        }
    }

    // Auth & Users
    suspend fun register(
        fullName: String,
        businessName: String,
        phone: String,
        email: String,
        password: String,
        province: String,
        municipality: String,
        businessType: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByEmail(email.trim().lowercase())
        if (existing != null) {
            return@withContext Result.failure(Exception("Este e-mail já está cadastrado."))
        }
        val user = UserEntity(
            fullName = fullName.trim(),
            businessName = businessName.trim(),
            phone = phone.trim(),
            email = email.trim().lowercase(),
            passwordHash = password,
            province = province.trim(),
            municipality = municipality.trim(),
            businessType = businessType.trim(),
            credits = 50,
            currentPlan = "FREE"
        )
        val id = userDao.insertUser(user)
        val createdUser = user.copy(id = id)

        // Create default business profile
        profileDao.insertOrUpdateProfile(
            BusinessProfileEntity(
                userId = id,
                businessName = businessName.trim(),
                businessType = businessType.trim(),
                productsOrServices = "",
                targetAudience = "",
                province = province.trim(),
                municipality = municipality.trim(),
                mainGoal = "Crescer vendas em Angola",
                socialMedia = "WhatsApp",
                phone = phone.trim(),
                whatsapp = phone.trim()
            )
        )

        // Welcome notification
        notificationDao.insertNotification(
            NotificationEntity(
                userId = id,
                title = "Conta criada com sucesso! 🎉",
                message = "Você recebeu 50 créditos gratuitos para começar a criar conteúdos e anúncios.",
                type = "CREDIT"
            )
        )

        Result.success(createdUser)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return@withContext Result.failure(Exception("Usuário não encontrado."))
        if (user.passwordHash != password) {
            return@withContext Result.failure(Exception("Palavra-passe incorreta."))
        }
        Result.success(user)
    }

    suspend fun getUserById(userId: Long): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()

    suspend fun deleteUser(userId: Long) = withContext(Dispatchers.IO) {
        userDao.deleteUser(userId)
    }

    suspend fun updateUserPlan(userId: Long, newPlan: String) = withContext(Dispatchers.IO) {
        userDao.updatePlan(userId, newPlan)
    }

    suspend fun addCredits(userId: Long, amount: Int, reason: String) = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext
        val newCredits = user.credits + amount
        userDao.updateCredits(userId, newCredits)
        creditTxDao.insertTransaction(
            CreditTransactionEntity(
                userId = userId,
                amount = amount,
                type = "PURCHASE",
                description = reason
            )
        )
        notificationDao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = "Créditos Adicionados! ⚡",
                message = "Foram adicionados $amount créditos à sua conta ($reason).",
                type = "CREDIT"
            )
        )
    }

    // Business Profile
    fun getBusinessProfileFlow(userId: Long): Flow<BusinessProfileEntity?> = profileDao.getProfileFlow(userId)

    suspend fun getBusinessProfile(userId: Long): BusinessProfileEntity? = withContext(Dispatchers.IO) {
        profileDao.getProfileByUserId(userId)
    }

    suspend fun saveBusinessProfile(profile: BusinessProfileEntity) = withContext(Dispatchers.IO) {
        profileDao.insertOrUpdateProfile(profile)
    }

    // AI Generation with Credit Tracking
    suspend fun executeAiGeneration(
        userId: Long,
        toolType: String,
        title: String,
        inputs: Map<String, String>,
        creditsRequired: Int = 2,
        customApiKey: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId)
            ?: return@withContext Result.failure(Exception("Usuário não encontrado."))

        if (user.credits < creditsRequired) {
            return@withContext Result.failure(
                IllegalStateException("Créditos insuficientes (${user.credits} disponíveis). Compre mais créditos para continuar.")
            )
        }

        val profile = profileDao.getProfileByUserId(userId)
        val generatedText = AngolaBusinessAiEngine.generateContent(
            toolType = toolType,
            profile = profile,
            inputs = inputs,
            customApiKey = customApiKey
        )

        // Deduct credits
        val newCredits = user.credits - creditsRequired
        userDao.updateCredits(userId, newCredits)
        creditTxDao.insertTransaction(
            CreditTransactionEntity(
                userId = userId,
                amount = -creditsRequired,
                type = "USAGE",
                description = "Geração de IA: $title"
            )
        )

        // Save generation to history
        val inputSummary = inputs.entries.joinToString(", ") { "${it.key}: ${it.value}" }.take(180)
        aiGenDao.insertGeneration(
            AiGenerationEntity(
                userId = userId,
                toolType = toolType,
                title = title,
                inputSummary = inputSummary,
                outputContent = generatedText,
                creditsConsumed = creditsRequired
            )
        )

        // Low credit warning notification
        if (newCredits <= 10) {
            notificationDao.insertNotification(
                NotificationEntity(
                    userId = userId,
                    title = "Atenção: Saldo de créditos baixo",
                    message = "Você tem apenas $newCredits créditos restantes. Recarregue para não interromper suas campanhas.",
                    type = "ALERT"
                )
            )
        }

        Result.success(generatedText)
    }

    // History & Favorites
    fun getGenerationsFlow(userId: Long): Flow<List<AiGenerationEntity>> = aiGenDao.getGenerationsFlow(userId)
    fun getGenerationsCountFlow(userId: Long): Flow<Int> = aiGenDao.getGenerationCountFlow(userId)
    suspend fun toggleGenerationFavorite(id: Long, fav: Boolean) = withContext(Dispatchers.IO) {
        aiGenDao.toggleFavorite(id, fav)
    }
    suspend fun deleteGeneration(id: Long) = withContext(Dispatchers.IO) {
        aiGenDao.deleteGeneration(id)
    }

    // Chat & Conversations
    fun getConversationsFlow(userId: Long): Flow<List<ConversationEntity>> = conversationDao.getConversationsFlow(userId)
    fun getMessagesFlow(conversationId: Long): Flow<List<ChatMessageEntity>> = messageDao.getMessagesFlow(conversationId)

    suspend fun createConversation(userId: Long, initialTitle: String): Long = withContext(Dispatchers.IO) {
        conversationDao.insertConversation(
            ConversationEntity(
                userId = userId,
                title = initialTitle
            )
        )
    }

    suspend fun renameConversation(conversationId: Long, newTitle: String) = withContext(Dispatchers.IO) {
        conversationDao.renameConversation(conversationId, newTitle)
    }

    suspend fun setConversationFavorite(conversationId: Long, isFav: Boolean) = withContext(Dispatchers.IO) {
        conversationDao.setFavorite(conversationId, isFav)
    }

    suspend fun deleteConversation(conversationId: Long) = withContext(Dispatchers.IO) {
        messageDao.deleteMessagesForConversation(conversationId)
        conversationDao.deleteConversation(conversationId)
    }

    suspend fun sendChatMessage(
        userId: Long,
        conversationId: Long,
        userMessage: String,
        customApiKey: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId)
            ?: return@withContext Result.failure(Exception("Usuário não logado."))

        if (user.credits < 1) {
            return@withContext Result.failure(IllegalStateException("Você precisa de pelo menos 1 crédito para conversar com o assistente."))
        }

        // Save user message
        messageDao.insertMessage(
            ChatMessageEntity(
                conversationId = conversationId,
                role = "user",
                content = userMessage
            )
        )

        val profile = profileDao.getProfileByUserId(userId)
        val assistantReply = AngolaBusinessAiEngine.generateContent(
            toolType = "CHAT",
            profile = profile,
            inputs = mapOf("prompt" to userMessage),
            customApiKey = customApiKey
        )

        // Save assistant message
        messageDao.insertMessage(
            ChatMessageEntity(
                conversationId = conversationId,
                role = "assistant",
                content = assistantReply
            )
        )

        // Deduct 1 credit
        val newCredits = user.credits - 1
        userDao.updateCredits(userId, newCredits)
        creditTxDao.insertTransaction(
            CreditTransactionEntity(
                userId = userId,
                amount = -1,
                type = "USAGE",
                description = "Assistente IA Chat"
            )
        )

        Result.success(assistantReply)
    }

    // Sales Control
    fun getSalesFlow(userId: Long): Flow<List<SaleEntity>> = saleDao.getSalesFlow(userId)

    suspend fun addSale(
        userId: Long,
        product: String,
        quantity: Int,
        unitPriceKz: Double,
        costPriceKz: Double,
        clientName: String,
        paymentMethod: String,
        notes: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val total = unitPriceKz * quantity
        val totalCost = costPriceKz * quantity
        val profit = total - totalCost
        saleDao.insertSale(
            SaleEntity(
                userId = userId,
                product = product,
                quantity = quantity,
                unitPriceKz = unitPriceKz,
                costPriceKz = costPriceKz,
                totalPriceKz = total,
                profitKz = profit,
                clientName = clientName,
                paymentMethod = paymentMethod,
                notes = notes
            )
        )
    }

    suspend fun deleteSale(saleId: Long) = withContext(Dispatchers.IO) {
        saleDao.deleteSale(saleId)
    }

    // Budgets (Orçamentos)
    fun getBudgetsFlow(userId: Long): Flow<List<BudgetEntity>> = budgetDao.getBudgetsFlow(userId)

    suspend fun createBudget(
        userId: Long,
        clientName: String,
        clientPhone: String,
        itemsSummary: String,
        subtotalKz: Double,
        discountKz: Double,
        taxKz: Double,
        notes: String,
        validityDays: Int = 15
    ): BudgetEntity = withContext(Dispatchers.IO) {
        val count = budgetDao.getBudgetCount(userId) + 1
        val budgetNumber = "ORC-2026-${String.format(Locale.US, "%03d", count)}"
        val total = (subtotalKz - discountKz) + taxKz

        val budget = BudgetEntity(
            userId = userId,
            budgetNumber = budgetNumber,
            clientName = clientName,
            clientPhone = clientPhone,
            itemsSummary = itemsSummary,
            subtotalKz = subtotalKz,
            discountKz = discountKz,
            taxKz = taxKz,
            totalKz = total,
            validityDays = validityDays,
            notes = notes
        )
        val id = budgetDao.insertBudget(budget)
        budget.copy(id = id)
    }

    suspend fun deleteBudget(id: Long) = withContext(Dispatchers.IO) {
        budgetDao.deleteBudget(id)
    }

    // Proposals (Propostas)
    fun getProposalsFlow(userId: Long): Flow<List<ProposalEntity>> = proposalDao.getProposalsFlow(userId)

    suspend fun createProposal(
        userId: Long,
        clientName: String,
        clientPhone: String,
        presentation: String,
        problem: String,
        solution: String,
        services: String,
        totalAmountKz: Double,
        deadline: String,
        paymentTerms: String,
        terms: String
    ): ProposalEntity = withContext(Dispatchers.IO) {
        val count = proposalDao.getProposalCount(userId) + 1
        val proposalNumber = "PROP-2026-${String.format(Locale.US, "%03d", count)}"

        val proposal = ProposalEntity(
            userId = userId,
            proposalNumber = proposalNumber,
            clientName = clientName,
            clientPhone = clientPhone,
            presentation = presentation,
            problem = problem,
            solution = solution,
            services = services,
            totalAmountKz = totalAmountKz,
            deadline = deadline,
            paymentTerms = paymentTerms,
            terms = terms
        )
        val id = proposalDao.insertProposal(proposal)
        proposal.copy(id = id)
    }

    suspend fun deleteProposal(id: Long) = withContext(Dispatchers.IO) {
        proposalDao.deleteProposal(id)
    }

    // Notifications & Credits
    fun getNotificationsFlow(userId: Long): Flow<List<NotificationEntity>> = notificationDao.getNotificationsFlow(userId)
    suspend fun markNotificationsAsRead(userId: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(userId)
    }

    fun getCreditTransactionsFlow(userId: Long): Flow<List<CreditTransactionEntity>> = creditTxDao.getTransactionsFlow(userId)
    fun getAllTemplatesFlow(): Flow<List<TemplateEntity>> = templateDao.getAllTemplatesFlow()
}
