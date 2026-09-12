package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("UPDATE users SET credits = :newCredits WHERE id = :userId")
    suspend fun updateCredits(userId: Long, newCredits: Int)

    @Query("UPDATE users SET currentPlan = :newPlan WHERE id = :userId")
    suspend fun updatePlan(userId: Long, newPlan: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)
}

@Dao
interface BusinessProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: BusinessProfileEntity): Long

    @Query("SELECT * FROM business_profiles WHERE userId = :userId LIMIT 1")
    fun getProfileFlow(userId: Long): Flow<BusinessProfileEntity?>

    @Query("SELECT * FROM business_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileByUserId(userId: Long): BusinessProfileEntity?
}

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conv: ConversationEntity): Long

    @Query("SELECT * FROM conversations WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getConversationsFlow(userId: Long): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: Long): ConversationEntity?

    @Query("UPDATE conversations SET title = :newTitle, updatedAt = :timestamp WHERE id = :id")
    suspend fun renameConversation(id: Long, newTitle: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE conversations SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: Long)
}

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesFlow(convId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    suspend fun getMessagesList(convId: Long): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages WHERE conversationId = :convId")
    suspend fun deleteMessagesForConversation(convId: Long)
}

@Dao
interface AiGenerationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneration(generation: AiGenerationEntity): Long

    @Query("SELECT * FROM ai_generations WHERE userId = :userId ORDER BY timestamp DESC")
    fun getGenerationsFlow(userId: Long): Flow<List<AiGenerationEntity>>

    @Query("SELECT * FROM ai_generations WHERE userId = :userId AND toolType = :type ORDER BY timestamp DESC")
    fun getGenerationsByTypeFlow(userId: Long, type: String): Flow<List<AiGenerationEntity>>

    @Query("SELECT COUNT(*) FROM ai_generations WHERE userId = :userId")
    fun getGenerationCountFlow(userId: Long): Flow<Int>

    @Query("UPDATE ai_generations SET isFavorite = :fav WHERE id = :id")
    suspend fun toggleFavorite(id: Long, fav: Boolean)

    @Query("DELETE FROM ai_generations WHERE id = :id")
    suspend fun deleteGeneration(id: Long)
}

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity): Long

    @Query("SELECT * FROM sales WHERE userId = :userId ORDER BY dateTimestamp DESC")
    fun getSalesFlow(userId: Long): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE userId = :userId ORDER BY dateTimestamp DESC")
    suspend fun getSalesList(userId: Long): List<SaleEntity>

    @Query("DELETE FROM sales WHERE id = :id")
    suspend fun deleteSale(id: Long)
}

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY createdAt DESC")
    fun getBudgetsFlow(userId: Long): Flow<List<BudgetEntity>>

    @Query("SELECT COUNT(*) FROM budgets WHERE userId = :userId")
    suspend fun getBudgetCount(userId: Long): Int

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudget(id: Long)
}

@Dao
interface ProposalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposal(proposal: ProposalEntity): Long

    @Query("SELECT * FROM proposals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getProposalsFlow(userId: Long): Flow<List<ProposalEntity>>

    @Query("SELECT COUNT(*) FROM proposals WHERE userId = :userId")
    suspend fun getProposalCount(userId: Long): Int

    @Query("DELETE FROM proposals WHERE id = :id")
    suspend fun deleteProposal(id: Long)
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsFlow(userId: Long): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Long)
}

@Dao
interface CreditTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: CreditTransactionEntity): Long

    @Query("SELECT * FROM credit_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsFlow(userId: Long): Flow<List<CreditTransactionEntity>>
}

@Dao
interface TemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<TemplateEntity>)

    @Query("SELECT * FROM templates ORDER BY category ASC")
    fun getAllTemplatesFlow(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE category = :category")
    fun getTemplatesByCategoryFlow(category: String): Flow<List<TemplateEntity>>

    @Query("SELECT COUNT(*) FROM templates")
    suspend fun countTemplates(): Int
}
