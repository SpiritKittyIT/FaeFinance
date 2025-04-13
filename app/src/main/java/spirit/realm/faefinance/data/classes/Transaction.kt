package spirit.realm.faefinance.data.classes

import androidx.room.*
import java.util.Date

@Entity(
    indices = [
        Index(value = ["senderAccount"]),
        Index(value = ["recipientAccount"]),
        Index(value = ["timestamp"]),
        Index(value = ["category"])
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var type: ETransactionType,
    var title: String,
    var amount: Double,
    var amountConverted: Double,
    var senderAccount: Long, // FK to Account.id
    var recipientAccount: Long, // FK to Account.id
    var currency: String, // ISO code
    var category: Long, // FK to Category.id
    var timestamp: Date
)
