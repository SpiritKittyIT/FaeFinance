package spirit.realm.faefinance.data.repositories

import spirit.realm.faefinance.data.daos.PeriodicTransactionWithExpandedDao
import spirit.realm.faefinance.data.classes.PeriodicTransactionWithExpanded

interface IPeriodicTransactionWithExpandedRepository {
    suspend fun getPeriodicTransactionWithExpanded(periodicTransactionId: Int): PeriodicTransactionWithExpanded?
    suspend fun getAllPeriodicTransactionsWithExpanded(): List<PeriodicTransactionWithExpanded>
}

class PeriodicTransactionWithExpandedRepository(
    private val dao: PeriodicTransactionWithExpandedDao
) : IPeriodicTransactionWithExpandedRepository {

    // Get a specific periodic transaction with expanded details
    override suspend fun getPeriodicTransactionWithExpanded(periodicTransactionId: Int): PeriodicTransactionWithExpanded? {
        return dao.getPeriodicTransactionWithExpanded(periodicTransactionId)
    }

    // Get all periodic transactions with expanded details
    override suspend fun getAllPeriodicTransactionsWithExpanded(): List<PeriodicTransactionWithExpanded> {
        return dao.getAllPeriodicTransactionsWithExpanded()
    }
}
