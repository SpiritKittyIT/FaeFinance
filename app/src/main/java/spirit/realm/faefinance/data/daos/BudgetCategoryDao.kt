package spirit.realm.faefinance.data.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.BudgetCategory

@Dao
interface BudgetCategoryDao {

    /**
     * Inserts a single BudgetCategory into the database.
     * If a record with the same primary key exists, it will be replaced.
     *
     * @param budgetCategory The BudgetCategory to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budgetCategory: BudgetCategory)

    /**
     * Inserts multiple BudgetCategories into the database.
     * If records with the same primary key exist, they will be replaced.
     *
     * @param budgetCategories The list of BudgetCategories to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgetCategories: List<BudgetCategory>)

    /**
     * Deletes a specific BudgetCategory from the database.
     *
     * @param budgetCategory The BudgetCategory to be deleted.
     */
    @Delete
    suspend fun delete(budgetCategory: BudgetCategory)

    /**
     * Deletes multiple BudgetCategories from the database.
     *
     * @param budgetCategories The list of BudgetCategories to be deleted.
     */
    @Delete
    suspend fun deleteAll(budgetCategories: List<BudgetCategory>)

    /**
     * Retrieves all BudgetCategories associated with a specific budgetId.
     *
     * @param budgetId The ID of the budget to retrieve the categories for.
     * @return A Flow emitting the list of BudgetCategories.
     */
    @Query("SELECT * FROM BudgetCategory WHERE budget = :budgetId")
    fun getAllForBudget(budgetId: Long): Flow<List<BudgetCategory>>

    /**
     * Deletes all BudgetCategories associated with a specific budgetId.
     *
     * @param budgetId The ID of the budget whose categories should be deleted.
     */
    @Query("DELETE FROM BudgetCategory WHERE budget = :budgetId")
    suspend fun deleteAllForBudget(budgetId: Long)
}
