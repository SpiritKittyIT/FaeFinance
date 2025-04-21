package spirit.realm.faefinance.data.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.PeriodicTransaction
import spirit.realm.faefinance.data.classes.PeriodicTransactionExpanded
import java.util.*

@Dao
interface PeriodicTransactionDao {

    /**
     * Inserts a PeriodicTransaction into the database.
     * If a record with the same ID exists, it will be replaced.
     *
     * @param periodicTransaction The PeriodicTransaction to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(periodicTransaction: PeriodicTransaction)

    /**
     * Updates an existing PeriodicTransaction in the database.
     *
     * @param periodicTransaction The PeriodicTransaction to update.
     */
    @Update
    suspend fun update(periodicTransaction: PeriodicTransaction)

    /**
     * Deletes a PeriodicTransaction by its ID.
     *
     * @param id The ID of the PeriodicTransaction to delete.
     */
    @Query("DELETE FROM PeriodicTransaction WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Retrieves a PeriodicTransaction by its ID.
     *
     * @param id The ID of the PeriodicTransaction to retrieve.
     * @return A Flow emitting the PeriodicTransaction.
     */
    @Transaction
    @Query("SELECT * FROM PeriodicTransaction WHERE id = :id")
    fun getById(id: Long): Flow<PeriodicTransaction>

    /**
     * Deletes all PeriodicTransactions that are associated with a given account.
     *
     * @param accountId The ID of the account involved in the transactions.
     */
    @Query("""
        DELETE FROM PeriodicTransaction
        WHERE recipientAccount = :accountId OR recipientAccount = :accountId
    """)
    suspend fun deleteAllWithAccount(accountId: Long)

    /**
     * Retrieves all PeriodicTransactions that are due for processing based on the given date.
     *
     * @param date The cutoff date for retrieving unprocessed transactions. Defaults to the current date.
     * @return A Flow emitting a list of unprocessed PeriodicTransactions.
     */
    @Query("SELECT * FROM PeriodicTransaction WHERE nextTransaction <= :date")
    fun getUnprocessed(date: Date = Date()): Flow<List<PeriodicTransaction>>

    /**
     * Retrieves an expanded version of a PeriodicTransaction by its ID, including related data.
     *
     * @param id The ID of the PeriodicTransaction.
     * @return A Flow emitting the expanded PeriodicTransaction.
     */
    @Transaction
    @Query("SELECT * FROM PeriodicTransaction WHERE id = :id")
    fun getExpandedById(id: Long): Flow<PeriodicTransactionExpanded>

    /**
     * Retrieves all expanded PeriodicTransactions, including related data.
     *
     * @return A Flow emitting a list of expanded PeriodicTransactions.
     */
    @Transaction
    @Query("SELECT * FROM PeriodicTransaction")
    fun getExpandedAll(): Flow<List<PeriodicTransactionExpanded>>
}
