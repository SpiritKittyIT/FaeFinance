package spirit.realm.faefinance.data.daos
import androidx.room.*
import spirit.realm.faefinance.data.classes.BudgetWithCategories
import java.util.Date

@Dao
interface BudgetWithCategoriesDao {

    @Transaction
    @Query("""
        SELECT * FROM Budget 
        WHERE :timestamp >= startDate 
          AND :timestamp < endDate 
          AND id IN (
              SELECT budget FROM BudgetCategory WHERE category = :categoryId
          )
    """)
    suspend fun getBudgetsWithCategory(timestamp: Date, categoryId: Int): List<BudgetWithCategories>

    @Transaction
    @Query("SELECT * FROM Budget WHERE id = :budgetId")
    suspend fun getBudgetWithCategoriesById(budgetId: Int): BudgetWithCategories?
}