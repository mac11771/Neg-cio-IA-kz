package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        BusinessProfileEntity::class,
        ConversationEntity::class,
        ChatMessageEntity::class,
        AiGenerationEntity::class,
        SaleEntity::class,
        BudgetEntity::class,
        ProposalEntity::class,
        NotificationEntity::class,
        CreditTransactionEntity::class,
        TemplateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun conversationDao(): ConversationDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun aiGenerationDao(): AiGenerationDao
    abstract fun saleDao(): SaleDao
    abstract fun budgetDao(): BudgetDao
    abstract fun proposalDao(): ProposalDao
    abstract fun notificationDao(): NotificationDao
    abstract fun creditTransactionDao(): CreditTransactionDao
    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "negocio_ia_kz.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
