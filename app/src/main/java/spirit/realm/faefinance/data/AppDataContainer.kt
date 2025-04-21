package spirit.realm.faefinance.data

import spirit.realm.faefinance.data.repositories.*
import android.content.Context

/**
 * App container for Dependency Injection (DI).
 * This interface provides access to various repositories and settings in the application.
 */
interface IAppDataContainer {
    // Settings data store, which may handle storing app settings.
    val settings: SettingsDataStore

    // Repositories for interacting with the application's data layer.
    val accountRepository: AccountRepository
    val budgetCategoryRepository: BudgetCategoryRepository
    val budgetRepository: BudgetRepository
    val categoryRepository: CategoryRepository
    val periodicTransactionRepository: PeriodicTransactionRepository
    val transactionRepository: TransactionRepository
}

/**
 * Implementation of [IAppDataContainer] that provides instances of repositories.
 * This class is used to manage and inject the required dependencies throughout the app.
 */
class AppDataContainer(context: Context) : IAppDataContainer {

    // Get the instance of the database for use in repositories.
    private val appDatabase = AppDatabase.getDatabase(context)

    // Settings data store to handle app-specific settings storage.
    override val settings = SettingsDataStore(context)

    // Repositories are instantiated by passing necessary DAOs from the database.
    override val budgetCategoryRepository: BudgetCategoryRepository = BudgetCategoryRepository(appDatabase.budgetCategoryDao())
    override val budgetRepository: BudgetRepository = BudgetRepository(appDatabase)
    override val categoryRepository: CategoryRepository = CategoryRepository(appDatabase.categoryDao())
    override val transactionRepository: TransactionRepository = TransactionRepository(appDatabase)

    // Account repository requires both the database and the transaction repository.
    override val accountRepository: AccountRepository = AccountRepository(
        appDatabase,
        transactionRepository
    )

    // PeriodicTransaction repository needs both the database and the transaction repository as well.
    override val periodicTransactionRepository: PeriodicTransactionRepository = PeriodicTransactionRepository(
        appDatabase,
        transactionRepository
    )
}
