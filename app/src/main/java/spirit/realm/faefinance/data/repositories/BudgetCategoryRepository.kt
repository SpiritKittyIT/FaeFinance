package spirit.realm.faefinance.data.repositories

import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.BudgetCategory
import spirit.realm.faefinance.data.daos.BudgetCategoryDao

/**
 * Interface defining repository operations for BudgetCategory entities.
 */
interface IBudgetCategoryRepository {
    suspend fun insert(budgetCategory: BudgetCategory)
    suspend fun insertAll(budgetCategories: List<BudgetCategory>)
    suspend fun delete(budgetCategory: BudgetCategory)
    suspend fun deleteAll(budgetCategories: List<BudgetCategory>)
    fun getAllForBudget(budgetId: Long): Flow<List<BudgetCategory>>
}

/**
 * Repository implementation for managing BudgetCategory data.
 */
class BudgetCategoryRepository(
    private val budgetCategoryDao: BudgetCategoryDao
) : IBudgetCategoryRepository {

    /**
     * Inserts a single budget-category association.
     *
     * @param budgetCategory The BudgetCategory to insert.
     */
    override suspend fun insert(budgetCategory: BudgetCategory) {
        budgetCategoryDao.insert(budgetCategory)
    }

    /**
     * Inserts multiple budget-category associations at once.
     *
     * @param budgetCategories A list of BudgetCategory objects to insert.
     */
    override suspend fun insertAll(budgetCategories: List<BudgetCategory>) {
        budgetCategoryDao.insertAll(budgetCategories)
    }

    /**
     * Deletes a specific budget-category association.
     *
     * @param budgetCategory The BudgetCategory to delete.
     */
    override suspend fun delete(budgetCategory: BudgetCategory) {
        budgetCategoryDao.delete(budgetCategory)
    }

    /**
     * Deletes multiple budget-category associations.
     *
     * @param budgetCategories A list of BudgetCategory objects to delete.
     */
    override suspend fun deleteAll(budgetCategories: List<BudgetCategory>) {
        budgetCategoryDao.deleteAll(budgetCategories)
    }

    /**
     * Retrieves all budget-category associations for a specific budget.
     *
     * @param budgetId The ID of the budget.
     * @return A Flow emitting a list of BudgetCategory objects.
     */
    override fun getAllForBudget(budgetId: Long): Flow<List<BudgetCategory>> {
        return budgetCategoryDao.getAllForBudget(budgetId)
    }
}
