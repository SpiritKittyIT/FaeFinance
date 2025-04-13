package spirit.realm.faefinance.data.classes

import androidx.room.*

@Entity(
    indices = [
        Index(value = ["currency"])
    ]
)
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var title: String,
    var currency: String, // ISO code
    var balance: Double,
    var color: String, // hex string
    var sortOrder: Int
)
