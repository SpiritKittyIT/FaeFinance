package spirit.realm.faefinance.data.daos
import androidx.room.*
import spirit.realm.faefinance.data.classes.Budget
import spirit.realm.faefinance.data.classes.ETransactionInterval
import java.util.*

@Dao
interface BudgetDao {
    // Insert a new budget or update it if it already exists (using the primary key)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    // Insert multiple budgets
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<Budget>)

    // Update an existing budget
    @Update
    suspend fun update(budget: Budget)

    // Delete a specific budget
    @Delete
    suspend fun delete(budget: Budget)

    // Get a budget by its ID
    @Query("SELECT * FROM budget WHERE id = :id")
    suspend fun getBudgetById(id: Int): Budget?

    // Get all budgets for a specific currency, ordered by startDate
    @Query("SELECT * FROM budget WHERE currency = :currency ORDER BY startDate ASC")
    suspend fun getBudgetsByCurrency(currency: String): List<Budget>

    // Get all budgets
    @Query("SELECT * FROM budget ORDER BY startDate ASC")
    suspend fun getAllBudgets(): List<Budget>

    // Function to create the next budget based on the interval
    @Transaction
    suspend fun createNext(currentBudget: Budget): Budget {
        // Calculate the next budget's start and end dates
        val calendar = Calendar.getInstance()
        calendar.time = currentBudget.endDate

        // Apply the intervalLength to determine the next start and end dates
        when (currentBudget.interval) {
            ETransactionInterval.Days -> calendar.add(Calendar.DAY_OF_YEAR, currentBudget.intervalLength)
            ETransactionInterval.Weeks -> calendar.add(Calendar.WEEK_OF_YEAR, currentBudget.intervalLength)
            ETransactionInterval.Months -> calendar.add(Calendar.MONTH, currentBudget.intervalLength)
        }

        val nextStartDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, currentBudget.intervalLength)  // Calculate the end date
        val nextEndDate = calendar.time

        // Create the new budget as a copy of the current one
        val nextBudget = currentBudget.copy(
            id = 0,  // Make sure to generate a new ID
            startDate = nextStartDate,
            endDate = nextEndDate,
            amountSpent = 0.0
        )

        // Insert the new budget into the database
        insert(nextBudget)

        return nextBudget
    }
}
