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

data class BudgetsState(
    val budgets: List<BudgetExpanded> = emptyList()
)

class BudgetsViewModel(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetsState())
    val state: StateFlow<BudgetsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            budgetRepository.getExpandedAll()
                .collect { list ->
                    _state.update { it.copy(budgets = list) }
                }
        }
    }
}
