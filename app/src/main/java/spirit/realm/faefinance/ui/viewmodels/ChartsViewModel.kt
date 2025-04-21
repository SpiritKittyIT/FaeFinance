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

data class ChartsState(
    val activeAccountId: Long = 0L,
    val intervalChoice: Choice = Choice(title = "", value = ""),
    val intervalLength: String = "6",
    val type: String = "",
    val groupedByCategory: List<Pair<String, Int>> = emptyList(),
    val totalTransactionCount: Int = 0,
)

class ChartsViewModel(
    resourceProvider: IAppResourceProvider,
    private val transactionRepository: TransactionRepository,
    private val settings: SettingsDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ChartsState())
    val state: StateFlow<ChartsState> = _state.asStateFlow()

    private var _transactions: List<TransactionExpanded> = emptyList()

    val intervalChoices = TransactionIntervalUtil.getChoices(resourceProvider)

    init {
        viewModelScope.launch {
            settings.activeAccountId.collect { id ->
                _state.update { it.copy(
                    activeAccountId = id
                ) }
            }
        }
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
            updateTransactions()
        }
    }

    fun updateIntervalChoice(choice: Choice) {
        _state.update { it.copy(intervalChoice = choice) }
        updateTransactions()
        viewModelScope.launch {
            settings.setChartsInterval(choice.value)
        }
    }

    fun updateIntervalLength(length: String) {
        _state.update { it.copy(intervalLength = length) }
        val lenInt: Int? = length.toIntOrNull()
        if (lenInt != null && lenInt >= 0) {
            updateTransactions()
            viewModelScope.launch {
                settings.setChartsIntervalLen(lenInt)
            }
        }
    }

    fun updateType(type: String) {
        _state.update { it.copy(type = type) }
        updateTransactions()
        viewModelScope.launch {
            settings.setChartsType(type)
        }
    }

    private fun calculateDateRange(): Pair<Date, Date>? {
        val len = _state.value.intervalLength.toIntOrNull() ?: return null
        if (len < 0) return null

        val interval = _state.value.intervalChoice.value
        val before = Date()
        val cal = java.util.Calendar.getInstance()
        cal.time = before

        when (interval) {
            ETransactionInterval.Days.name -> cal.add(java.util.Calendar.DAY_OF_YEAR, -len)
            ETransactionInterval.Weeks.name -> cal.add(java.util.Calendar.WEEK_OF_YEAR, -len)
            ETransactionInterval.Months.name -> cal.add(java.util.Calendar.MONTH, -len)
        }

        val after = cal.time
        return before to after
    }

    private fun updateTransactions() {
        val accountId = _state.value.activeAccountId
        val typeFilter = _state.value.type
        val dateRange = calculateDateRange() ?: return

        val (beforeDate, afterDate) = dateRange

        viewModelScope.launch {
            transactionRepository.getExpandedAllByAccountInterval(accountId, afterDate, beforeDate)
                .collect { transactions ->
                    val filtered = when (typeFilter) {
                        ETransactionType.Income.name -> transactions.filter { it.transaction.type == ETransactionType.Income }
                        ETransactionType.Expense.name -> transactions.filter { it.transaction.type == ETransactionType.Expense }
                        else -> transactions
                    }

                    _transactions = filtered

                    val grouped = filtered
                        .groupingBy { it.category.title }
                        .eachCount()
                        .toList()
                        .sortedBy { it.first }

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