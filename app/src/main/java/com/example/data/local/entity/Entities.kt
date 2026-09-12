package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val businessName: String,
    val phone: String,
    val email: String,
    val passwordHash: String,
    val province: String,
    val municipality: String,
    val businessType: String,
    val role: String = "USER", // "USER", "ADMIN", "SUPERADMIN"
    val currentPlan: String = "FREE", // "FREE", "PREMIUM", "BUSINESS"
    val credits: Int = 50,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "business_profiles")
data class BusinessProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val businessName: String,
    val businessType: String,
    val productsOrServices: String,
    val targetAudience: String,
    val province: String,
    val municipality: String,
    val mainGoal: String,
    val socialMedia: String,
    val customBio: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val role: String, // "user", "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_generations")
data class AiGenerationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val toolType: String, // "POST_FB", "INSTA", "AD", "PROD_DESC", "CLIENT_REPLY", "MKT_PLAN", "CONTENT_IDEAS", "NAME", "SLOGAN"
    val title: String,
    val inputSummary: String,
    val outputContent: String,
    val creditsConsumed: Int = 2,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val product: String,
    val quantity: Int,
    val unitPriceKz: Double,
    val costPriceKz: Double,
    val totalPriceKz: Double,
    val profitKz: Double,
    val clientName: String,
    val paymentMethod: String, // "Multicaixa Express", "Transferência Bancária", "Dinheiro", "TPA"
    val dateTimestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val budgetNumber: String, // e.g. "ORC-2026-001"
    val clientName: String,
    val clientPhone: String,
    val itemsSummary: String, // summary text of items
    val subtotalKz: Double,
    val discountKz: Double,
    val taxKz: Double,
    val totalKz: Double,
    val validityDays: Int = 15,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "proposals")
data class ProposalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val proposalNumber: String,
    val clientName: String,
    val clientPhone: String,
    val presentation: String,
    val problem: String,
    val solution: String,
    val services: String,
    val totalAmountKz: Double,
    val deadline: String,
    val paymentTerms: String,
    val validity: String = "30 dias",
    val terms: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String, // "CREDIT", "PLAN", "REPORT", "ALERT"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "credit_transactions")
data class CreditTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val amount: Int,
    val type: String, // "BONUS", "PURCHASE", "USAGE"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "Loja de Roupas", "Restaurante", "Salão & Barbearia", "Designer", "Fotógrafo", "Informática", "Alimentos"
    val toolType: String,
    val title: String,
    val description: String,
    val content: String
)
