package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.classes.*
import spirit.realm.faefinance.data.daos.PeriodicTransactionDao
import spirit.realm.faefinance.data.daos.TransactionDao
import java.util.*

interface IPeriodicTransactionRepository {
    suspend fun insert(periodicTransaction: PeriodicTransaction)
    suspend fun update(periodicTransaction: PeriodicTransaction)
    suspend fun delete(periodicTransaction: PeriodicTransaction)
    suspend fun getUnprocessedPeriodicTransactions(): List<PeriodicTransaction>
    suspend fun processPeriodicTransaction(periodicTransaction: PeriodicTransaction)
    suspend fun processAllUnprocessed()
}

class PeriodicTransactionRepository(
    private val periodicTransactionDao: PeriodicTransactionDao,
    private val transactionDao: TransactionDao
) : IPeriodicTransactionRepository {

    override suspend fun insert(periodicTransaction: PeriodicTransaction) {
        periodicTransactionDao.insert(periodicTransaction)
    }

    override suspend fun update(periodicTransaction: PeriodicTransaction) {
        periodicTransactionDao.update(periodicTransaction)
    }

    override suspend fun delete(periodicTransaction: PeriodicTransaction) {
        periodicTransactionDao.delete(periodicTransaction)
    }

    override suspend fun getUnprocessedPeriodicTransactions(): List<PeriodicTransaction> {
        return periodicTransactionDao.getUnprocessedPeriodicTransactions()
    }

    override suspend fun processPeriodicTransaction(periodicTransaction: PeriodicTransaction) {
        // Create a new Transaction based on the periodic transaction.
        val newTransaction = Transaction(
            id = 0,
            type = periodicTransaction.type,
            title = periodicTransaction.title,
            amount = periodicTransaction.amount,
            amountConverted = 0.0, // Will be computed by TransactionDao
            senderAccount = periodicTransaction.senderAccount,
            recipientAccount = periodicTransaction.recipientAccount,
            currency = periodicTransaction.currency,
            category = periodicTransaction.category,
            timestamp = periodicTransaction.nextTransaction
        )
        transactionDao.processTransaction(newTransaction)

        // Process the periodic transaction based on its intervalLength.
        if (periodicTransaction.intervalLength == 0) {
            // No further recurrence; delete this periodic transaction.
            periodicTransactionDao.delete(periodicTransaction)
            return
        }

        // Calculate new nextTransaction date.
        val calendar = Calendar.getInstance()
        calendar.time = periodicTransaction.nextTransaction
        when (periodicTransaction.interval) {
            ETransactionInterval.Days -> calendar.add(Calendar.DAY_OF_YEAR, periodicTransaction.intervalLength)
            ETransactionInterval.Weeks -> calendar.add(Calendar.WEEK_OF_YEAR, periodicTransaction.intervalLength)
            ETransactionInterval.Months -> calendar.add(Calendar.MONTH, periodicTransaction.intervalLength)
        }
        periodicTransaction.nextTransaction = calendar.time
        periodicTransactionDao.update(periodicTransaction)
    }

    override suspend fun processAllUnprocessed() {
        val transactions = getUnprocessedPeriodicTransactions()
        for (transaction in transactions) {
            processPeriodicTransaction(transaction)
        }
    }
}
