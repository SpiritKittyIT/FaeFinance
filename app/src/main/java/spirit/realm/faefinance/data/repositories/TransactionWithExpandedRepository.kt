package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.daos.TransactionWithExpandedDao
import spirit.realm.faefinance.data.classes.TransactionWithExpanded

interface ITransactionWithExpandedRepository {
    suspend fun getTransactionWithExpanded(transactionId: Int): TransactionWithExpanded?
    suspend fun getAllTransactionsWithExpanded(): List<TransactionWithExpanded>
}

class TransactionWithExpandedRepository(private val dao: TransactionWithExpandedDao): ITransactionWithExpandedRepository {

    // Get a specific transaction with expanded senderAccount and category info by transactionId
    override suspend fun getTransactionWithExpanded(transactionId: Int): TransactionWithExpanded? {
        return dao.getTransactionWithExpanded(transactionId)
    }

    // Get all transactions with expanded senderAccount and category info
    override suspend fun getAllTransactionsWithExpanded(): List<TransactionWithExpanded> {
        return dao.getAllTransactionsWithExpanded()
    }
}
