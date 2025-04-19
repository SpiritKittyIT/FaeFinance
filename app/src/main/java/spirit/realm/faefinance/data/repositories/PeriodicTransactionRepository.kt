package spirit.realm.faefinance.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import spirit.realm.faefinance.data.AppDatabase
import spirit.realm.faefinance.data.classes.*
import spirit.realm.faefinance.data.daos.PeriodicTransactionDao
import java.util.*

interface IPeriodicTransactionRepository {
    suspend fun insert(periodicTransaction: PeriodicTransaction)
    suspend fun update(periodicTransaction: PeriodicTransaction)
    suspend fun delete(periodicTransaction: PeriodicTransaction)
    fun getById(id: Long): Flow<PeriodicTransaction>
    fun getUnprocessed(): Flow<List<PeriodicTransaction>>
    suspend fun process(periodicTransaction: PeriodicTransaction)
    suspend fun processAllUnprocessed()

    fun getExpandedById(id: Long): Flow<PeriodicTransactionExpanded>
    fun getExpandedAll(): Flow<List<PeriodicTransactionExpanded>>
}

class PeriodicTransactionRepository(
    private val db: AppDatabase,
    private val transactionRepo: TransactionRepository,
    private val periodicTransactionDao: PeriodicTransactionDao = db.periodicTransactionDao()
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

    override fun getById(id: Long): Flow<PeriodicTransaction> {
        return periodicTransactionDao.getById(id)
    }

    override fun getUnprocessed(): Flow<List<PeriodicTransaction>> {
        return periodicTransactionDao.getUnprocessed()
    }

    override suspend fun process(periodicTransaction: PeriodicTransaction) {
        db.withTransaction {
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
            transactionRepo.process(newTransaction)

            // Process the periodic transaction based on its intervalLength.
            if (periodicTransaction.intervalLength == 0) {
                // No further recurrence; delete this periodic transaction.
                periodicTransactionDao.delete(periodicTransaction)
            }
            else {
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
        }
    }

    override suspend fun processAllUnprocessed() {
        val periodicTransactions = getUnprocessed().first()
        for (periodicTransaction in periodicTransactions) {
            process(periodicTransaction)
        }
    }

    override fun getExpandedById(id: Long): Flow<PeriodicTransactionExpanded> {
        return periodicTransactionDao.getExpandedById(id)
    }

    override fun getExpandedAll(): Flow<List<PeriodicTransactionExpanded>> {
        return periodicTransactionDao.getExpandedAll()
    }
}
