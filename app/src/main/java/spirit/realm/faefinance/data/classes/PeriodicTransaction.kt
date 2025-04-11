package spirit.realm.faefinance.data.classes

import androidx.room.*
import java.util.Date

@Entity(
    indices = [
        Index(value = ["nextTransaction"])
    ]
)
data class PeriodicTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var type: ETransactionType,
    var title: String,
    var amount: Double,
    var senderAccount: Int, // FK to Account.id
    var recipientAccount: Int, // FK to Account.id
    var currency: String, // ISO code
    var category: Int, // FK to Category.id
    var nextTransaction: Date,
    var interval: ETransactionInterval,
    var intervalLength: Int
)
