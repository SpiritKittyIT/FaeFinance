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

    // Update the balance for an Account by a given delta (increase or decrease)
    @Query("UPDATE Account SET balance = balance + :delta WHERE id = :id")
    suspend fun updateBalance(id: Long, delta: Double)

    // Retrieve all Accounts, ordered by the 'sortOrder' column
    @Query("SELECT * FROM Account ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<Account>>

    // Retrieve a specific Account by its ID
    @Query("SELECT * FROM Account WHERE id = :id")
    fun getById(id: Long): Flow<Account>
}
