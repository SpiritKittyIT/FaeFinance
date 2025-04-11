package spirit.realm.faefinance.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import spirit.realm.faefinance.data.AppDatabase
import spirit.realm.faefinance.data.CurrencyConverter
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.data.classes.TransactionExpanded
import spirit.realm.faefinance.data.classes.TransactionGroup
import spirit.realm.faefinance.data.daos.AccountDao
import spirit.realm.faefinance.data.daos.BudgetDao
import spirit.realm.faefinance.data.classes.Transaction as DataTransaction
import spirit.realm.faefinance.data.daos.TransactionDao
import java.util.Calendar

interface ITransactionRepository {
    suspend fun update(transaction: DataTransaction)
    suspend fun delete(transaction: DataTransaction)
    suspend fun process(transaction: DataTransaction)
    suspend fun deleteAllByAccount(accountId: Int)
    fun getExpandedAllByAccountGrouped(accountId: Int): Flow<Map<TransactionGroup, List<TransactionExpanded>>>
}

class TransactionRepository(
    private val db: AppDatabase,
    private val transactionDao: TransactionDao = db.transactionDao(),
    private val budgetDao: BudgetDao = db.budgetDao(),
    private val accountDao: AccountDao = db.accountDao()
) : ITransactionRepository {
    // Apply/revert account balance and budget
    private suspend fun apply(transaction: DataTransaction, revert: Boolean = false) {
        val modifier = if (revert) { -1 } else { 1 }
        val typeModifier = if (transaction.type == ETransactionType.Expense) { -1 } else { 1 }

        if (transaction.type == ETransactionType.Transfer) {
            throw IllegalArgumentException("Transaction of type Transfer cannot be applied")
        }

        // Update account balance
        accountDao.updateBalance(transaction.senderAccount, transaction.amountConverted * modifier * typeModifier)

        // Update budgets balance
        if (transaction.type == ETransactionType.Expense) {
            val budgets = budgetDao.getWithCategory(transaction.timestamp, transaction.category).first()
            for (budget in budgets) {
                val amountConverted = if (transaction.currency != budget.currency) {
                    CurrencyConverter.convertCurrency(transaction.amount, transaction.currency, budget.currency)
                }
                else {
                    transaction.amount
                }
                budgetDao.updateAmountSpent(budget.id, amountConverted * modifier)
            }
        }
    }

    private suspend fun createTransaction(transaction: DataTransaction) {
        if (transaction.type == ETransactionType.Transfer) {
            throw IllegalArgumentException("Transaction of type Transfer cannot be created directly")
        }

        // Get the account's currency
        val accountCurrency = accountDao.getById(transaction.senderAccount).first().currency

        // If different, convert the amount
        transaction.amountConverted = if (transaction.currency != accountCurrency) {
            CurrencyConverter.convertCurrency(
                transaction.amount,
                transaction.currency,
                accountCurrency
            )
        } else {
            transaction.amount
        }

        // Insert the transaction record
        transactionDao.insert(transaction)

        // Update account balance and budgets
        apply(transaction)
    }

    // public

    override suspend fun update(transaction: DataTransaction) {
        db.withTransaction {
            val oldTransaction = transactionDao.getById(transaction.id).first()

            // Revert effects of the old transaction:
            apply(oldTransaction, true)

            // Disallow updating to a Transfer type.
            if (transaction.type == ETransactionType.Transfer) {
                throw IllegalArgumentException("Transaction of type Transfer cannot be updated directly")
            }

            // Get the account's currency
            val accountCurrency = accountDao.getById(transaction.senderAccount).first().currency

            // Process the new transaction:
            transaction.amountConverted = if (transaction.currency != accountCurrency) {
                CurrencyConverter.convertCurrency(
                    transaction.amount,
                    transaction.currency,
                    accountCurrency
                )
            } else {
                transaction.amount
            }

            // Update the transaction record.
            transactionDao.update(transaction)

            // Apply new effects:
            apply(transaction)
        }
    }

    override suspend fun delete(transaction: DataTransaction) {
        db.withTransaction {
            // Revert transaction effects
            apply(transaction, true)

            // Delete the transaction record
            transactionDao.delete(transaction)
        }
    }

    override suspend fun process(transaction: DataTransaction) {
        db.withTransaction {
            // Create non transfer transaction and return
            if (transaction.type != ETransactionType.Transfer) {
                createTransaction(transaction)
            }
            else {
                // Create income transaction for the recipient account.
                val incomeTransaction = transaction.copy(
                    id = 0, // Assume a new auto-generated id
                    type = ETransactionType.Income,
                    senderAccount = transaction.recipientAccount,
                    recipientAccount = 0
                )
                createTransaction(incomeTransaction)

                // Create expense transaction for the sender account.
                val expenseTransaction = transaction.copy(
                    id = 0, // Assume a new auto-generated id
                    type = ETransactionType.Expense,
                    senderAccount = transaction.senderAccount,
                    recipientAccount = 0
                )
                createTransaction(expenseTransaction)
            }
        }
    }

    override suspend fun deleteAllByAccount(accountId: Int) {
        val transactions = transactionDao.getAllByAccount(accountId).first()

        for (transaction in transactions) {
            delete(transaction)
        }
    }

    override fun getExpandedAllByAccountGrouped(accountId: Int): Flow<Map<TransactionGroup, List<TransactionExpanded>>> {
        return transactionDao.getExpandedAllByAccount(accountId).map { list ->
            list.groupBy {
                val cal = Calendar.getInstance().apply { time = it.transaction.timestamp }
                TransactionGroup(
                    year = cal.get(Calendar.YEAR),
                    month = cal.get(Calendar.MONTH) + 1 // calendar months are 0-based
                )
            }.toSortedMap(compareByDescending<TransactionGroup> { it.year }
                .thenByDescending { it.month })
        }
    }
}
