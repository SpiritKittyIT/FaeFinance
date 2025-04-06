package spirit.realm.faefinance.data

import spirit.realm.faefinance.data.repositories.*
import android.content.Context

/**
 * App container for Dependency injection.
 */
interface IAppDataContainer {
    val accountRepository: AccountRepository
    val budgetCategoryRepository: BudgetCategoryRepository
    val budgetRepository: BudgetRepository
    val budgetWithCategoriesRepository: BudgetWithCategoriesRepository
    val categoryRepository: CategoryRepository
    val periodicTransactionRepository: PeriodicTransactionRepository
    val periodicTransactionWithExpandedRepository: PeriodicTransactionWithExpandedRepository
    val transactionRepository: TransactionRepository
    val transactionWithExpandedRepository: TransactionWithExpandedRepository
}

/**
 * [IAppDataContainer] implementation that provides instance of repositories
 */
class AppDataContainer(context: Context) : IAppDataContainer {
    private val appDatabase = AppDatabase.getDatabase(context)

    override val accountRepository: AccountRepository = AccountRepository(appDatabase.accountDao())
    override val budgetCategoryRepository: BudgetCategoryRepository = BudgetCategoryRepository(appDatabase.budgetCategoryDao())
    override val budgetRepository: BudgetRepository = BudgetRepository(appDatabase.budgetDao())
    override val budgetWithCategoriesRepository: BudgetWithCategoriesRepository= BudgetWithCategoriesRepository(
        appDatabase.budgetDao(),
        appDatabase.budgetCategoryDao(),
        appDatabase.budgetWithCategoriesDao()
    )
    override val categoryRepository: CategoryRepository = CategoryRepository(appDatabase.categoryDao())
    override val periodicTransactionRepository: PeriodicTransactionRepository = PeriodicTransactionRepository(
        appDatabase.periodicTransactionDao(),
        appDatabase.transactionDao()
    )
    override val periodicTransactionWithExpandedRepository: PeriodicTransactionWithExpandedRepository
        = PeriodicTransactionWithExpandedRepository(appDatabase.periodicTransactionWithExpandedDao())
    override val transactionRepository: TransactionRepository = TransactionRepository(appDatabase.transactionDao())
    override val transactionWithExpandedRepository: TransactionWithExpandedRepository
        = TransactionWithExpandedRepository(appDatabase.transactionWithExpandedDao())
}