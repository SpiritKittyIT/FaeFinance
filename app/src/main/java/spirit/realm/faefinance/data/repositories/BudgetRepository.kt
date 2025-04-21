package spirit.realm.faefinance.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import spirit.realm.faefinance.data.CurrencyConverter
import spirit.realm.faefinance.data.classes.*
import spirit.realm.faefinance.data.daos.*
import java.util.*
import androidx.room.withTransaction
import spirit.realm.faefinance.data.AppDatabase

/**
 * Interface for managing budget-related data.
 */
interface IBudgetRepository {
    suspend fun create(budget: Budget)
    suspend fun update(budget: Budget)
    suspend fun deleteSetByLatestId(id: Long)
    fun getById(id: Long): Flow<Budget>
    fun getAll(): Flow<List<Budget>>
    suspend fun processAllDeferred()

    // Expanded
    fun getExpandedWithCategory(timestamp: Date, categoryId: Long): Flow<List<BudgetExpanded>>
    fun getExpandedAll(): Flow<List<BudgetExpanded>>
    fun getExpandedById(id: Long): Flow<BudgetExpanded>
    fun getExpandedAllInSet(setId: Long): Flow<List<BudgetExpanded>>
    suspend fun createExpanded(budgetExpanded: BudgetExpanded)
    suspend fun updateExpanded(budgetExpanded: BudgetExpanded)
}

/**
 * Repository implementation for managing Budgets and BudgetExpanded objects.
 */
