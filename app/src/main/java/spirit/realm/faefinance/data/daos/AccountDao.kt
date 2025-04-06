package spirit.realm.faefinance.data.daos

import androidx.room.*
import spirit.realm.faefinance.data.classes.Account

@Dao
interface AccountDao {
    // Insert a new account or update it if it already exists (using the primary key)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    // Insert multiple accounts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<Account>)

    // Update an existing account
    @Update
    suspend fun update(account: Account)

    // Delete a specific account (with check to prevent deleting the default account)
    @Delete
    suspend fun delete(account: Account) {
        if (account.id == 0) {
            throw IllegalArgumentException("Default account cannot be deleted")
        }
    }

    // Delete all accounts, excluding the default account
    @Query("DELETE FROM account WHERE id != 1")
    suspend fun deleteAllExceptDefault()

    // Get all accounts sorted by their sort order
    @Query("SELECT * FROM account ORDER BY sortOrder ASC")
    suspend fun getAllAccounts(): List<Account>

    // Get an account by its ID
    @Query("SELECT * FROM account WHERE id = :id")
    suspend fun getAccountById(id: Int): Account?

    // Get accounts filtered by currency (ISO code)
    @Query("SELECT * FROM account WHERE currency = :currency ORDER BY sortOrder ASC")
    suspend fun getAccountsByCurrency(currency: String): List<Account>

    // Get the total balance of all accounts
    @Query("SELECT SUM(balance) FROM account")
    suspend fun getTotalBalance(): Double?
}
