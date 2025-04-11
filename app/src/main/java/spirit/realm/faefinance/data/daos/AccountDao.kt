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

    @Delete
    suspend fun delete(account: Account)

    // Update the balance for an Account by a given delta (increase or decrease)
    @Query("UPDATE Account SET balance = balance + :delta WHERE id = :accountId")
    suspend fun updateBalance(accountId: Int, delta: Double)

    // Retrieve all Accounts, ordered by the 'sortOrder' column
    @Query("SELECT * FROM account ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<Account>>

    // Retrieve a specific Account by its ID
    @Query("SELECT * FROM account WHERE id = :id")
    fun getById(id: Int): Flow<Account>
}
