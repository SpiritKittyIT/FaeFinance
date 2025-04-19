package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.classes.PeriodicTransactionExpanded
import spirit.realm.faefinance.data.repositories.PeriodicTransactionRepository

data class PeriodicTransactionsState(
    val periodicTransactions: List<PeriodicTransactionExpanded> = emptyList()
)

class PeriodicTransactionsViewModel(
    private val periodicTransactionRepository: PeriodicTransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PeriodicTransactionsState())
    val state: StateFlow<PeriodicTransactionsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            periodicTransactionRepository.getExpandedAll()
                .collect { list ->
                    _state.update { it.copy(periodicTransactions = list) }
                }
        }
    }
}
