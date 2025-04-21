package spirit.realm.faefinance.data.daos
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.Budget
import spirit.realm.faefinance.data.classes.BudgetExpanded
import java.util.*

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget)

    @Query("DELETE FROM Budget WHERE id = :id")
    suspend fun deleteById(id: Long)

    // Retrieve a specific Budget by its ID
    @Query("SELECT * FROM Budget WHERE id = :id")
    fun getById(id: Long): Flow<Budget>

    // Retrieve all Budget records sorted by startDate in ascending order
    @Query("SELECT * FROM Budget ORDER BY startDate ASC")
    fun getAll(): Flow<List<Budget>>

    // Retrieve all Budget records sorted by startDate in ascending order
    @Query("SELECT * FROM Budget WHERE budgetSet = :setId")
    fun getAllInSet(setId: Long): Flow<List<Budget>>

    // Update the amountSpent for Budget records that are associated with a specific BudgetCategory
    @Query("""
        UPDATE Budget 
        SET amountSpent = amountSpent + :delta 
        WHERE id = :id
    """)
    suspend fun updateAmountSpent(id: Long, delta: Double)

    // Set the amountSpent for Budget records that are associated with a specific BudgetCategory
    @Query("""
        UPDATE Budget 
        SET amountSpent = :amountSpent
        WHERE id = :id
    """)
    suspend fun setAmountSpent(id: Long, amountSpent: Double)

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
    fun getWithCategory(timestamp: Date, categoryId: Long): Flow<List<Budget>>

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
    fun getExpandedWithCategory(timestamp: Date, categoryId: Long): Flow<List<BudgetExpanded>>

    // Retrieve all Expanded Budget records
    @Transaction
    @Query("SELECT * FROM Budget")
    fun getExpandedAll(): Flow<List<BudgetExpanded>>

    // Retrieve Expanded Budget by id
    @Transaction
    @Query("SELECT * FROM Budget WHERE id = :id")
    fun getExpandedById(id: Long): Flow<BudgetExpanded>

    // Retrieve all Expanded Budget records sorted by startDate in ascending order
    @Query("SELECT * FROM Budget WHERE budgetSet = :setId")
    fun getExpandedAllInSet(setId: Long): Flow<List<BudgetExpanded>>
}
