package spirit.realm.faefinance.data.classes

import androidx.room.*

@Entity
data class Category(
    @PrimaryKey val id: Int,
    val title: String,
    val symbol: String
)
