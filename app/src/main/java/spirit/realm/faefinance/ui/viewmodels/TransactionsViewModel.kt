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

data class TransactionsState(
    val activeAccountId: Long = 0L,
    val groupedTransactions: List<TransactionGroup> = emptyList()
)

class TransactionsViewModel(
    private val settings: SettingsDataStore,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionsState())
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settings.activeAccountId.collectLatest { id ->
                _state.update { it.copy(activeAccountId = id) }

                transactionRepository.getExpandedAllByAccountGrouped(id)
                    .collect { list ->
                        _state.update { it.copy(groupedTransactions = list) }
                    }
            }
        }
    }
}
