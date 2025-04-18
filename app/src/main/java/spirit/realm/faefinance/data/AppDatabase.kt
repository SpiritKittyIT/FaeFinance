package spirit.realm.faefinance.data

import Converters
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

@Database(
    entities = [
        Account::class,
        Budget::class,
        BudgetCategory::class,
        Category::class,
        Transaction::class,
        PeriodicTransaction::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun periodicTransactionDao(): PeriodicTransactionDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                .build()
                .also { Instance = it }
            }
        }
    }
}