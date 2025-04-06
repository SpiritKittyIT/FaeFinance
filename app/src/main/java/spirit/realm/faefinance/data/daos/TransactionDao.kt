package spirit.realm.faefinance.data.daos
import androidx.room.*
import spirit.realm.faefinance.data.CurrencyConverter
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.data.classes.Transaction as DataTransaction
import java.util.*

@Dao
interface TransactionDao {
    // Basic CRUD operations for Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: DataTransaction)

    @Update
    suspend fun updateTransactionRecord(transaction: DataTransaction)

    @Delete
    suspend fun deleteTransactionRecord(transaction: DataTransaction)

    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    suspend fun getTransactionById(id: Int): DataTransaction?

    // --- Auxiliary functions to update related tables ---

    // Update the balance of an account by a delta amount.
    @Query("UPDATE Account SET balance = balance + :delta WHERE id = :accountId")
    suspend fun updateAccountBalance(accountId: Int, delta: Double)

    // Update the budgets' amountSpent for a given category and timestamp.
    @Query("""
        UPDATE Budget 
        SET amountSpent = amountSpent + :delta 
        WHERE id IN (
            SELECT budget FROM BudgetCategory WHERE category = :categoryId
        ) 
        AND :timestamp >= startDate 
        AND :timestamp < endDate
    """)
    suspend fun updateBudgetAmountSpent(categoryId: Int, timestamp: Date, delta: Double)

    // Retrieve the currency for a given account.
    @Query("SELECT currency FROM Account WHERE id = :accountId")
    suspend fun getAccountCurrency(accountId: Int): String

    // Apply account balance and budgets
    @Transaction
    suspend fun applyTransaction(transaction: DataTransaction) {
        if (transaction.type == ETransactionType.Transfer) {
            throw IllegalArgumentException("Transaction of type Transfer cannot be applied")
        }

        // Update account balance based on transaction type
        val delta: Double = if (transaction.type == ETransactionType.Expense) {
            -transaction.amountConverted
        }
        else {
            transaction.amountConverted
        }
        updateAccountBalance(transaction.senderAccount, delta)
        updateAccountBalance(1, delta)

        // If Expense, update budgets' amountSpent for relevant budgets
        if (transaction.type == ETransactionType.Expense) {
            updateBudgetAmountSpent(transaction.category, transaction.timestamp, transaction.amountConverted)
        }
    }

    // Revert account balance and budgets
    @Transaction
    suspend fun revertTransaction(transaction: DataTransaction) {
        if (transaction.type == ETransactionType.Transfer) {
            throw IllegalArgumentException("Transaction of type Transfer cannot be applied")
        }

        // Update account balance based on transaction type
        val delta: Double = if (transaction.type == ETransactionType.Expense) {
            transaction.amountConverted
        }
        else {
            -transaction.amountConverted
        }
        updateAccountBalance(transaction.senderAccount, delta)
        updateAccountBalance(1, delta)

        // If Expense, update budgets' amountSpent for relevant budgets
        if (transaction.type == ETransactionType.Expense) {
            updateBudgetAmountSpent(transaction.category, transaction.timestamp, -transaction.amountConverted)
        }
    }

    // --- Business logic methods ---

    /**
     * Creates a transaction.
     *
     * - Throws an exception if the transaction type is Transfer.
     * - If the transaction currency differs from the account currency, converts the amount.
     * - Inserts the transaction.
     * - Updates the account balance.
     * - If the transaction is an Expense, updates the amountSpent in relevant budgets.
     */
    @Transaction
    suspend fun createTransaction(transaction: DataTransaction) {
        if (transaction.type == ETransactionType.Transfer) {
            throw IllegalArgumentException("Transaction of type Transfer cannot be created directly")
        }

        // Get the account's currency
        val accountCurrency = getAccountCurrency(transaction.senderAccount)
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
        insertTransaction(transaction)

        // Update account balance and budgets
        applyTransaction(transaction)
    }

    /**
     * Updates a transaction.
     *
     * Reverts the effect of the old transaction then applies the new transaction’s effects.
     */
    @Transaction
    suspend fun updateTransaction(transaction: DataTransaction) {
        val oldTransaction = getTransactionById(transaction.id)
            ?: throw IllegalArgumentException("Transaction not found")

        // Revert effects of the old transaction:
        revertTransaction(oldTransaction)

        // Disallow updating to a Transfer type.
        if (transaction.type == ETransactionType.Transfer) {
            throw IllegalArgumentException("Transaction of type Transfer cannot be updated directly")
        }

        // Process the new transaction:
        val accountCurrency = getAccountCurrency(transaction.senderAccount)
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
        updateTransactionRecord(transaction)

        // Apply new effects:
        applyTransaction(transaction)
    }

    /**
     * Deletes a transaction.
     *
     * Reverts the transaction’s effects before deletion.
     */
    @Transaction
    suspend fun deleteTransaction(transaction: DataTransaction) {
        // Revert transaction effects
        revertTransaction(transaction)

        // Delete the transaction record
        deleteTransactionRecord(transaction)
    }

    /**
     * Processes a transaction
     *
     * If Transaction, creates two transactions:
     *  - An Expense for the sender account.
     *  - An Income for the recipient account.
     *
     * The original transaction’s details are used for both; the type and account are adjusted accordingly.
     */
    @Transaction
    suspend fun processTransaction(transaction: DataTransaction) {
        // Create non transfer transaction and return
        if (transaction.type != ETransactionType.Transfer) {
            createTransaction(transaction)
            return
        }

        // Create income transaction for the recipient account.
        val recipientTransaction = transaction.copy(
            id = 0, // Assume a new auto-generated id
            type = ETransactionType.Income,
            senderAccount = transaction.recipientAccount,
            recipientAccount = 0
        )
        createTransaction(recipientTransaction)

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
