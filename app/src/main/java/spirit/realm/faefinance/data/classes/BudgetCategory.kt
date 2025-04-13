package spirit.realm.faefinance.data.classes

import androidx.room.*

@Entity(
    primaryKeys = ["budget", "category"],
    indices = [
        Index(value = ["budget"]),
        Index(value = ["category"])
    ]
)
data class BudgetCategory(
    val budget: Long, // FK to Budget.id
    val category: Long // FK to Category.id
)
