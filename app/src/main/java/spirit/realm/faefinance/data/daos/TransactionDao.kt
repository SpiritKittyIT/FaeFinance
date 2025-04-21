package spirit.realm.faefinance.data.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.TransactionExpanded
import java.util.Date
import spirit.realm.faefinance.data.classes.Transaction as DataTransaction

@Dao
interface TransactionDao {

    /**
     * Inserts a Transaction into the database.
     * If a record with the same ID exists, it will be replaced.
     *
     * @param transaction The Transaction to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: DataTransaction)

    /**
     * Updates an existing Transaction in the database.
     *
     * @param transaction The Transaction to update.
     */
    @Update
    suspend fun update(transaction: DataTransaction)

    /**
     * Deletes a Transaction by its ID.
     *
     * @param id The ID of the Transaction to delete.
     */
    @Query("DELETE FROM `Transaction` WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Retrieves a Transaction by its ID.
     *
     * @param id The ID of the Transaction to retrieve.
     * @return A Flow emitting the Transaction.
     */
    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    fun getById(id: Long): Flow<DataTransaction>

    /**
     * Retrieves all Transactions where the given account is the sender.
     *
     * @param accountId The sender account ID.
     * @return A Flow emitting a list of Transactions sent from the account.
     */
    @Query("""
        SELECT * FROM `Transaction` 
        WHERE senderAccount = :accountId
    """)
    fun getAllByAccount(accountId: Long): Flow<List<DataTransaction>>

    /**
     * Retrieves all Transactions with a specific category within a given time interval.
     *
     * @param categoryId The ID of the category.
     * @param after The start of the time interval (inclusive).
     * @param before The end of the time interval (exclusive).
     * @return A Flow emitting a list of matching Transactions.
     */
    @Query("""
        SELECT * FROM `Transaction` 
        WHERE category = :categoryId
        AND timestamp >= :after AND timestamp < :before
    """)
    fun getAllWithCategory(categoryId: Long, after: Date, before: Date): Flow<List<DataTransaction>>

    /**
     * Retrieves an expanded version of a Transaction by its ID, including related data.
     *
     * @param id The ID of the Transaction.
     * @return A Flow emitting the expanded Transaction.
     */
    @Transaction
    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    fun getExpandedById(id: Long): Flow<TransactionExpanded>

    /**
     * Retrieves all expanded Transactions for an account.
     * If accountId is 0, retrieves all transactions.
     * Results are ordered by timestamp in descending order.
     *
     * @param accountId The account ID to filter by, or 0 to ignore.
     * @return A Flow emitting a list of expanded Transactions.
     */
    @Transaction
    @Query("""
        SELECT * FROM `Transaction` 
        WHERE (:accountId = 0 OR senderAccount = :accountId) 
        ORDER BY timestamp DESC
    """)
    fun getExpandedAllByAccount(accountId: Long): Flow<List<TransactionExpanded>>

    /**
     * Retrieves all expanded Transactions for an account within a specific time interval.
     * If accountId is 0, retrieves all transactions within the interval.
     *
     * @param accountId The account ID to filter by, or 0 to ignore.
     * @param after The start of the time interval (inclusive).
     * @param before The end of the time interval (exclusive).
     * @return A Flow emitting a list of expanded Transactions.
     */
    @Transaction
    @Query("""
        SELECT * FROM `Transaction` 
        WHERE (:accountId = 0 OR senderAccount = :accountId)
        AND timestamp >= :after AND timestamp < :before
    """)
    fun getExpandedAllByAccountInterval(accountId: Long, after: Date, before: Date): Flow<List<TransactionExpanded>>
}
