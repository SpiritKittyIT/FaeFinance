package spirit.realm.faefinance.data.daos
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.PeriodicTransaction
import spirit.realm.faefinance.data.classes.PeriodicTransactionExpanded
import java.util.*

@Dao
interface PeriodicTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(periodicTransaction: PeriodicTransaction)

    @Update
    suspend fun update(periodicTransaction: PeriodicTransaction)

    @Query("DELETE FROM PeriodicTransaction WHERE id = :id")
    suspend fun deleteById(id: Long)

    // Retrieves PeriodicTransaction by its id
    @Transaction
    @Query("SELECT * FROM PeriodicTransaction WHERE id = :id")
    fun getById(id: Long): Flow<PeriodicTransaction>

    // Retrieves all unprocessed PeriodicTransactions based on date
    @Query("""
        DELETE FROM PeriodicTransaction
        WHERE recipientAccount = :accountId OR recipientAccount = :accountId
    """)
    suspend fun deleteAllWithAccount(accountId: Long)

    // Retrieves all unprocessed PeriodicTransactions based on date
    @Query("SELECT * FROM PeriodicTransaction WHERE nextTransaction <= :date")
    fun getUnprocessed(date: Date = Date()): Flow<List<PeriodicTransaction>>

    // Retrieves Expanded PeriodicTransaction by its id
    @Transaction
    @Query("SELECT * FROM PeriodicTransaction WHERE id = :id")
    fun getExpandedById(id: Long): Flow<PeriodicTransactionExpanded>

    // Retrieves all Expanded PeriodicTransactions
    @Transaction
    @Query("SELECT * FROM PeriodicTransaction")
    fun getExpandedAll(): Flow<List<PeriodicTransactionExpanded>>
}
