package spirit.realm.faefinance.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import spirit.realm.faefinance.data.AppDatabase
import spirit.realm.faefinance.data.CurrencyConverter
import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.data.daos.AccountDao
import spirit.realm.faefinance.data.daos.PeriodicTransactionDao

interface IAccountRepository {
    suspend fun insert(account: Account)
    suspend fun update(account: Account)
    suspend fun delete(account: Account)
    fun getAll(): Flow<List<Account>>
    fun getById(id: Int): Flow<Account>
}

class AccountRepository(
    private val db: AppDatabase,
    private val transactionRepo: TransactionRepository,
    private val accountDao: AccountDao = db.accountDao(),
    private val periodicTransactionDao: PeriodicTransactionDao = db.periodicTransactionDao()
) : IAccountRepository {

    override suspend fun insert(account: Account) {
        accountDao.insert(account)
    }

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

    override suspend fun delete(account: Account) {
        db.withTransaction {
            transactionRepo.deleteAllByAccount(account.id)
            periodicTransactionDao.deleteAllWithAccount(account.id)
            accountDao.delete(account)
        }
    }

    override fun getAll(): Flow<List<Account>> {
        return accountDao.getAll()
    }

    override fun getById(id: Int): Flow<Account> {
        return accountDao.getById(id)
    }
}
