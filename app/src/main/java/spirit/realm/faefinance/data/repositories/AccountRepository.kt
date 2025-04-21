package spirit.realm.faefinance.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import spirit.realm.faefinance.data.AppDatabase
import spirit.realm.faefinance.data.CurrencyConverter
import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.data.daos.AccountDao
import spirit.realm.faefinance.data.daos.PeriodicTransactionDao

/**
 * Interface defining repository operations for Account entities.
 */
interface IAccountRepository {
    suspend fun insert(account: Account)
    suspend fun update(account: Account)
    suspend fun deleteById(id: Long)
    fun getAll(): Flow<List<Account>>
    fun getById(id: Long): Flow<Account>
}

/**
 * Repository implementation for managing Account data,
 * handling associated transactions and periodic transactions.
 */
class AccountRepository(
    private val db: AppDatabase,
    private val transactionRepo: TransactionRepository,
    private val accountDao: AccountDao = db.accountDao(),
    private val periodicTransactionDao: PeriodicTransactionDao = db.periodicTransactionDao()
) : IAccountRepository {

    /**
     * Inserts a new account into the database.
     *
     * @param account The account to insert.
     */
    override suspend fun insert(account: Account) {
        accountDao.insert(account)
    }

    /**
     * Updates an existing account. If the currency has changed,
     * the balance is converted accordingly.
     *
     * @param account The updated account data.
     */
    override suspend fun update(account: Account) {
        val oldAccount = getById(account.id).first()

        account.balance = if (account.currency != oldAccount.currency) {
            CurrencyConverter.convertCurrency(
                account.balance,
                oldAccount.currency,
                account.currency
            )
        } else {
            account.balance
        }

        accountDao.update(account)
    }

    /**
     * Deletes an account by ID and removes all related transactions and periodic transactions.
     *
     * @param id The ID of the account to delete.
     */
    override suspend fun deleteById(id: Long) {
        db.withTransaction {
            transactionRepo.deleteAllByAccount(id)
            periodicTransactionDao.deleteAllWithAccount(id)
            accountDao.deleteById(id)
        }
    }

    /**
     * Retrieves all accounts from the database.
     *
     * @return A Flow emitting the list of all accounts.
     */
    override fun getAll(): Flow<List<Account>> {
        return accountDao.getAll()
    }

    /**
     * Retrieves a specific account by its ID.
     *
     * @param id The ID of the account.
     * @return A Flow emitting the requested account.
     */
    override fun getById(id: Long): Flow<Account> {
        return accountDao.getById(id)
    }
}
