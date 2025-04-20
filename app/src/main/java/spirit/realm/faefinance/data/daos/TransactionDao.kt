package spirit.realm.faefinance.data.daos
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.TransactionExpanded
import java.util.Date
import spirit.realm.faefinance.data.classes.Transaction as DataTransaction

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: DataTransaction)

    @Update
    suspend fun update(transaction: DataTransaction)

    @Query("DELETE FROM `Transaction` WHERE id = :id")
    suspend fun deleteById(id: Long)

    // Retrieves a Transaction by its id
    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    fun getById(id: Long): Flow<DataTransaction>

    // Retrieves all Transactions made by
    @Query("""
        select * FROM `Transaction` 
        WHERE senderAccount = :accountId
    """)
    fun getAllByAccount(accountId: Long): Flow<List<DataTransaction>>

    // Retrieves all Transactions with category in a time interval
    @Query("""
        select * FROM `Transaction` 
        WHERE category = :categoryId
        AND timestamp >= :after AND timestamp < :before
    """)
    fun getAllWithCategory(categoryId: Long, after: Date, before: Date): Flow<List<DataTransaction>>

    // Retrieves an Expanded Transaction by its id
    @Transaction
    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    fun getExpandedById(id: Long): Flow<TransactionExpanded>

    // Retrieves all Expanded Transactions for an account, filtered by the sender or recipient account
    // Results are sorted by timestamp in descending order
    @Transaction
    @Query("""
        SELECT * FROM `Transaction` 
        WHERE (:accountId = 0 OR senderAccount = :accountId) 
        ORDER BY timestamp DESC
    """)
    fun getExpandedAllByAccount(accountId: Long): Flow<List<TransactionExpanded>>
}
