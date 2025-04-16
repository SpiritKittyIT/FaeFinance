package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import spirit.realm.faefinance.DatabaseApplication
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.data.classes.Account
import java.util.Currency

data class AppNavigationState(
    val activeAccountId: Long = 0L,
    val accountTitle: String? = null,
    val accountBalance: String = "",
    val accounts: List<Account> = emptyList<Account>(),
    val isDarkTheme: Boolean = false,
)

data class FormsState(
    val formSubmit: () -> Unit = {},
    val formAccount: Account = Account()
)

class AppNavigationViewModel(
    private val app: DatabaseApplication,
    private val settings: SettingsDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(AppNavigationState())
    val state: StateFlow<AppNavigationState> = _state.asStateFlow()

    private val _formsState = MutableStateFlow(FormsState())
    val formsState: StateFlow<FormsState> = _formsState.asStateFlow()

    init {
        initAccounts()
        initTheme()
        initActiveAccount()
    }

    private fun initAccounts() {
        viewModelScope.launch {
            app.container.accountRepository.getAll().collect { accounts ->
                _state.update { it.copy(accounts = accounts) }
            }
        }
    }

    private fun initTheme() {
        viewModelScope.launch {
            settings.isDarkTheme.collect { isDark ->
                _state.update { it.copy(isDarkTheme = isDark) }
            }
        }
    }

    private fun initActiveAccount() {
        viewModelScope.launch {
            settings.activeAccountId.collect { id ->
                updateActiveAccount(id)
            }
        }
    }

    fun updateActiveAccount(id: Long) {
        viewModelScope.launch {
            settings.setActiveAccountId(id)

            if (id != 0L) {
                app.container.accountRepository.getById(id).collect { account ->
                    val formattedBalance = try {
                        val symbol = Currency.getInstance(account.currency).symbol
                        "${"%.2f".format(account.balance)} $symbol"
                    } catch (_: Exception) {
                        "%.2f".format(account.balance)
                    }

                    _state.update {
                        it.copy(
                            activeAccountId = account.id,
                            accountTitle = account.title,
                            accountBalance = formattedBalance
                        )
                    }
                }
            }
            else {
                _state.update {
                    it.copy(
                        activeAccountId = 0L,
                        accountTitle = null,
                        accountBalance = ""
                    )
                }
            }
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settings.setDarkTheme(enabled)
        }
    }

    fun updateAccountOrder(updated: List<Account>) {
        viewModelScope.launch {
            updated.forEachIndexed { i, acc ->
                if (acc.sortOrder != i.toLong()) {
                    app.container.accountRepository.update(acc.copy(sortOrder = i.toLong()))
                }
            }
        }
    }

    fun setFormSubmitAction(action: () -> Unit) {
        _formsState.update { it.copy(formSubmit = action) }
    }

    fun setFormAccount(formAccount: Account) {
        _formsState.update { it.copy(formAccount = formAccount) }
    }
}
