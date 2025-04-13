package spirit.realm.faefinance.data.repositories

import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.BudgetCategory
import spirit.realm.faefinance.data.daos.BudgetCategoryDao

interface IBudgetCategoryRepository {
    suspend fun insert(budgetCategory: BudgetCategory)
    suspend fun insertAll(budgetCategories: List<BudgetCategory>)
    suspend fun delete(budgetCategory: BudgetCategory)
    suspend fun deleteAll(budgetCategories: List<BudgetCategory>)
    fun getAllForBudget(budgetId: Long): Flow<List<BudgetCategory>>
}

class BudgetCategoryRepository(
    private val budgetCategoryDao: BudgetCategoryDao
) : IBudgetCategoryRepository {

    override suspend fun insert(budgetCategory: BudgetCategory) {
        budgetCategoryDao.insert(budgetCategory)
    }

    override suspend fun insertAll(budgetCategories: List<BudgetCategory>) {
        budgetCategoryDao.insertAll(budgetCategories)
    }

    override suspend fun delete(budgetCategory: BudgetCategory) {
        budgetCategoryDao.delete(budgetCategory)
    }

    override suspend fun deleteAll(budgetCategories: List<BudgetCategory>) {
        budgetCategoryDao.deleteAll(budgetCategories)
    }

    override fun getAllForBudget(budgetId: Long): Flow<List<BudgetCategory>> {
        return budgetCategoryDao.getAllForBudget(budgetId)
    }
}
