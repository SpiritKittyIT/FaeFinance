package spirit.realm.faefinance.data.classes

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionExpanded(
    @Embedded val transaction: Transaction,

    @Relation(
        parentColumn = "senderAccount",
        entityColumn = "id"
    )
    val senderAccount: Account,

    @Relation(
        parentColumn = "recipientAccount",
        entityColumn = "id"
    )
    val recipientAccount: Account?,

    @Relation(
        parentColumn = "category",
        entityColumn = "id"
    )
    val category: Category
)
