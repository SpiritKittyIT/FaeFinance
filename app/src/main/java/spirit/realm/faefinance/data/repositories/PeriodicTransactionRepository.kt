package spirit.realm.faefinance.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import spirit.realm.faefinance.data.AppDatabase
import spirit.realm.faefinance.data.classes.*
import spirit.realm.faefinance.data.daos.PeriodicTransactionDao
import java.util.*

/**
 * Interface for managing periodic transactions (recurring transactions).
 */
interface IPeriodicTransactionRepository {
    suspend fun insert(periodicTransaction: PeriodicTransaction)
    suspend fun update(periodicTransaction: PeriodicTransaction)
    suspend fun deleteById(id: Long)
    fun getById(id: Long): Flow<PeriodicTransaction>
    fun getUnprocessed(): Flow<List<PeriodicTransaction>>
    suspend fun process(periodicTransaction: PeriodicTransaction)
    suspend fun processAllUnprocessed()

    fun getExpandedById(id: Long): Flow<PeriodicTransactionExpanded>
    fun getExpandedAll(): Flow<List<PeriodicTransactionExpanded>>
}

/**
 * Repository implementation for PeriodicTransaction data and processing logic.
 */
class PeriodicTransactionRepository(
    private val db: AppDatabase,
    private val transactionRepo: TransactionRepository,
    private val periodicTransactionDao: PeriodicTransactionDao = db.periodicTransactionDao()
) : IPeriodicTransactionRepository {

    /**
     * Inserts a periodic transaction into the database.
     */
    override suspend fun insert(periodicTransaction: PeriodicTransaction) {
        periodicTransactionDao.insert(periodicTransaction)
    }

    /**
     * Updates an existing periodic transaction.
     */
    override suspend fun update(periodicTransaction: PeriodicTransaction) {
        periodicTransactionDao.update(periodicTransaction)
    }

    /**
     * Deletes a periodic transaction by its ID.
     */
    override suspend fun deleteById(id: Long) {
        periodicTransactionDao.deleteById(id)
    }

    /**
     * Retrieves a periodic transaction by ID.
     */
    override fun getById(id: Long): Flow<PeriodicTransaction> {
        return periodicTransactionDao.getById(id)
    }

    /**
     * Retrieves all periodic transactions that are due for processing.
     */
    override fun getUnprocessed(): Flow<List<PeriodicTransaction>> {
        return periodicTransactionDao.getUnprocessed()
    }

    /**
     * Processes a single periodic transaction:
     * - Converts it into a normal transaction
     * - Either schedules the next one or deletes it if not recurring
     */
    override suspend fun process(periodicTransaction: PeriodicTransaction) {
        db.withTransaction {
            val newTransaction = Transaction(
                id = 0,
                type = periodicTransaction.type,
                title = periodicTransaction.title,
                amount = periodicTransaction.amount,
                amountConverted = 0.0, // Converted later
                senderAccount = periodicTransaction.senderAccount,
                recipientAccount = periodicTransaction.recipientAccount,
                currency = periodicTransaction.currency,
                category = periodicTransaction.category,
                timestamp = periodicTransaction.nextTransaction
            )

            transactionRepo.process(newTransaction)

            if (periodicTransaction.intervalLength == 0) {
                // One-time transaction, delete it after processing
                periodicTransactionDao.deleteById(periodicTransaction.id)
            } else {
                // Reschedule next occurrence
                val calendar = Calendar.getInstance().apply {
                    time = periodicTransaction.nextTransaction
                    when (periodicTransaction.interval) {
                        ETransactionInterval.Days -> add(Calendar.DAY_OF_YEAR, periodicTransaction.intervalLength)
                        ETransactionInterval.Weeks -> add(Calendar.WEEK_OF_YEAR, periodicTransaction.intervalLength)
                        ETransactionInterval.Months -> add(Calendar.MONTH, periodicTransaction.intervalLength)
                    }
                }
                periodicTransaction.nextTransaction = calendar.time
                periodicTransactionDao.update(periodicTransaction)
            }
        }
    }

    /**
     * Processes all periodic transactions that are due.
     */
    override suspend fun processAllUnprocessed() {
        var unprocessed = getUnprocessed().first()
        while (unprocessed.isNotEmpty()) {
            for (periodicTransaction in unprocessed) {
                process(periodicTransaction)
            }
            unprocessed = getUnprocessed().first()
        }
    }

    /**
     * Retrieves an expanded version of a periodic transaction by ID.
     */
    override fun getExpandedById(id: Long): Flow<PeriodicTransactionExpanded> {
        return periodicTransactionDao.getExpandedById(id)
    }

    /**
     * Retrieves all expanded periodic transactions.
     */
    override fun getExpandedAll(): Flow<List<PeriodicTransactionExpanded>> {
        return periodicTransactionDao.getExpandedAll()
    }
}
