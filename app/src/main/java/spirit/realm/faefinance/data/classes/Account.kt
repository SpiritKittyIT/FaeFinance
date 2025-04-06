package spirit.realm.faefinance.data.classes

import androidx.room.*

@Entity
data class Account(
    @PrimaryKey val id: Int,
    var title: String,
    val currency: String, // ISO code
    var balance: Double,
    var color: String, // hex string
    var sortOrder: Int
)
