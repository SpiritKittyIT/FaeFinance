package spirit.realm.faefinance.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import spirit.realm.faefinance.data.CurrencyConverter
import spirit.realm.faefinance.data.classes.Budget
import spirit.realm.faefinance.data.classes.BudgetCategory
import spirit.realm.faefinance.data.classes.BudgetExpanded
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.data.daos.BudgetCategoryDao
import spirit.realm.faefinance.data.daos.BudgetDao
import spirit.realm.faefinance.data.daos.TransactionDao
import java.util.Calendar
import java.util.Date
import androidx.room.withTransaction
import spirit.realm.faefinance.data.AppDatabase

interface IBudgetRepository {
    suspend fun create(budget: Budget)
    suspend fun update(budget: Budget)
    suspend fun deleteById(id: Long)
    fun getById(id: Long): Flow<Budget>
    fun getAll(): Flow<List<Budget>>
    fun getAllInSet(setId: Long): Flow<List<Budget>>
    suspend fun processDeferred()

    // Expanded
    fun getExpandedWithCategory(timestamp: Date, categoryId: Long): Flow<List<BudgetExpanded>>
    fun getExpandedAll(): Flow<List<BudgetExpanded>>
    fun getExpandedById(id: Long): Flow<BudgetExpanded>
    suspend fun createExpanded(budgetExpanded: BudgetExpanded)
    suspend fun updateExpanded(budgetExpanded: BudgetExpanded)
}

class BudgetRepository(
    private val db: AppDatabase,
    private val budgetDao: BudgetDao = db.budgetDao(),
    private val transactionDao: TransactionDao = db.transactionDao(),
    private val budgetCategoryDao: BudgetCategoryDao = db.budgetCategoryDao()
) : IBudgetRepository {
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
                if (transaction.type != ETransactionType.Expense) {
                    continue
                }

                val delta = if (transaction.currency != budgetExpanded.budget.currency) {
                    CurrencyConverter.convertCurrency(transaction.amount, transaction.currency, budgetExpanded.budget.currency)
                }
                else {
                    transaction.amount
                }

                amountSpent += delta
            }
        }

        budgetDao.setAmountSpent(id, amountSpent)
    }

    private suspend fun createNext(currentBudget: Budget) {
        // Calculate the next budget's start and end dates
        val calendar = Calendar.getInstance()
        calendar.time = currentBudget.endDate

        // Apply the intervalLength to determine the next start and end dates
        when (currentBudget.interval) {
            ETransactionInterval.Days -> calendar.add(Calendar.DAY_OF_YEAR, currentBudget.intervalLength)
            ETransactionInterval.Weeks -> calendar.add(Calendar.WEEK_OF_YEAR, currentBudget.intervalLength)
            ETransactionInterval.Months -> calendar.add(Calendar.MONTH, currentBudget.intervalLength)
        }

        val nextStartDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, currentBudget.intervalLength)  // Calculate the end date
        val nextEndDate = calendar.time

        // Create the new budget as a copy of the current one
        val nextBudget = currentBudget.copy(
            id = 0,  // Make sure to generate a new ID
            startDate = nextStartDate,
            endDate = nextEndDate,
            amountSpent = 0.0
        )

        db.withTransaction {
            // Insert the new budget into the database
            val id = budgetDao.insert(nextBudget)
            recalculateAmountSpent(id)
        }
    }

    // public

    override suspend fun create(budget: Budget) {
        db.withTransaction {
            val id = budgetDao.insert(budget)
            if (budget.budgetSet == 0L) {
                val updatedBudget = budget.copy(
                    id = id,
                    budgetSet = id
                )
                budgetDao.update(updatedBudget)
            }
            recalculateAmountSpent(id)
        }
    }

    override suspend fun update(budget: Budget) {
        db.withTransaction {
            budgetDao.update(budget)
            recalculateAmountSpent(budget.id)
        }
    }

    override suspend fun deleteById(id: Long) {
        budgetDao.deleteById(id)
    }

    override fun getById(id: Long): Flow<Budget> {
        return budgetDao.getById(id)
    }

    override fun getAll(): Flow<List<Budget>> {
        return budgetDao.getAll()
    }

    override fun getAllInSet(setId: Long): Flow<List<Budget>> {
        return budgetDao.getAllInSet(setId)
    }

    override suspend fun processDeferred() {
        val deferredBudgets = budgetDao.getDeferredBudgets().first()

        for (deferredBudget in deferredBudgets) {
            createNext(deferredBudget)
        }
    }

    // Expanded

    override fun getExpandedWithCategory(timestamp: Date, categoryId: Long): Flow<List<BudgetExpanded>> {
        return budgetDao.getExpandedWithCategory(timestamp, categoryId)
    }

    override fun getExpandedAll(): Flow<List<BudgetExpanded>> {
        return budgetDao.getExpandedAll()
    }

    override fun getExpandedById(id: Long): Flow<BudgetExpanded> {
        return budgetDao.getExpandedById(id)
    }

    override suspend fun createExpanded(budgetExpanded: BudgetExpanded) {
        db.withTransaction {
            // Insert the budget
            budgetDao.insert(budgetExpanded.budget)

            // Insert the category links
            val budgetId = budgetExpanded.budget.id
            val categoryLinks = budgetExpanded.categories.map { category ->
                BudgetCategory(budget = budgetId, category = category.id)
            }
            budgetCategoryDao.insertAll(categoryLinks)

            recalculateAmountSpent(budgetId)
        }
    }

    override suspend fun updateExpanded(budgetExpanded: BudgetExpanded)  {
        db.withTransaction {
            val budgetId = budgetExpanded.budget.id
            val budgetExpandedOld = budgetDao.getExpandedById(budgetId).first()

            val existingLinks = budgetExpandedOld.categories.map { category ->
                BudgetCategory(budget = budgetId, category = category.id)
            }
            val newLinks = budgetExpanded.categories.map { category ->
                BudgetCategory(budget = budgetId, category = category.id)
            }

            if (existingLinks.toSet() != newLinks.toSet()) { // toSet() so they don't compare order
                // remove old category links
                budgetCategoryDao.deleteAll(existingLinks)

                // Insert updated category links
                budgetCategoryDao.insertAll(newLinks)

                // Update the budget
                budgetDao.update(budgetExpanded.budget)

                recalculateAmountSpent(budgetId)
            }
            else {
                if (budgetExpandedOld.budget.currency != budgetExpanded.budget.currency) {
                    val newAmountSpent = CurrencyConverter.convertCurrency(
                        budgetExpanded.budget.amountSpent,
                        budgetExpandedOld.budget.currency,
                        budgetExpanded.budget.currency
                    )

                    val newBudget = budgetExpanded.budget.copy(amountSpent = newAmountSpent)

                    // Update the budget
                    budgetDao.update(newBudget)
                }
                else {
                    // Update the budget
                    budgetDao.update(budgetExpanded.budget)
                }
            }
        }
    }
}
