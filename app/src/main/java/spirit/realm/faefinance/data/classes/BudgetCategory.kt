package spirit.realm.faefinance.data.classes

import androidx.room.*

@Entity(primaryKeys = ["budget", "category"])
data class BudgetCategory(
    val budget: Int, // FK to Budget.id
    val category: Int // FK to Category.id
)
