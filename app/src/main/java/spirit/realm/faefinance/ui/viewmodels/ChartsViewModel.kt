package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.data.classes.TransactionExpanded
import spirit.realm.faefinance.data.repositories.TransactionRepository
import spirit.realm.faefinance.ui.components.Choice
import spirit.realm.faefinance.ui.utility.IAppResourceProvider
import spirit.realm.faefinance.ui.utility.TransactionIntervalUtil
import java.util.Date

/**
 * Data class representing the state of the charts screen.
 * It contains various properties that control the chart's behavior,
 * such as the active account, interval choice, and the filtered transactions.
 */
data class ChartsState(
    val activeAccountId: Long = 0L,
    val intervalChoice: Choice = Choice(title = "", value = ""),
    val intervalLength: String = "6",
    val type: String = "",
    val groupedByCategory: List<Pair<String, Int>> = emptyList(),
    val totalTransactionCount: Int = 0,
)

/**
 * ViewModel class responsible for managing the charts screen state.
 * It allows for adjusting the time interval and transaction type while displaying grouped transactions.
 */
class ChartsViewModel(
    resourceProvider: IAppResourceProvider,
    private val transactionRepository: TransactionRepository,
    private val settings: SettingsDataStore
) : ViewModel() {

    // Mutable state flow to hold the current state of the charts screen
    private val _state = MutableStateFlow(ChartsState())
    val state: StateFlow<ChartsState> = _state.asStateFlow()

    // List to hold all transactions that match the current filter criteria
    private var _transactions: List<TransactionExpanded> = emptyList()

    // Available choices for the time intervals to be used in the chart
    val intervalChoices = TransactionIntervalUtil.getChoices(resourceProvider)

    init {
        // Collect and update the active account ID whenever it changes
        viewModelScope.launch {
            settings.activeAccountId.collect { id ->
                _state.update { it.copy(activeAccountId = id) }
            }
        }

        // Retrieve and apply saved settings for charts (interval, length, and type)
        viewModelScope.launch {
            settings.chartsInterval.first().let { value ->
                updateIntervalChoice(intervalChoices.first { it.value == value })
            }
            settings.chartsIntervalLen.first().let { value ->
                updateIntervalLength(value.toString())
            }
            settings.chartsType.first().let { value ->
                updateType(value)
            }
            updateTransactions() // Fetch and update the transactions initially
        }
    }

    // --- State Updates ---

    /**
     * Updates the interval choice and triggers a refresh of the transactions.
     * @param choice The selected interval choice (e.g., days, weeks, months).
     */
    fun updateIntervalChoice(choice: Choice) {
        _state.update { it.copy(intervalChoice = choice) }
        updateTransactions() // Refresh the transactions after changing the interval
        viewModelScope.launch {
            settings.setChartsInterval(choice.value) // Save the new interval choice in settings
        }
    }

    /**
     * Updates the interval length and triggers a refresh of the transactions.
     * @param length The new interval length (in numeric form).
     */
    fun updateIntervalLength(length: String) {
        _state.update { it.copy(intervalLength = length) }
        val lenInt: Int? = length.toIntOrNull()
        if (lenInt != null && lenInt >= 0) {
            updateTransactions() // Refresh the transactions after changing the length
            viewModelScope.launch {
                settings.setChartsIntervalLen(lenInt) // Save the new interval length in settings
            }
        }
    }

    /**
     * Updates the transaction type (either income or expense) and triggers a refresh of the transactions.
     * @param type The new transaction type to be shown on the chart (income or expense).
     */
    fun updateType(type: String) {
        _state.update { it.copy(type = type) }
        updateTransactions() // Refresh the transactions after changing the type
        viewModelScope.launch {
            settings.setChartsType(type) // Save the new type in settings
        }
    }

    // --- Date Calculation ---

    /**
     * Calculates the date range for the transaction filter based on the interval and length.
     * @return A pair of dates representing the date range for the filter, or null if the range cannot be calculated.
     */
    private fun calculateDateRange(): Pair<Date, Date>? {
        val len = _state.value.intervalLength.toIntOrNull() ?: return null
        if (len < 0) return null

        val interval = _state.value.intervalChoice.value
        val before = Date()
        val cal = java.util.Calendar.getInstance()
        cal.time = before

        // Adjust the date based on the selected interval (days, weeks, or months)
        when (interval) {
            ETransactionInterval.Days.name -> cal.add(java.util.Calendar.DAY_OF_YEAR, -len)
            ETransactionInterval.Weeks.name -> cal.add(java.util.Calendar.WEEK_OF_YEAR, -len)
            ETransactionInterval.Months.name -> cal.add(java.util.Calendar.MONTH, -len)
        }

        val after = cal.time
        return before to after
    }

    // --- Transaction Filtering ---

    /**
     * Updates the list of transactions by applying the selected filters (date range, type, and account).
     */
    private fun updateTransactions() {
        val accountId = _state.value.activeAccountId
        val typeFilter = _state.value.type
        val dateRange = calculateDateRange() ?: return

        val (beforeDate, afterDate) = dateRange

        viewModelScope.launch {
            transactionRepository.getExpandedAllByAccountInterval(accountId, afterDate, beforeDate)
                .collect { transactions ->
                    // Filter transactions based on the selected type (income, expense, or both)
                    val filtered = when (typeFilter) {
                        ETransactionType.Income.name -> transactions.filter { it.transaction.type == ETransactionType.Income }
                        ETransactionType.Expense.name -> transactions.filter { it.transaction.type == ETransactionType.Expense }
                        else -> transactions
                    }

                    _transactions = filtered

                    // Group filtered transactions by category and count their occurrences
                    val grouped = filtered
                        .groupingBy { it.category.title }
                        .eachCount()
                        .toList()
                        .sortedBy { it.first }

                    // Update the state with the grouped data and transaction count
                    _state.update {
                        it.copy(
                            groupedByCategory = grouped,
                            totalTransactionCount = filtered.size
                        )
                    }
                }
        }
    }
}
