package spirit.realm.faefinance.data.daos
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.Budget
import spirit.realm.faefinance.data.classes.BudgetExpanded
import java.util.*

@Dao
interface BudgetDao {

    /**
     * Inserts a single Budget into the database.
     * If a record with the same primary key exists, it will be replaced.
     *
     * @param budget The Budget to be inserted.
     * @return The row ID of the inserted Budget.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long

    /**
     * Updates an existing Budget in the database.
     *
     * @param budget The Budget to be updated.
     */
    @Update
    suspend fun update(budget: Budget)

    /**
     * Deletes a specific Budget from the database by its ID.
     *
     * @param id The ID of the Budget to be deleted.
     */
    @Query("DELETE FROM Budget WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Retrieves a specific Budget by its ID.
     *
     * @param id The ID of the Budget to retrieve.
     * @return A Flow emitting the Budget.
     */
    @Query("SELECT * FROM Budget WHERE id = :id")
    fun getById(id: Long): Flow<Budget>

    /**
     * Retrieves all Budget records, ordered by the startDate in ascending order.
     *
     * @return A Flow emitting a list of Budgets.
     */
    @Query("SELECT * FROM Budget ORDER BY startDate ASC")
    fun getAll(): Flow<List<Budget>>

    /**
     * Retrieves all Budget records associated with a specific budgetSet, ordered by the startDate in ascending order.
     *
     * @param setId The ID of the budget set to filter by.
     * @return A Flow emitting a list of Budgets.
     */
    @Query("SELECT * FROM Budget WHERE budgetSet = :setId")
    fun getAllInSet(setId: Long): Flow<List<Budget>>

    /**
     * Updates the amountSpent for a Budget by a given delta (increase or decrease).
     *
     * @param id The ID of the Budget to update.
     * @param delta The amount to add to the current amountSpent.
     */
    @Query("""
        UPDATE Budget 
        SET amountSpent = amountSpent + :delta 
        WHERE id = :id
    """)
    suspend fun updateAmountSpent(id: Long, delta: Double)

    /**
     * Sets the amountSpent for a specific Budget.
     *
     * @param id The ID of the Budget to update.
     * @param amountSpent The amount to set as the new amountSpent.
     */
    @Query("""
        UPDATE Budget 
        SET amountSpent = :amountSpent
        WHERE id = :id
    """)
    suspend fun setAmountSpent(id: Long, amountSpent: Double)

    /**
     * Retrieves all deferred Budgets where the endDate is less than or equal to the specified date,
     * and the intervalLength is greater than zero.
     *
     * @param date The date to filter deferred budgets by.
     * @return A Flow emitting a list of deferred Budgets.
     */
    @Query("SELECT * FROM Budget WHERE endDate <= :date AND intervalLength > 0")
    fun getDeferredBudgets(date: Date = Date()): Flow<List<Budget>>

    /**
     * Retrieves all Budget records that are associated with a specific category,
     * and where the timestamp falls within the startDate and endDate range.
     *
     * @param timestamp The timestamp to filter the budgets by.
     * @param categoryId The ID of the category to filter by.
     * @return A Flow emitting a list of Budgets associated with the category.
     */
    @Transaction
    @Query("""
        SELECT * FROM Budget 
        WHERE :timestamp >= startDate 
          AND :timestamp < endDate 
          AND id IN (
              SELECT budget FROM BudgetCategory WHERE category = :categoryId
          )
    """)
    fun getWithCategory(timestamp: Date, categoryId: Long): Flow<List<Budget>>

    /**
     * Retrieves all Expanded Budget records that are associated with a specific category,
     * and where the timestamp falls within the startDate and endDate range.
     *
     * @param timestamp The timestamp to filter the expanded budgets by.
     * @param categoryId The ID of the category to filter by.
     * @return A Flow emitting a list of Expanded Budgets associated with the category.
     */
    @Transaction
    @Query("""
        SELECT * FROM Budget 
        WHERE :timestamp >= startDate 
          AND :timestamp < endDate 
          AND id IN (
              SELECT budget FROM BudgetCategory WHERE category = :categoryId
          )
    """)
    fun getExpandedWithCategory(timestamp: Date, categoryId: Long): Flow<List<BudgetExpanded>>

    /**
     * Retrieves all Expanded Budget records from the database.
     *
     * @return A Flow emitting a list of Expanded Budgets.
     */
    @Transaction
    @Query("SELECT * FROM Budget")
    fun getExpandedAll(): Flow<List<BudgetExpanded>>

    /**
     * Retrieves an Expanded Budget by its ID.
     *
     * @param id The ID of the Expanded Budget to retrieve.
     * @return A Flow emitting the Expanded Budget.
     */
    @Transaction
    @Query("SELECT * FROM Budget WHERE id = :id")
    fun getExpandedById(id: Long): Flow<BudgetExpanded>

    /**
     * Retrieves all Expanded Budget records associated with a specific budgetSet.
     *
     * @param setId The ID of the budgetSet to filter by.
     * @return A Flow emitting a list of Expanded Budgets.
     */
    @Query("SELECT * FROM Budget WHERE budgetSet = :setId")
    fun getExpandedAllInSet(setId: Long): Flow<List<BudgetExpanded>>
}
