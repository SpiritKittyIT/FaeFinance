package spirit.realm.faefinance.ui.viewmodels

import spirit.realm.faefinance.ui.components.Choice
import java.util.Date
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.Budget
import spirit.realm.faefinance.data.classes.BudgetExpanded
import spirit.realm.faefinance.data.classes.Category
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.data.repositories.BudgetRepository
import spirit.realm.faefinance.data.repositories.CategoryRepository
import spirit.realm.faefinance.ui.utility.CurrencyUtil
import spirit.realm.faefinance.ui.utility.DateFormatterUtil
import spirit.realm.faefinance.ui.utility.IAppResourceProvider
import spirit.realm.faefinance.ui.utility.TransactionIntervalUtil

data class BudgetFormState(
    val title: String = "",
    val currencyChoice: Choice = Choice(title = "", value = ""),
    val amount: String = "0",
    val amountSpent: String = "0",
    val startDate: String = DateFormatterUtil.format(Date()),
    val endDate: Date = Date(),
    val intervalChoice: Choice = Choice(title = "", value = ""),
    val intervalLength: String = "0",
    val categories: List<Category> = emptyList(),

    val showErrorDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showCategoryDialog: Boolean = false,
    val isDeleteVisible: Boolean = false,
    val errorMessage: String? = null
)

class BudgetFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: IAppResourceProvider,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    private val _state = MutableStateFlow(BudgetFormState())
    val state: StateFlow<BudgetFormState> = _state.asStateFlow()

    val currencyChoices = CurrencyUtil.currencyChoices
    val intervalChoices = TransactionIntervalUtil.getChoices(resourceProvider)

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _categoryList.value = categories
            }
        }

        viewModelScope.launch {
            if (_itemId != 0L) {
                budgetRepository.getExpandedById(_itemId).first().let { expanded ->
                    _state.value = BudgetFormState(
                        title = expanded.budget.title,
                        currencyChoice = currencyChoices.first { it.value == expanded.budget.currency },
                        amount = expanded.budget.amount.toString(),
                        amountSpent = expanded.budget.amountSpent.toString(),
                        startDate = DateFormatterUtil.format(expanded.budget.startDate),
                        endDate = expanded.budget.endDate,
                        intervalChoice = intervalChoices.first { it.value == expanded.budget.interval.toString() },
                        intervalLength = expanded.budget.intervalLength.toString(),
                        categories = expanded.categories,
                        isDeleteVisible = true
                    )
                }
            }
        }
    }

    // --- State Updates ---
    fun updateTitle(value: String) = _state.update { it.copy(title = value) }
    fun updateAmount(value: String) = _state.update { it.copy(amount = value) }
    fun updateStartDate(value: String) {
        _state.update { it.copy(startDate = value) }
        updateEndDate()
    }
    fun updateIntervalChoice(choice: Choice) {
        _state.update { it.copy(intervalChoice = choice) }
        updateEndDate()
    }
    fun updateIntervalLength(length: String) {
        _state.update { it.copy(intervalLength = length) }
        updateEndDate()
    }
    fun updateCurrencyChoice(choice: Choice) = _state.update { it.copy(currencyChoice = choice) }
    fun updateCategoryChoices(selected: List<Category>) = _state.update { it.copy(categories = selected) }

    private fun updateEndDate() {
        val startDate = DateFormatterUtil.tryParse(_state.value.startDate)
        val intervalLength = _state.value.intervalLength.toIntOrNull()
        val interval = _state.value.intervalChoice.value

        if (startDate != null && intervalLength != null && interval.isNotBlank()) {
            val calendar = java.util.Calendar.getInstance().apply {
                time = startDate
                when (ETransactionInterval.valueOf(interval)) {
                    ETransactionInterval.Days -> add(java.util.Calendar.DAY_OF_YEAR, intervalLength)
                    ETransactionInterval.Weeks -> add(java.util.Calendar.WEEK_OF_YEAR, intervalLength)
                    ETransactionInterval.Months -> add(java.util.Calendar.MONTH, intervalLength)
                }
            }

            val newEndDate = calendar.time

            _state.update { it.copy(endDate = newEndDate) }
        }
    }

    // --- Submit ---
    fun validateAndSubmit(navigateBack: () -> Unit) {
        val s = _state.value

        val error = when {
            s.amount.toDoubleOrNull() == null -> resourceProvider.getString(R.string.invalid_amount_message)
            s.currencyChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_currency_message)
            s.categories.isEmpty() -> resourceProvider.getString(R.string.invalid_category_message)
            s.intervalChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_interval_message)
            s.intervalLength.toIntOrNull() == null -> resourceProvider.getString(R.string.invalid_interval_length_message)
            DateFormatterUtil.tryParse(s.startDate) == null -> resourceProvider.getString(R.string.invalid_start_date_message)
            else -> null
        }

        if (error != null) {
            _state.update { it.copy(errorMessage = error, showErrorDialog = true) }
            return
        }

        val budget = Budget(
            id = _itemId,
            title = s.title,
            currency = s.currencyChoice.value,
            amount = s.amount.toDouble(),
            amountSpent = s.amountSpent.toDoubleOrNull() ?: 0.0,
            startDate = DateFormatterUtil.tryParse(s.startDate)!!,
            endDate = s.endDate,
            interval = ETransactionInterval.valueOf(s.intervalChoice.value),
            intervalLength = s.intervalLength.toInt(),
            budgetSet = 0 // you can add this if you have a selection for BudgetSet
        )

        val expanded = BudgetExpanded(
            budget = budget,
            categories = s.categories.toMutableList()
        )

        navigateBack()
        viewModelScope.launch {
            if (_itemId == 0L) {
                budgetRepository.createExpanded(expanded)
            } else {
                budgetRepository.updateExpanded(expanded)
            }
            budgetRepository.processAllDeferred()
        }
    }

    // --- Dialog Control ---
    fun dismissErrorDialog() = _state.update { it.copy(showErrorDialog = false) }
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }
    fun showCategoryDialog() = _state.update { it.copy(showCategoryDialog = true) }
    fun dismissCategoryDialog() = _state.update { it.copy(showCategoryDialog = false) }

    fun deleteItem(navigateBack: () -> Unit) {
        navigateBack()
        viewModelScope.launch {
            if (_itemId != 0L)
            {
                budgetRepository.deleteSetByLatestId(_itemId)
            }
        }
    }
}
