package spirit.realm.faefinance.data.classes

import androidx.room.*

data class BudgetExpanded(
    @Embedded val budget: Budget,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BudgetCategory::class,
            parentColumn = "budget",
            entityColumn = "category"
        )
    )
    var categories: MutableList<Category>
)
