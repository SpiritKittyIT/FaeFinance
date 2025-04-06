package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.classes.Budget
import spirit.realm.faefinance.data.daos.BudgetDao

interface IBudgetRepository {
    suspend fun insert(budget: Budget)
    suspend fun insertAll(budgets: List<Budget>)
    suspend fun update(budget: Budget)
    suspend fun delete(budget: Budget)
    suspend fun getBudgetById(id: Int): Budget?
    suspend fun getBudgetsByCurrency(currency: String): List<Budget>
    suspend fun getAllBudgets(): List<Budget>
    suspend fun createNext(currentBudget: Budget): Budget
}

class BudgetRepository(
    private val budgetDao: BudgetDao
) : IBudgetRepository {

    override suspend fun insert(budget: Budget) {
        budgetDao.insert(budget)
    }

    override suspend fun insertAll(budgets: List<Budget>) {
        budgetDao.insertAll(budgets)
    }

    override suspend fun update(budget: Budget) {
        budgetDao.update(budget)
    }

    override suspend fun delete(budget: Budget) {
        budgetDao.delete(budget)
    }

    override suspend fun getBudgetById(id: Int): Budget? {
        return budgetDao.getBudgetById(id)
    }

    override suspend fun getBudgetsByCurrency(currency: String): List<Budget> {
        return budgetDao.getBudgetsByCurrency(currency)
    }

    override suspend fun getAllBudgets(): List<Budget> {
        return budgetDao.getAllBudgets()
    }

    override suspend fun createNext(currentBudget: Budget): Budget {
        return budgetDao.createNext(currentBudget)
    }
}
