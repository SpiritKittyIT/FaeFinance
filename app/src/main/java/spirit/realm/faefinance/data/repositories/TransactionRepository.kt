package spirit.realm.faefinance.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import spirit.realm.faefinance.data.AppDatabase
import spirit.realm.faefinance.data.CurrencyConverter
import spirit.realm.faefinance.data.classes.*
import spirit.realm.faefinance.data.daos.AccountDao
import spirit.realm.faefinance.data.daos.BudgetDao
import spirit.realm.faefinance.data.daos.TransactionDao
import java.util.*

interface ITransactionRepository {
    fun getById(id: Long): Flow<Transaction>
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    suspend fun deleteById(id: Long)
    suspend fun process(transaction: Transaction)
    suspend fun deleteAllByAccount(accountId: Long)
    fun getExpandedById(id: Long): Flow<TransactionExpanded>
    fun getExpandedAllByAccountGrouped(accountId: Long): Flow<List<TransactionGroup>>
    fun getExpandedAllByAccountInterval(accountId: Long, after: Date, before: Date): Flow<List<TransactionExpanded>>
}

class TransactionRepository(
    private val db: AppDatabase,
    private val transactionDao: TransactionDao = db.transactionDao(),
    private val budgetDao: BudgetDao = db.budgetDao(),
    private val accountDao: AccountDao = db.accountDao()
) : ITransactionRepository {

    /**
     * Applies or reverts the financial impact of a transaction on account balances and budgets.
     * @param revert if true, undoes the transaction effects.
     */
    private suspend fun apply(transaction: Transaction, revert: Boolean = false) {
        val modifier = if (revert) -1 else 1
        val typeModifier = if (transaction.type == ETransactionType.Expense) -1 else 1

        if (transaction.type == ETransactionType.Transfer) {
            throw IllegalArgumentException("Transfer transactions must be split before applying.")
        }

        // Adjust the sender account balance
        accountDao.updateBalance(
            transaction.senderAccount,
            transaction.amountConverted * modifier * typeModifier
        )

        // Update any related budgets if it's an expense
        if (transaction.type == ETransactionType.Expense) {
            val budgets = budgetDao.getWithCategory(transaction.timestamp, transaction.category).first()
            for (budget in budgets) {
                val converted = if (transaction.currency != budget.currency) {
                    CurrencyConverter.convertCurrency(transaction.amount, transaction.currency, budget.currency)
                } else transaction.amount

                budgetDao.updateAmountSpent(budget.id, converted * modifier)
            }
        }
    }

    /**
     * Inserts a new transaction into the database and updates all related balances.
     */
    private suspend fun createTransaction(transaction: Transaction) {
        if (transaction.type == ETransactionType.Transfer) {
            throw IllegalArgumentException("Direct creation of transfers is not allowed.")
        }

        // Convert amount to the sender's currency if needed
        val accountCurrency = accountDao.getById(transaction.senderAccount).first().currency
        transaction.amountConverted = if (transaction.currency != accountCurrency) {
            CurrencyConverter.convertCurrency(transaction.amount, transaction.currency, accountCurrency)
        } else transaction.amount

        // Insert and apply the transaction
        transactionDao.insert(transaction)
        apply(transaction)
    }

    // --- Public API implementations ---

    override fun getById(id: Long): Flow<Transaction> {
        return transactionDao.getById(id)
    }

    /**
     * Updates an existing transaction and reflects changes in balances and budgets.
     */
    override suspend fun update(transaction: Transaction) {
        db.withTransaction {
            val oldTransaction = transactionDao.getById(transaction.id).first()

            // Revert old effects
            apply(oldTransaction, revert = true)

            if (transaction.type == ETransactionType.Transfer) {
                throw IllegalArgumentException("Cannot update transaction to a Transfer type.")
            }

            // Convert amount if currency changed
            val accountCurrency = accountDao.getById(transaction.senderAccount).first().currency
            transaction.amountConverted = if (transaction.currency != accountCurrency) {
                CurrencyConverter.convertCurrency(transaction.amount, transaction.currency, accountCurrency)
            } else transaction.amount

            // Save and apply updated transaction
            transactionDao.update(transaction)
            apply(transaction)
        }
    }

    /**
     * Deletes a transaction by ID, reverting its effects.
     */
    override suspend fun deleteById(id: Long) {
        db.withTransaction {
            val transaction = transactionDao.getById(id).first()
            apply(transaction, revert = true)
            transactionDao.deleteById(id)
        }
    }

    /**
     * Deletes a transaction and reverts its impact.
     */
    override suspend fun delete(transaction: Transaction) {
        db.withTransaction {
            apply(transaction, revert = true)
            transactionDao.deleteById(transaction.id)
        }
    }

    /**
     * Processes a new transaction. If it's a transfer, it will create a pair of internal transactions.
     */
    override suspend fun process(transaction: Transaction) {
        db.withTransaction {
            if (transaction.type != ETransactionType.Transfer) {
                createTransaction(transaction)
            } else {
                // Handle transfer as separate income/expense
                transaction.recipientAccount?.let {
                    val incomeTransaction = transaction.copy(
                        id = 0,
                        type = ETransactionType.Income,
                        senderAccount = it,
                        recipientAccount = transaction.senderAccount
                    )
                    createTransaction(incomeTransaction)
                }

                val expenseTransaction = transaction.copy(
                    id = 0,
                    type = ETransactionType.Expense
                )
                createTransaction(expenseTransaction)
            }
        }
    }

    /**
     * Deletes all transactions linked to an account, reverting each one.
     */
    override suspend fun deleteAllByAccount(accountId: Long) {
        val transactions = transactionDao.getAllByAccount(accountId).first()
        for (transaction in transactions) {
            delete(transaction)
        }
    }

    override fun getExpandedById(id: Long): Flow<TransactionExpanded> {
        return transactionDao.getExpandedById(id)
    }

    /**
     * Groups transactions by year and month for display purposes.
     */
    override fun getExpandedAllByAccountGrouped(accountId: Long): Flow<List<TransactionGroup>> {
        return transactionDao.getExpandedAllByAccount(accountId).map { list ->
            list.groupBy {
                val cal = Calendar.getInstance().apply { time = it.transaction.timestamp }
                TransactionGroupDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
            }.toSortedMap(
                compareByDescending<TransactionGroupDate> { it.year }
                    .thenByDescending { it.month }
            ).map { (date, transactions) ->
                TransactionGroup(groupDate = date, accounts = transactions)
            }
        }
    }

    /**
     * Fetches all transactions for an account within a date range.
     */
    override fun getExpandedAllByAccountInterval(accountId: Long, after: Date, before: Date): Flow<List<TransactionExpanded>> {
        return transactionDao.getExpandedAllByAccountInterval(accountId, after, before)
    }
}
