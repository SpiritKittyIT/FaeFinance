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

data class AccountFormState(
    val title: String = "",
    val currencyChoice: Choice = Choice(title = "", value = ""),
    val balance: String = "0",
    val color: Color = Color.Unspecified,

    val errorMessage: String? = null,
    val isSubmitSuccessful: Boolean = false,
    val showErrorDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleteVisible: Boolean = false
)

class AccountFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: IAppResourceProvider,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val accountId: Long = savedStateHandle["id"] ?: 0L

    private val _state = MutableStateFlow(AccountFormState())
    val state: StateFlow<AccountFormState> = _state.asStateFlow()

    val currencyChoices = CurrencyUtil.currencyChoices

    private lateinit var formAccount: Account

    init {
        viewModelScope.launch {
            if (accountId == 0L) {
                formAccount = Account()
                _state.value = AccountFormState()
            } else {
                accountRepository.getById(accountId).collect { account ->
                    formAccount = account
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
                        isDeleteVisible = true
                    )
                }
            }
        }
    }

    // --- State Updates ---
    fun updateTitle(newTitle: String) = _state.update { it.copy(title = newTitle) }
    fun updateCurrencyChoice(newCurrencyChoice: Choice) = _state.update { it.copy(currencyChoice = newCurrencyChoice) }
    fun updateBalance(newBalance: String) = _state.update { it.copy(balance = newBalance) }
    fun updateColor(newColor: Color) = _state.update { it.copy(color = newColor) }

    // --- Submit ---
    fun validateAndSubmit() {
        val s = _state.value
        val error = when {
            s.currencyChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_currency_message)
            s.balance.toDoubleOrNull() == null -> resourceProvider.getString(R.string.invalid_balance_message)
            s.color == Color.Unspecified -> resourceProvider.getString(R.string.invalid_color_message)
            else -> null
        }

        if (error != null) {
            _state.update { it.copy(errorMessage = error, showErrorDialog = true) }
            return
        }

        val updatedAccount = formAccount.copy(
            title = s.title,
            currency = s.currencyChoice.value,
            balance = s.balance.toDouble(),
            color = s.color
        )

        viewModelScope.launch {
            if (updatedAccount.id == 0L) {
                accountRepository.insert(updatedAccount)
            } else {
                accountRepository.update(updatedAccount)
            }
            _state.update { it.copy(isSubmitSuccessful = true) }
        }
    }

    // --- Dialog Control ---
    fun dismissErrorDialog() = _state.update { it.copy(showErrorDialog = false) }
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    fun deleteAccount() {
        viewModelScope.launch {
            accountRepository.delete(formAccount)
            _state.update { it.copy(isSubmitSuccessful = true) }
        }
    }
}
