package spirit.realm.faefinance.ui.viewmodels

import java.util.Date
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.classes.Category
import spirit.realm.faefinance.data.repositories.BudgetRepository

data class BudgetDetailState(
    val title: String = "",
    val currencyChoice: String = "",
    val amount: Double = 0.0,
    val amountSpent: Double = 0.0,
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val categories: List<Category> = emptyList(),
)

class BudgetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository,
) : ViewModel() {

    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    private val _state = MutableStateFlow(BudgetDetailState())
    val state: StateFlow<BudgetDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (_itemId != 0L) {
                budgetRepository.getExpandedById(_itemId).first().let { expanded ->
                    _state.value = BudgetDetailState(
                        title = expanded.budget.title,
                        currencyChoice = expanded.budget.currency,
                        amount = expanded.budget.amount,
                        amountSpent = expanded.budget.amountSpent,
                        startDate = expanded.budget.startDate,
                        endDate = expanded.budget.endDate,
                        categories = expanded.categories
                    )
                }
            }
        }
    }
}
