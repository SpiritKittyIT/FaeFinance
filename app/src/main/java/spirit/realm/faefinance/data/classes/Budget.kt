package spirit.realm.faefinance.data.classes

import androidx.room.*
import java.util.Date

@Entity(
    indices = [
        Index(value = ["startDate"]),
        Index(value = ["endDate"])
    ]
)
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var budgetSet: Int = 0,
    var title: String,
    val currency: String, // ISO code
    var amount: Double,
    var amountSpent: Double,
    val startDate: Date,
    val endDate: Date,
    val interval: ETransactionInterval,
    val intervalLength: Int
)
