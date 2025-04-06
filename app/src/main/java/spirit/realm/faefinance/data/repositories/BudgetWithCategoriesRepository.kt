package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.classes.BudgetCategory
import spirit.realm.faefinance.data.classes.BudgetWithCategories
import spirit.realm.faefinance.data.daos.BudgetCategoryDao
import spirit.realm.faefinance.data.daos.BudgetDao
import spirit.realm.faefinance.data.daos.BudgetWithCategoriesDao
import java.util.Date

interface IBudgetWithCategoriesRepository {
    suspend fun getBudgetsWithCategory(timestamp: Date, categoryId: Int): List<BudgetWithCategories>
    suspend fun getBudgetWithCategoriesById(budgetId: Int): BudgetWithCategories?
    suspend fun createBudgetWithCategories(budgetWithCategories: BudgetWithCategories): BudgetWithCategories?
    suspend fun updateBudgetWithCategories(budgetWithCategories: BudgetWithCategories): BudgetWithCategories?
}

class BudgetWithCategoriesRepository(
    private val budgetDao: BudgetDao,
    private val budgetCategoryDao: BudgetCategoryDao,
    private val budgetWithCategoriesDao: BudgetWithCategoriesDao
) : IBudgetWithCategoriesRepository {

    override suspend fun getBudgetsWithCategory(timestamp: Date, categoryId: Int): List<BudgetWithCategories> {
        return budgetWithCategoriesDao.getBudgetsWithCategory(timestamp, categoryId)
    }

    override suspend fun getBudgetWithCategoriesById(budgetId: Int): BudgetWithCategories? {
        return budgetWithCategoriesDao.getBudgetWithCategoriesById(budgetId)
    }

    override suspend fun createBudgetWithCategories(budgetWithCategories: BudgetWithCategories): BudgetWithCategories? {
        // Insert the budget
        budgetDao.insert(budgetWithCategories.budget)

        // Insert the category links
        val budgetId = budgetWithCategories.budget.id
        val categoryLinks = budgetWithCategories.categories.map { category ->
            BudgetCategory(budget = budgetId, category = category.id)
        }
        budgetCategoryDao.insertAll(categoryLinks)

        return getBudgetWithCategoriesById(budgetId)
    }

    override suspend fun updateBudgetWithCategories(budgetWithCategories: BudgetWithCategories): BudgetWithCategories? {
        // Update the budget
        budgetDao.update(budgetWithCategories.budget)

        // Clear old category links
        val budgetId = budgetWithCategories.budget.id
        val existingLinks = budgetCategoryDao.getCategoriesForBudget(budgetId)
        existingLinks.forEach { budgetCategoryDao.delete(it) }

        // Insert updated category links
        val newLinks = budgetWithCategories.categories.map { category ->
            BudgetCategory(budget = budgetId, category = category.id)
        }
        budgetCategoryDao.insertAll(newLinks)

        return getBudgetWithCategoriesById(budgetId)
    }
}
