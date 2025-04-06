package spirit.realm.faefinance.data.daos
import androidx.room.*
import spirit.realm.faefinance.data.classes.PeriodicTransaction
import java.util.*

@Dao
interface PeriodicTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(periodicTransaction: PeriodicTransaction)

    @Update
    suspend fun update(periodicTransaction: PeriodicTransaction)

    @Delete
    suspend fun delete(periodicTransaction: PeriodicTransaction)

    // Returns all periodic transactions where nextTransaction is in the past
    @Query("SELECT * FROM PeriodicTransaction WHERE nextTransaction <= :now")
    suspend fun getUnprocessedPeriodicTransactions(now: Date = Date()): List<PeriodicTransaction>
}
