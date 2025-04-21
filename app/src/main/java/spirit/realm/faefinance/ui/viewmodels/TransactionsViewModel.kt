package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.data.classes.TransactionGroup
import spirit.realm.faefinance.data.repositories.TransactionRepository

/**
 * Data class to represent the state of the Transactions view.
 *
 * @param activeAccountId The ID of the currently active account.
 * @param groupedTransactions A list of transaction groups associated with the active account.
 */
data class TransactionsState(
    val activeAccountId: Long = 0L,
    val groupedTransactions: List<TransactionGroup> = emptyList()
)

/**
 * ViewModel for managing the state of transactions in the app.
 *
 * This ViewModel is responsible for interacting with the SettingsDataStore and the TransactionRepository
 * to manage the state of the active account and grouped transactions.
 */
class TransactionsViewModel(
    private val settings: SettingsDataStore,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    // Mutable state flow to hold the current state of the transactions.
    private val _state = MutableStateFlow(TransactionsState())

    // Public state flow to be observed by the UI.
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    init {
        // Launch a coroutine in the ViewModel's scope to observe changes in the active account ID
        viewModelScope.launch {
            settings.activeAccountId.collectLatest { id ->
                // Update the state with the new active account ID
                _state.update { it.copy(activeAccountId = id) }

                // Fetch the grouped transactions for the active account from the repository
                transactionRepository.getExpandedAllByAccountGrouped(id)
                    .collect { list ->
                        _state.update { it.copy(groupedTransactions = list) }
                    }
            }
        }
    }
}
