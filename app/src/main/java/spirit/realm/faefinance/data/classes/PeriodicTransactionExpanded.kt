package spirit.realm.faefinance.data.classes

import androidx.room.Embedded
import androidx.room.Relation

data class PeriodicTransactionExpanded(
    @Embedded val periodicTransaction: PeriodicTransaction,

    // Expanded sender account details
    @Relation(parentColumn = "senderAccount", entityColumn = "id")
    val senderAccount: Account,

    // Expanded recipient account details
    @Relation(parentColumn = "recipientAccount", entityColumn = "id")
    val recipientAccount: Account?,

    // Expanded category details
    @Relation(parentColumn = "category", entityColumn = "id")
    val category: Category
)
