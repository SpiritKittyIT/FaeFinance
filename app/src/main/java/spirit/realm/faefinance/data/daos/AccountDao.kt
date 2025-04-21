package spirit.realm.faefinance.data.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.Account

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Query("DELETE FROM Account WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Adjusts the balance of the account by a specified delta.
     *
     * @param id The ID of the account to update.
     * @param delta The amount to adjust the balance by (can be positive or negative).
     */
    @Query("UPDATE Account SET balance = balance + :delta WHERE id = :id")
    suspend fun updateBalance(id: Long, delta: Double)

    /**
     * Retrieves all accounts ordered by their sortOrder.
     *
     * @return A Flow emitting a list of all accounts.
     */
    @Query("SELECT * FROM Account ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<Account>>

    /**
     * Retrieves a single account by its ID.
     *
     * @param id The ID of the account.
     * @return A Flow emitting the matching account.
     */
    @Query("SELECT * FROM Account WHERE id = :id")
    fun getById(id: Long): Flow<Account>
}
