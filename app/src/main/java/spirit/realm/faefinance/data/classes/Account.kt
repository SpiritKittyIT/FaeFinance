package spirit.realm.faefinance.data.classes

import androidx.compose.ui.graphics.Color
import androidx.room.*

@Entity(
    indices = [
        Index(value = ["currency"])
    ]
)
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String = "",
    var currency: String = "", // ISO code
    var balance: Double = 0.0,
    var color: Color = Color.White,
    var sortOrder: Long = Long.MAX_VALUE
)
