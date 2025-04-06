package spirit.realm.faefinance.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import spirit.realm.faefinance.data.classes.TransactionWithExpanded

@Dao
interface TransactionWithExpandedDao {

    // Get a specific transaction with expanded senderAccount and category info
    @Transaction
    @Query("SELECT * FROM `Transaction` WHERE id = :transactionId")
    suspend fun getTransactionWithExpanded(transactionId: Int): TransactionWithExpanded?

    // Get all transactions with expanded senderAccount and category info
    @Transaction
    @Query("SELECT * FROM `Transaction`")
    suspend fun getAllTransactionsWithExpanded(): List<TransactionWithExpanded>
}
