package spirit.realm.faefinance.ui.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.DatabaseApplication
import spirit.realm.faefinance.ui.components.Choice
import java.util.*

data class AccountFormState(
    val title: String = "",
    val currencyChoice: Choice = Choice(title = "", value = ""),
    val balance: String = "",
    val color: Color = Color.Unspecified,
    val errorMessage: String? = null,
    val isSubmitSuccessful: Boolean = false,
    val showErrorDialog: Boolean = false,
    val showDeleteDialog: Boolean = false
)

class AccountFormViewModel(
    private val app: DatabaseApplication,
    private val formAccount: Account
) : ViewModel() {

    private val _state = MutableStateFlow(AccountFormState(
        title = formAccount.title,
        currencyChoice = if (formAccount.currency == "") {
            Choice(
                title = "",
                value = ""
            )
        }
        else {
            val currency = Currency.getInstance(formAccount.currency)
            Choice(
                title = currency.displayName,
                value = currency.currencyCode,
                trailing = currency.symbol
            )
        },
        balance = formAccount.balance.toString(),
        color = formAccount.color
    ))
    val state: StateFlow<AccountFormState> = _state.asStateFlow()

    val currencyChoices: List<Choice>
        get() = Currency.getAvailableCurrencies()
            .filter { it.displayName.contains(_state.value.currencyChoice.title) }
            .sortedBy { it.displayName }
            .map { currency ->
                Choice(
                    currency.displayName,
                    currency.currencyCode,
                    trailing = currency.symbol
                )
            }

    fun updateTitle(newTitle: String) {
        _state.update { it.copy(title = newTitle) }
    }

    fun updateCurrencyChoice(newCurrencyChoice: Choice) {
        _state.update { it.copy(currencyChoice = newCurrencyChoice) }
    }

    fun updateBalance(newBalance: String) {
        _state.update { it.copy(balance = newBalance) }
    }

    fun updateColor(newColor: Color) {
        _state.update { it.copy(color = newColor) }
    }

    fun validateAndSubmit() {
        val s = _state.value
        val error = when {
            s.currencyChoice.value == "" -> "Please enter a valid currency"
            s.balance.toDoubleOrNull() == null -> "Please enter a valid number for balance"
            s.color == Color.Unspecified -> "Please select a valid color"
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
                app.container.accountRepository.insert(updatedAccount)
            } else {
                app.container.accountRepository.update(updatedAccount)
            }
            _state.update { it.copy(isSubmitSuccessful = true) }
        }
    }

    fun dismissErrorDialog() {
        _state.update { it.copy(showErrorDialog = false) }
    }

    fun triggerDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    fun dismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            app.container.accountRepository.delete(formAccount)
            _state.update { it.copy(isSubmitSuccessful = true) }
        }
    }
}
