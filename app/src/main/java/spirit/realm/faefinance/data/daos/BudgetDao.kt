package spirit.realm.faefinance.data.daos
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.Budget
import spirit.realm.faefinance.data.classes.BudgetExpanded
import java.util.*

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Int

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    // Retrieve a specific Budget by its ID
    @Query("SELECT * FROM budget WHERE id = :id")
    fun getById(id: Int): Flow<Budget>

    // Retrieve all Budget records sorted by startDate in ascending order
    @Query("SELECT * FROM budget ORDER BY startDate ASC")
    fun getAll(): Flow<List<Budget>>

    // Retrieve all Budget records sorted by startDate in ascending order
    @Query("SELECT * FROM budget WHERE budgetSet = :setId")
    fun getAllInSet(setId: Int): Flow<List<Budget>>

    // Update the amountSpent for Budget records that are associated with a specific BudgetCategory
    @Query("""
        UPDATE Budget 
        SET amountSpent = amountSpent + :delta 
        WHERE id = :id
    """)
    suspend fun updateAmountSpent(id: Int, delta: Double)

    // Set the amountSpent for Budget records that are associated with a specific BudgetCategory
    @Query("""
        UPDATE Budget 
        SET amountSpent = :amountSpent
        WHERE id = :id
    """)
    suspend fun setAmountSpent(id: Int, amountSpent: Double)

    // Retrieves all deferred Budgets based on date
    @Query("SELECT * FROM Budget WHERE endDate <= :date AND intervalLength > 0")
    fun getDeferredBudgets(date: Date = Date()): Flow<List<Budget>>

    // Retrieve all Budget records that are associated with a specific category,
    // and where the timestamp falls within the startDate and endDate range
    @Transaction
    @Query("""
        SELECT * FROM Budget 
        WHERE :timestamp >= startDate 
          AND :timestamp < endDate 
          AND id IN (
              SELECT budget FROM BudgetCategory WHERE category = :categoryId
          )
    """)
    fun getWithCategory(timestamp: Date, categoryId: Int): Flow<List<Budget>>

    // Retrieve all Expanded Budget records that are associated with a specific category,
    // and where the timestamp falls within the startDate and endDate range
    @Transaction
    @Query("""
        SELECT * FROM Budget 
        WHERE :timestamp >= startDate 
          AND :timestamp < endDate 
          AND id IN (
              SELECT budget FROM BudgetCategory WHERE category = :categoryId
          )
    """)
    fun getExpandedWithCategory(timestamp: Date, categoryId: Int): Flow<List<BudgetExpanded>>

    // Retrieve all Expanded Budget records
    @Transaction
    @Query("SELECT * FROM Budget")
    fun getExpandedAll(): Flow<List<BudgetExpanded>>

    // Retrieve Expanded Budget by id
    @Transaction
    @Query("SELECT * FROM Budget WHERE id = :id")
    fun getExpandedById(id: Int): Flow<BudgetExpanded>
}
