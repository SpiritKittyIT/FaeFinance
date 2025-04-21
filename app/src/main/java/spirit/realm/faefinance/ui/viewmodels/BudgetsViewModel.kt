package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.classes.BudgetExpanded
import spirit.realm.faefinance.data.repositories.BudgetRepository

/**
 * Data class representing the state of the Budgets screen.
 * Holds the list of budget items to be displayed.
 */
data class BudgetsState(
    val budgets: List<BudgetExpanded> = emptyList()
)

/**
 * ViewModel class responsible for managing the state of the Budgets screen.
 * It interacts with the BudgetRepository to fetch and filter budgets, and updates the state.
 */
class BudgetsViewModel(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetsState())
    val state: StateFlow<BudgetsState> = _state.asStateFlow()

    init {
        // Launch a coroutine to fetch and filter the budgets
        viewModelScope.launch {
            budgetRepository.getExpandedAll()
                .collect { list ->
                    val filtered = list.groupBy { it.budget.budgetSet }
                        .mapNotNull { (_, group) ->
                            group.maxByOrNull { it.budget.startDate.time }
                        }
                    _state.update { it.copy(budgets = filtered) }
                }
        }
    }
}
