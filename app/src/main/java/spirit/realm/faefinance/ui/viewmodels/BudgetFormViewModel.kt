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
import spirit.realm.faefinance.ui.utility.CategoryUtil
import spirit.realm.faefinance.ui.utility.CurrencyUtil
import spirit.realm.faefinance.ui.utility.IAppResourceProvider
import spirit.realm.faefinance.ui.utility.TransactionIntervalUtil

data class BudgetFormState(
    val title: String = "",
    val currencyChoice: Choice = Choice(title = "", value = ""),
    val amount: String = "0",
    val amountSpent: String = "0",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val intervalChoice: Choice = Choice(title = "", value = ""),
    val intervalLength: String = "0",
    val categoryChoices: List<Choice> = emptyList(),

    val showErrorDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
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

    private val _categoryChoices = MutableStateFlow<List<Choice>>(emptyList())
    val categoryChoices: StateFlow<List<Choice>> = _categoryChoices.asStateFlow()

    init {
        viewModelScope.launch {
            CategoryUtil.getCategoryChoices(categoryRepository).collect {
                _categoryChoices.value = it
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
                        startDate = expanded.budget.startDate,
                        endDate = expanded.budget.endDate,
                        intervalChoice = intervalChoices.first { it.value == expanded.budget.interval.toString() },
                        intervalLength = expanded.budget.intervalLength.toString(),
                        categoryChoices = expanded.categories.map { cat ->
                            Choice(cat.title, cat.id.toString(), cat.symbol)
                        },
                        isDeleteVisible = true
                    )
                }
            }
        }
    }

    // --- State Updates ---
    fun updateTitle(value: String) = _state.update { it.copy(title = value) }
    fun updateAmount(value: String) = _state.update { it.copy(amount = value) }
    fun updateAmountSpent(value: String) = _state.update { it.copy(amountSpent = value) }
    fun updateStartDate(value: Date) = _state.update { it.copy(startDate = value) }
    fun updateEndDate(value: Date) = _state.update { it.copy(endDate = value) }
    fun updateIntervalChoice(choice: Choice) = _state.update { it.copy(intervalChoice = choice) }
    fun updateIntervalLength(value: String) = _state.update { it.copy(intervalLength = value) }
    fun updateCurrencyChoice(choice: Choice) = _state.update { it.copy(currencyChoice = choice) }
    fun updateCategoryChoices(choices: List<Choice>) = _state.update { it.copy(categoryChoices = choices) }

    // --- Submit ---
    fun validateAndSubmit(navigateBack: () -> Unit) {
        val s = _state.value

        val error = when {
            s.amount.toDoubleOrNull() == null -> resourceProvider.getString(R.string.invalid_amount_message)
            s.currencyChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_currency_message)
            s.categoryChoices.isEmpty() -> resourceProvider.getString(R.string.invalid_category_message)
            s.intervalChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_interval_message)
            s.intervalLength.toIntOrNull() == null -> resourceProvider.getString(R.string.invalid_interval_length_message)
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
            startDate = s.startDate,
            endDate = s.endDate,
            interval = ETransactionInterval.valueOf(s.intervalChoice.value),
            intervalLength = s.intervalLength.toInt(),
            budgetSet = 0 // you can add this if you have a selection for BudgetSet
        )

        val expanded = BudgetExpanded(
            budget = budget,
            categories = s.categoryChoices.map { choice ->
                Category(
                    id = choice.value.toLong(),
                    title = choice.title,
                    symbol = choice.trailing
                )
            }.toMutableList()
        )

        navigateBack()
        viewModelScope.launch {
            if (_itemId == 0L) {
                budgetRepository.createExpanded(expanded)
            } else {
                budgetRepository.updateExpanded(expanded)
            }
        }
    }

    // --- Dialog Control ---
    fun dismissErrorDialog() = _state.update { it.copy(showErrorDialog = false) }
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    fun deleteItem(navigateBack: () -> Unit) {
        navigateBack()
        viewModelScope.launch {
            if (_itemId != 0L)
            {
                budgetRepository.deleteById(_itemId)
            }
        }
    }
}
