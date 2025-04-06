package spirit.realm.faefinance.data.classes

import androidx.room.*
import java.util.Date

@Entity
data class Budget(
    @PrimaryKey val id: Int,
    var title: String,
    val currency: String, // ISO code
    var amount: Double,
    var amountSpent: Double,
    val startDate: Date,
    val endDate: Date,
    val interval: ETransactionInterval,
    val intervalLength: Int
)
