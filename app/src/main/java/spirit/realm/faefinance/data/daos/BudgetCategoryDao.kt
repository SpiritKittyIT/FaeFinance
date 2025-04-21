package spirit.realm.faefinance.data.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.BudgetCategory

@Dao
interface BudgetCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budgetCategory: BudgetCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgetCategories: List<BudgetCategory>)

    @Delete
    suspend fun delete(budgetCategory: BudgetCategory)

    @Delete
    suspend fun deleteAll(budgetCategories: List<BudgetCategory>)

    // Retrieve all BudgetCategories associated with a specific budgetId.
    @Query("SELECT * FROM BudgetCategory WHERE budget = :budgetId")
    fun getAllForBudget(budgetId: Long): Flow<List<BudgetCategory>>

    // Delete all BudgetCategories associated with a specific budgetId.
    @Query("DELETE FROM BudgetCategory WHERE budget = :budgetId")
    suspend fun deleteAllForBudget(budgetId: Long)
}
