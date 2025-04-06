package spirit.realm.faefinance.data.daos

import androidx.room.*
import spirit.realm.faefinance.data.classes.BudgetCategory

@Dao
interface BudgetCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budgetCategory: BudgetCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgetCategories: List<BudgetCategory>)

    @Delete
    suspend fun delete(budgetCategory: BudgetCategory)

    @Query("SELECT * FROM BudgetCategory WHERE budget = :budgetId")
    suspend fun getCategoriesForBudget(budgetId: Int): List<BudgetCategory>
}
