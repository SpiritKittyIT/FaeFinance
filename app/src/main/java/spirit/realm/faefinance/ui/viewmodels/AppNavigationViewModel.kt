package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.data.repositories.AccountRepository
import spirit.realm.faefinance.data.repositories.BudgetRepository
import spirit.realm.faefinance.data.repositories.PeriodicTransactionRepository
import java.util.Currency

/**
 * Represents the state of the app's navigation and settings.
 *
 * Holds information about the currently active account, a list of accounts, and the app's theme preference.
 * Also provides an action to submit a form.
 *
 * @property activeAccountId The ID of the active account.
 * @property accountTitle The title of the active account.
 * @property accountBalance The balance of the active account, formatted as a string.
 * @property accounts A list of all accounts.
 * @property isDarkTheme A boolean representing whether the app is in dark mode.
 * @property formSubmit A lambda function for submitting a form.
 */
data class AppNavigationState(
    val activeAccountId: Long = 0L,
    val accountTitle: String? = null,
    val accountBalance: String = "",
    val accounts: List<Account> = emptyList<Account>(),
    val isDarkTheme: Boolean = false,
    val formSubmit: () -> Unit = {},
)

class AppNavigationViewModel(
    private val accountRepository: AccountRepository,
    private val periodicTransactionRepository: PeriodicTransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val settings: SettingsDataStore
) : ViewModel() {

    // Mutable state flow holding the current navigation state
    private val _state = MutableStateFlow(AppNavigationState())
    val state: StateFlow<AppNavigationState> = _state.asStateFlow()

    init {
        // Initialize accounts, theme, and active account on startup
        initAccounts()
        initTheme()
        initActiveAccount()
    }

    /**
     * Initializes the list of accounts and updates the state with the account data.
     */
    private fun initAccounts() {
        viewModelScope.launch {
            accountRepository.getAll().collect { accounts ->
                _state.update { it.copy(accounts = accounts) }
            }
        }
    }

    /**
     * Initializes the app's theme setting and updates the state with the current theme preference.
     */
    private fun initTheme() {
        viewModelScope.launch {
            settings.isDarkTheme.collect { isDark ->
                _state.update { it.copy(isDarkTheme = isDark) }
            }
        }
    }

    /**
     * Initializes the active account based on the ID stored in the settings.
     * Updates the state with the active account's information.
     */
    private fun initActiveAccount() {
        viewModelScope.launch {
            settings.activeAccountId.collect { id ->
                updateActiveAccount(id)
            }
        }
    }

    /**
     * Updates the active account in the settings and the state.
     *
     * @param id The ID of the account to set as active.
     */
    fun updateActiveAccount(id: Long) {
        viewModelScope.launch {
            settings.setActiveAccountId(id)

            // If the ID is non-zero, fetch the account details and update the state
            if (id != 0L) {
                accountRepository.getById(id).collect { account ->
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
            } else {
                // Reset the active account details when the ID is 0L
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

    /**
     * Sets the app's dark theme preference.
     *
     * @param enabled A boolean value indicating whether dark theme should be enabled.
     */
    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settings.setDarkTheme(enabled)
        }
    }

    /**
     * Updates the order of the accounts in the repository.
     *
     * This method loops through the updated list of accounts, and updates their `sortOrder`
     * values based on their index in the list.
     *
     * @param updated A list of accounts with updated order.
     */
    fun updateAccountOrder(updated: List<Account>) {
        viewModelScope.launch {
            updated.forEachIndexed { i, acc ->
                if (acc.sortOrder != i.toLong()) {
                    accountRepository.update(acc.copy(sortOrder = i.toLong()))
                }
            }
        }
    }

    /**
     * Sets the action to be called when a form is submitted.
     *
     * This method allows setting a custom action for submitting forms in the app.
     *
     * @param action The action to execute when submitting a form.
     */
    fun setFormSubmitAction(action: () -> Unit) {
        _state.update { it.copy(formSubmit = action) }
    }

    /**
     * Launches background tasks for processing all unprocessed periodic transactions and deferred budgets.
     */
    fun onLaunch() {
        viewModelScope.launch {
            periodicTransactionRepository.processAllUnprocessed()
        }
        viewModelScope.launch {
            budgetRepository.processAllDeferred()
        }
    }
}
