package spirit.realm.faefinance.ui.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.data.repositories.AccountRepository
import spirit.realm.faefinance.ui.utility.CurrencyUtil
import spirit.realm.faefinance.ui.components.Choice
import spirit.realm.faefinance.ui.utility.IAppResourceProvider
import java.util.*

/**
 * Represents the form state of an account.
 *
 * Holds the various pieces of data needed to edit or create a new account, including the
 * title, currency, balance, color, and sort order. It also manages error messages, dialog visibility,
 * and delete options.
 *
 * @property title The title of the account.
 * @property currencyChoice The currency choice for the account.
 * @property balance The balance of the account as a string.
 * @property color The color representing the account.
 * @property sortOrder The order in which the account appears, as a string.
 * @property errorMessage An optional error message to display.
 * @property showErrorDialog A flag indicating if the error dialog should be shown.
 * @property showDeleteDialog A flag indicating if the delete dialog should be shown.
 * @property isDeleteVisible A flag indicating if the delete option is visible.
 */
data class AccountFormState(
    val title: String = "",
    val currencyChoice: Choice = Choice(title = "", value = ""),
    val balance: String = "0",
    val color: Color = Color.Unspecified,
    val sortOrder: String = Long.MAX_VALUE.toString(),

    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleteVisible: Boolean = false
)

/**
 * ViewModel for managing the state of the AccountForm screen.
 *
 * This ViewModel handles fetching and updating account data, validating input, and managing
 * UI dialogs for errors and deletion. It supports the creation and editing of accounts.
 *
 * @param savedStateHandle The [SavedStateHandle] used to retrieve saved arguments (e.g., account ID).
 * @param resourceProvider The [IAppResourceProvider] for fetching localized strings.
 * @param accountRepository The [AccountRepository] for interacting with account data.
 */
class AccountFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: IAppResourceProvider,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    // The account ID, defaulting to 0L (indicating a new account if 0L)
    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    // State flow holding the current state of the account form
    private val _state = MutableStateFlow(AccountFormState())
    val state: StateFlow<AccountFormState> = _state.asStateFlow()

    // List of available currency choices
    val currencyChoices = CurrencyUtil.currencyChoices

    init {
        viewModelScope.launch {
            // If we're editing an existing account, populate the form with its details
            if (_itemId == 0L) {
                _state.value = AccountFormState()
            } else {
                accountRepository.getById(_itemId).first().let { account ->
                    // Set the initial state for editing an existing account
                    _state.value = AccountFormState(
                        title = account.title,
                        currencyChoice =  account.currency.takeIf { it.isNotEmpty() }?.let {
                            val currency = Currency.getInstance(it)
                            Choice(
                                title = currency.displayName,
                                value = currency.currencyCode,
                                trailing = currency.symbol
                            )
                        } ?: Choice(title = "", value = ""),
                        balance = account.balance.toString(),
                        color = account.color,
                        sortOrder = account.sortOrder.toString(),
                        isDeleteVisible = true
                    )
                }
            }
        }
    }

    // --- State Updates ---

    /**
     * Updates the title of the account.
     */
    fun updateTitle(newTitle: String) = _state.update { it.copy(title = newTitle) }

    /**
     * Updates the currency choice for the account.
     */
    fun updateCurrencyChoice(newCurrencyChoice: Choice) = _state.update { it.copy(currencyChoice = newCurrencyChoice) }

    /**
     * Updates the balance of the account.
     */
    fun updateBalance(newBalance: String) = _state.update { it.copy(balance = newBalance) }

    /**
     * Updates the color of the account.
     */
    fun updateColor(newColor: Color) = _state.update { it.copy(color = newColor) }

    // --- Submit ---

    /**
     * Validates the input and submits the form to create or update the account.
     *
     * If there are any validation errors, an error message will be displayed.
     * If the account is new, it will be inserted into the repository. If it is being edited, it will be updated.
     *
     * @param navigateBack The function to call when navigation should happen (after form submission).
     */
    fun validateAndSubmit(navigateBack: () -> Unit) {
        val s = _state.value
        val error = when {
            s.currencyChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_currency_message)
            s.balance.toDoubleOrNull() == null -> resourceProvider.getString(R.string.invalid_balance_message)
            s.color == Color.Unspecified -> resourceProvider.getString(R.string.invalid_color_message)
            s.sortOrder.toDoubleOrNull() == null -> resourceProvider.getString(R.string.invalid_sort_order_message)
            else -> null
        }

        // If validation fails, show the error dialog and exit
        if (error != null) {
            _state.update { it.copy(errorMessage = error, showErrorDialog = true) }
            return
        }

        // Create or update the account
        val updatedAccount = Account(
            id = _itemId,
            title = s.title,
            currency = s.currencyChoice.value,
            balance = s.balance.toDouble(),
            color = s.color,
            sortOrder = s.sortOrder.toLong()
        )

        navigateBack()
        viewModelScope.launch {
            if (updatedAccount.id == 0L) {
                // Insert new account
                accountRepository.insert(updatedAccount)
            } else {
                // Update existing account
                accountRepository.update(updatedAccount)
            }
        }
    }

    // --- Dialog Control ---

    /**
     * Dismisses the error dialog.
     */
    fun dismissErrorDialog() = _state.update { it.copy(showErrorDialog = false) }

    /**
     * Triggers the delete dialog to be shown.
     */
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }

    /**
     * Dismisses the delete dialog.
     */
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    /**
     * Deletes the current account.
     *
     * After the account is deleted, the navigation action is triggered.
     *
     * @param navigateBack The function to call when navigation should happen after deletion.
     */
    fun deleteItem(navigateBack: () -> Unit) {
        navigateBack()
        viewModelScope.launch {
            if (_itemId != 0L) {
                // Delete the account from the repository
                accountRepository.deleteById(_itemId)
            }
        }
    }
}
