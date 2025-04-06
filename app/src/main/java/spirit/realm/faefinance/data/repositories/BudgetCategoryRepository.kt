package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.classes.BudgetCategory
import spirit.realm.faefinance.data.daos.BudgetCategoryDao

interface IBudgetCategoryRepository {
    suspend fun insert(budgetCategory: BudgetCategory)
    suspend fun insertAll(budgetCategories: List<BudgetCategory>)
    suspend fun delete(budgetCategory: BudgetCategory)
    suspend fun getCategoriesForBudget(budgetId: Int): List<BudgetCategory>
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

    override suspend fun getCategoriesForBudget(budgetId: Int): List<BudgetCategory> {
        return budgetCategoryDao.getCategoriesForBudget(budgetId)
    }
}
