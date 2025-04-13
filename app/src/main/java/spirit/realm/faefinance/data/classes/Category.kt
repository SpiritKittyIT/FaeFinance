package spirit.realm.faefinance.data.classes

import androidx.room.*

@Entity(
    indices = [
        Index(value = ["title"])
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val symbol: String
)
