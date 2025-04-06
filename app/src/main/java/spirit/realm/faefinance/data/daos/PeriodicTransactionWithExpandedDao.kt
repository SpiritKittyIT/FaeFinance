package spirit.realm.faefinance.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import spirit.realm.faefinance.data.classes.PeriodicTransactionWithExpanded

@Dao
interface PeriodicTransactionWithExpandedDao {

    // Get a specific periodic transaction with expanded senderAccount, recipientAccount, and category
    @Transaction
    @Query("SELECT * FROM PeriodicTransaction WHERE id = :periodicTransactionId")
    suspend fun getPeriodicTransactionWithExpanded(periodicTransactionId: Int): PeriodicTransactionWithExpanded?

    // Get all periodic transactions with expanded senderAccount, recipientAccount, and category
    @Transaction
    @Query("SELECT * FROM PeriodicTransaction")
    suspend fun getAllPeriodicTransactionsWithExpanded(): List<PeriodicTransactionWithExpanded>
}
