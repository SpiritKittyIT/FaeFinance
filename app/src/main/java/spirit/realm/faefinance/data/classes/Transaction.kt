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
    @PrimaryKey(autoGenerate = true) val id: Int,
    var type: ETransactionType,
    var title: String,
    var amount: Double,
    var amountConverted: Double,
    var senderAccount: Int, // FK to Account.id
    var recipientAccount: Int, // FK to Account.id
    var currency: String, // ISO code
    var category: Int, // FK to Category.id
    var timestamp: Date
)
