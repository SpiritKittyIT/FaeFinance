package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.classes.BudgetExpanded
import spirit.realm.faefinance.data.repositories.BudgetRepository

/**
 * Data class representing the state of the BudgetDetail screen.
 * Contains a list of [BudgetExpanded] objects which represent the expanded budget information.
 */
data class BudgetDetailState(
    val budgets: List<BudgetExpanded> = emptyList() // List of expanded budgets
)

/**
 * ViewModel class responsible for managing the data for the BudgetDetail screen.
 * It retrieves budget data and updates the state accordingly.
 */
class BudgetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository,
) : ViewModel() {

    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    private val _state = MutableStateFlow(BudgetDetailState())
    val state: StateFlow<BudgetDetailState> = _state.asStateFlow()

    init {
        // Launch a coroutine to load the budget details when the ViewModel is created
        viewModelScope.launch {
            if (_itemId != 0L) {
                val item = budgetRepository.getById(_itemId).first()
                budgetRepository.getExpandedAllInSet(item.budgetSet).collect { budgetList ->
                    _state.update { it.copy(budgets = budgetList.sortedByDescending { it.budget.endDate }) }
                }
            }
        }
    }
}
