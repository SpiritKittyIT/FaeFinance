package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.data.daos.AccountDao

interface IAccountRepository {
    suspend fun insert(account: Account)
    suspend fun insertAll(accounts: List<Account>)
    suspend fun update(account: Account)
    suspend fun delete(account: Account)
    suspend fun deleteAllExceptDefault()
    suspend fun getAllAccounts(): List<Account>
    suspend fun getAccountById(id: Int): Account?
    suspend fun getAccountsByCurrency(currency: String): List<Account>
    suspend fun getTotalBalance(): Double?
}

class AccountRepository(
    private val accountDao: AccountDao
) : IAccountRepository {

    override suspend fun insert(account: Account) {
        accountDao.insert(account)
    }

    override suspend fun insertAll(accounts: List<Account>) {
        accountDao.insertAll(accounts)
    }

    override suspend fun update(account: Account) {
        accountDao.update(account)
    }

    override suspend fun delete(account: Account) {
        accountDao.delete(account)
    }

    override suspend fun deleteAllExceptDefault() {
        accountDao.deleteAllExceptDefault()
    }

    override suspend fun getAllAccounts(): List<Account> {
        return accountDao.getAllAccounts()
    }

    override suspend fun getAccountById(id: Int): Account? {
        return accountDao.getAccountById(id)
    }

    override suspend fun getAccountsByCurrency(currency: String): List<Account> {
        return accountDao.getAccountsByCurrency(currency)
    }

    override suspend fun getTotalBalance(): Double? {
        return accountDao.getTotalBalance()
    }
}
