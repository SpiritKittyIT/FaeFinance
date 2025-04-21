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

/**
 * Data class representing the state of the Budget form screen.
 * Holds various properties related to the form fields and dialog states.
 */
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

/**
 * ViewModel class responsible for managing the state of the Budget form screen.
 * It handles retrieving, updating, and submitting budget data, along with dialog control.
 */
class BudgetFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: IAppResourceProvider,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Retrieves the budget item ID passed via the saved state; defaults to 0L if not provided
    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    // Mutable state flow to hold the state of the Budget form screen
    private val _state = MutableStateFlow(BudgetFormState())
    val state: StateFlow<BudgetFormState> = _state.asStateFlow()

    // Lists of available currency choices and interval choices
    val currencyChoices = CurrencyUtil.currencyChoices
    val intervalChoices = TransactionIntervalUtil.getChoices(resourceProvider)

    // State for holding categories fetched from the repository
    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList.asStateFlow()

    init {
        // Fetch categories and update the category list state
        viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _categoryList.value = categories
            }
        }

        // Fetch and populate budget form when editing an existing budget
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

    /**
     * Updates the title in the form state.
     */
    fun updateTitle(value: String) = _state.update { it.copy(title = value) }

    /**
     * Updates the amount in the form state.
     */
    fun updateAmount(value: String) = _state.update { it.copy(amount = value) }

    /**
     * Updates the start date in the form state and recalculates the end date.
     */
    fun updateStartDate(value: String) {
        _state.update { it.copy(startDate = value) }
        updateEndDate()
    }

    /**
     * Updates the selected interval choice and recalculates the end date.
     */
    fun updateIntervalChoice(choice: Choice) {
        _state.update { it.copy(intervalChoice = choice) }
        updateEndDate()
    }

    /**
     * Updates the interval length and recalculates the end date.
     */
    fun updateIntervalLength(length: String) {
        _state.update { it.copy(intervalLength = length) }
        updateEndDate()
    }

    /**
     * Updates the selected currency choice in the form state.
     */
    fun updateCurrencyChoice(choice: Choice) = _state.update { it.copy(currencyChoice = choice) }

    /**
     * Updates the selected categories in the form state.
     */
    fun updateCategoryChoices(selected: List<Category>) = _state.update { it.copy(categories = selected) }

    /**
     * Calculates and updates the end date based on the start date, interval choice, and interval length.
     */
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

    /**
     * Validates the form and submits the budget data.
     * If validation fails, it shows an error dialog with the appropriate message.
     * If validation is successful, the budget is saved or updated.
     */
    fun validateAndSubmit(navigateBack: () -> Unit) {
        val s = _state.value

        // Validation checks for required fields and format
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
            // If validation fails, show the error dialog
            _state.update { it.copy(errorMessage = error, showErrorDialog = true) }
            return
        }

        // Create a Budget object from the form state
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
            budgetSet = 0
        )

        val expanded = BudgetExpanded(
            budget = budget,
            categories = s.categories.toMutableList()
        )

        navigateBack()
        viewModelScope.launch {
            if (_itemId == 0L) {
                // Create a new budget if no ID is provided
                budgetRepository.createExpanded(expanded)
            } else {
                // Update the existing budget
                budgetRepository.updateExpanded(expanded)
            }
            budgetRepository.processAllDeferred()
        }
    }

    // --- Dialog Control ---

    /**
     * Dismisses the error dialog.
     */
    fun dismissErrorDialog() = _state.update { it.copy(showErrorDialog = false) }

    /**
     * Triggers the delete confirmation dialog.
     */
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }

    /**
     * Dismisses the delete confirmation dialog.
     */
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    /**
     * Triggers the category selection dialog.
     */
    fun showCategoryDialog() = _state.update { it.copy(showCategoryDialog = true) }

    /**
     * Dismisses the category selection dialog.
     */
    fun dismissCategoryDialog() = _state.update { it.copy(showCategoryDialog = false) }

    /**
     * Deletes the current budget item.
     * If the budget item has an ID, it will be deleted.
     */
    fun deleteItem(navigateBack: () -> Unit) {
        navigateBack()
        viewModelScope.launch {
            if (_itemId != 0L) {
                // Delete the budget item by its ID
                budgetRepository.deleteSetByLatestId(_itemId)
            }
        }
    }
}
