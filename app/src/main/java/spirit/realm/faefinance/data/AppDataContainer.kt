package spirit.realm.faefinance.data

import spirit.realm.faefinance.data.repositories.*
import android.content.Context

/**
 * App container for Dependency injection.
 */
interface IAppDataContainer {
    val settings: SettingsDataStore
    val accountRepository: AccountRepository
    val budgetCategoryRepository: BudgetCategoryRepository
    val budgetRepository: BudgetRepository
    val categoryRepository: CategoryRepository
    val periodicTransactionRepository: PeriodicTransactionRepository
    val transactionRepository: TransactionRepository
}

/**
 * [IAppDataContainer] implementation that provides instance of repositories
 */
class AppDataContainer(context: Context) : IAppDataContainer {
    private val appDatabase = AppDatabase.getDatabase(context)

    override val settings = SettingsDataStore(context)

    override val budgetCategoryRepository: BudgetCategoryRepository = BudgetCategoryRepository(appDatabase.budgetCategoryDao())
    override val budgetRepository: BudgetRepository = BudgetRepository(appDatabase)
    override val categoryRepository: CategoryRepository = CategoryRepository(appDatabase.categoryDao())
    override val transactionRepository: TransactionRepository = TransactionRepository(appDatabase)

    override val accountRepository: AccountRepository = AccountRepository(
        appDatabase,
        transactionRepository
    )
    override val periodicTransactionRepository: PeriodicTransactionRepository = PeriodicTransactionRepository(
        appDatabase,
        transactionRepository
    )
}