class BudgetRepository(
    private val db: AppDatabase,
    private val budgetDao: BudgetDao = db.budgetDao(),
    private val transactionDao: TransactionDao = db.transactionDao(),
    private val budgetCategoryDao: BudgetCategoryDao = db.budgetCategoryDao(),
) : IBudgetRepository {

    /**
     * Recalculates the amount spent for a budget based on its category transactions.
     */
    private suspend fun recalculateAmountSpent(id: Long) {
        val budgetExpanded = budgetDao.getExpandedById(id).first()

        var amountSpent = 0.0
        for (category in budgetExpanded.categories) {
            val transactions = transactionDao.getAllWithCategory(
                category.id,
                budgetExpanded.budget.startDate,
                budgetExpanded.budget.endDate
            ).first()

            for (transaction in transactions) {
                if (transaction.type != ETransactionType.Expense) continue

                val delta = if (transaction.currency != budgetExpanded.budget.currency) {
                    CurrencyConverter.convertCurrency(transaction.amount, transaction.currency, budgetExpanded.budget.currency)
                } else {
                    transaction.amount
                }

                amountSpent += delta
            }
        }

        budgetDao.setAmountSpent(id, amountSpent)
    }

    /**
     * Creates the next budget in the set based on interval logic.
     */
    private suspend fun createNext(currentBudget: Budget) {
        val calendar = Calendar.getInstance()
        calendar.time = currentBudget.endDate

        // Advance the end date based on the interval type
        when (currentBudget.interval) {
            ETransactionInterval.Days -> calendar.add(Calendar.DAY_OF_YEAR, currentBudget.intervalLength)
            ETransactionInterval.Weeks -> calendar.add(Calendar.WEEK_OF_YEAR, currentBudget.intervalLength)
            ETransactionInterval.Months -> calendar.add(Calendar.MONTH, currentBudget.intervalLength)
        }

        val currentExpanded = budgetDao.getExpandedById(currentBudget.id).first()

        val nextBudget = currentBudget.copy(
            id = 0,
            budgetSet = currentBudget.budgetSet,
            startDate = currentBudget.endDate,
            endDate = calendar.time,
            amountSpent = 0.0
        )

        val nextExpanded = BudgetExpanded(
            budget = nextBudget,
            categories = currentExpanded.categories
        )

        db.withTransaction {
            budgetDao.update(currentBudget.copy(intervalLength = 0))
            createExpanded(nextExpanded)
        }
    }

    // Public methods

    /**
     * Inserts a new budget and assigns a budgetSet if not already assigned.
     */
    override suspend fun create(budget: Budget) {
        db.withTransaction {
            val id = budgetDao.insert(budget)
            if (budget.budgetSet == 0L) {
                val updatedBudget = budget.copy(id = id, budgetSet = id)
                budgetDao.update(updatedBudget)
            }
            recalculateAmountSpent(id)
        }
    }

    /**
     * Updates a budget and recalculates the spent amount.
     */
    override suspend fun update(budget: Budget) {
        db.withTransaction {
            budgetDao.update(budget)
            recalculateAmountSpent(budget.id)
        }
    }

    /**
     * Deletes all budgets within a set, identified by the latest budget's ID.
     */
    override suspend fun deleteSetByLatestId(id: Long) {
        db.withTransaction {
            val budget = budgetDao.getById(id).first()
            budgetDao.getAllInSet(budget.budgetSet).first().forEach {
                budgetDao.deleteById(it.id)
                budgetCategoryDao.deleteAllForBudget(it.id)
            }
        }
    }

    override fun getById(id: Long): Flow<Budget> = budgetDao.getById(id)

    override fun getAll(): Flow<List<Budget>> = budgetDao.getAll()

    /**
     * Processes all deferred budgets, creating future ones as needed.
     */
    override suspend fun processAllDeferred() {
        var deferredBudgets = budgetDao.getDeferredBudgets().first()

        while (deferredBudgets.isNotEmpty()) {
            for (deferredBudget in deferredBudgets) {
                createNext(deferredBudget)
            }
            deferredBudgets = budgetDao.getDeferredBudgets().first()
        }
    }

    // Expanded

    override fun getExpandedAllInSet(setId: Long): Flow<List<BudgetExpanded>> =
        budgetDao.getExpandedAllInSet(setId)

    override fun getExpandedWithCategory(timestamp: Date, categoryId: Long): Flow<List<BudgetExpanded>> =
        budgetDao.getExpandedWithCategory(timestamp, categoryId)

    override fun getExpandedAll(): Flow<List<BudgetExpanded>> = budgetDao.getExpandedAll()

    override fun getExpandedById(id: Long): Flow<BudgetExpanded> = budgetDao.getExpandedById(id)

    /**
     * Creates a BudgetExpanded with associated category links and initializes spent amount.
     */
    override suspend fun createExpanded(budgetExpanded: BudgetExpanded) {
        db.withTransaction {
            val budgetId = budgetDao.insert(budgetExpanded.budget)
            if (budgetExpanded.budget.budgetSet == 0L) {
                budgetDao.update(budgetExpanded.budget.copy(id = budgetId, budgetSet = budgetId))
            }

            val categoryLinks = budgetExpanded.categories.map {
                BudgetCategory(budget = budgetId, category = it.id)
            }
            budgetCategoryDao.insertAll(categoryLinks)

            recalculateAmountSpent(budgetId)
        }
    }

    /**
     * Updates an existing BudgetExpanded, updating category links and converting currency if needed.
     */
    override suspend fun updateExpanded(budgetExpanded: BudgetExpanded) {
        db.withTransaction {
            val budgetId = budgetExpanded.budget.id
            val oldExpanded = budgetDao.getExpandedById(budgetId).first()

            val existingLinks = oldExpanded.categories.map { BudgetCategory(budgetId, it.id) }
            val newLinks = budgetExpanded.categories.map { BudgetCategory(budgetId, it.id) }

            if (existingLinks.toSet() != newLinks.toSet()) {
                budgetCategoryDao.deleteAll(existingLinks)
                budgetCategoryDao.insertAll(newLinks)
                budgetDao.update(budgetExpanded.budget)
                recalculateAmountSpent(budgetId)
            } else {
                if (oldExpanded.budget.currency != budgetExpanded.budget.currency) {
                    val newAmountSpent = CurrencyConverter.convertCurrency(
                        budgetExpanded.budget.amountSpent,
                        oldExpanded.budget.currency,
                        budgetExpanded.budget.currency
                    )
                    val newBudget = budgetExpanded.budget.copy(amountSpent = newAmountSpent)
                    budgetDao.update(newBudget)
                } else {
                    budgetDao.update(budgetExpanded.budget)
                }
            }
        }
    }
}
