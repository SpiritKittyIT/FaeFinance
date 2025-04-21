package spirit.realm.faefinance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.data.classes.Budget
import spirit.realm.faefinance.data.classes.BudgetCategory
import spirit.realm.faefinance.data.classes.Category
import spirit.realm.faefinance.data.classes.PeriodicTransaction
import spirit.realm.faefinance.data.classes.Transaction
import spirit.realm.faefinance.data.daos.AccountDao
import spirit.realm.faefinance.data.daos.BudgetCategoryDao
import spirit.realm.faefinance.data.daos.BudgetDao
import spirit.realm.faefinance.data.daos.CategoryDao
import spirit.realm.faefinance.data.daos.PeriodicTransactionDao
import spirit.realm.faefinance.data.daos.TransactionDao

/**
 * AppDatabase class that provides access to all the DAOs (Data Access Objects) for the app.
 * It is the main database class for the app, using Room for ORM (Object-Relational Mapping).
 */
@Database(
    entities = [
        Account::class,          // Account entity
        Budget::class,           // Budget entity
        BudgetCategory::class,   // BudgetCategory entity
        Category::class,         // Category entity
        Transaction::class,      // Transaction entity
        PeriodicTransaction::class // PeriodicTransaction entity
    ],
    version = 3 // Version of the database
)
@TypeConverters(Converters::class) // Type converters for custom data types
abstract class AppDatabase : RoomDatabase() {

    // Abstract functions to access each DAO. Room will automatically implement these.
    abstract fun accountDao(): AccountDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun periodicTransactionDao(): PeriodicTransactionDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        // A volatile instance of the database to ensure that it's properly shared
        // between different threads.
        @Volatile
        private var Instance: AppDatabase? = null

        /**
         * Get the single instance of the AppDatabase.
         * Uses the double-check locking pattern to ensure thread safety.
         */
        fun getDatabase(context: Context): AppDatabase {
            // Check if the database instance already exists
            return Instance ?: synchronized(this) {
                // If it doesn't exist, build the database
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database" // Database name
                )
                    .fallbackToDestructiveMigration(false) // Disallow destructive migrations
                    .build()
                    .also { Instance = it } // Save the instance for future use
            }
        }
    }
}
