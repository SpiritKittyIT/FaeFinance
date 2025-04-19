package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.data.repositories.TransactionRepository

class TransactionsViewModel(
    private val settings: SettingsDataStore,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

}