package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.classes.Transaction as DataTransaction
import spirit.realm.faefinance.data.daos.TransactionDao

interface ITransactionRepository {
    suspend fun insertTransaction(transaction: DataTransaction)
    suspend fun updateTransactionRecord(transaction: DataTransaction)
    suspend fun deleteTransactionRecord(transaction: DataTransaction)
    suspend fun getTransactionById(id: Int): DataTransaction?
    suspend fun createTransaction(transaction: DataTransaction)
    suspend fun updateTransaction(transaction: DataTransaction)
    suspend fun deleteTransaction(transaction: DataTransaction)
    suspend fun processTransaction(transaction: DataTransaction)
}

class TransactionRepository(
    private val transactionDao: TransactionDao
) : ITransactionRepository {

    override suspend fun insertTransaction(transaction: DataTransaction) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun updateTransactionRecord(transaction: DataTransaction) {
        transactionDao.updateTransactionRecord(transaction)
    }

    override suspend fun deleteTransactionRecord(transaction: DataTransaction) {
        transactionDao.deleteTransactionRecord(transaction)
    }

    override suspend fun getTransactionById(id: Int): DataTransaction? {
        return transactionDao.getTransactionById(id)
    }

    override suspend fun createTransaction(transaction: DataTransaction) {
        transactionDao.createTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: DataTransaction) {
        transactionDao.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: DataTransaction) {
        transactionDao.deleteTransaction(transaction)
    }

    override suspend fun processTransaction(transaction: DataTransaction) {
        transactionDao.processTransaction(transaction)
    }
}
