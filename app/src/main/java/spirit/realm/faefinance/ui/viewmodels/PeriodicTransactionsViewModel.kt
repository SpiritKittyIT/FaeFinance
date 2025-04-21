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

/**
 * Data class representing the state of the periodic transactions screen.
 * Holds a list of all periodic transactions that will be displayed.
 */
data class PeriodicTransactionsState(
    val periodicTransactions: List<PeriodicTransactionExpanded> = emptyList()
)

/**
 * ViewModel responsible for managing the state of periodic transactions.
 * It fetches the list of all periodic transactions and exposes it to the UI.
 */
class PeriodicTransactionsViewModel(
    private val periodicTransactionRepository: PeriodicTransactionRepository
) : ViewModel() {

    // Mutable state flow to hold the current state of periodic transactions
    private val _state = MutableStateFlow(PeriodicTransactionsState())
    val state: StateFlow<PeriodicTransactionsState> = _state.asStateFlow()

    // Initialization block to fetch the periodic transactions on ViewModel creation
    init {
        viewModelScope.launch {
            periodicTransactionRepository.getExpandedAll()
                .collect { list ->
                    _state.update { it.copy(periodicTransactions = list) }
                }
        }
    }
}
