package spirit.realm.faefinance.ui.viewmodels

import spirit.realm.faefinance.ui.components.Choice
import java.util.Date
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.data.classes.PeriodicTransaction
import spirit.realm.faefinance.data.repositories.AccountRepository
import spirit.realm.faefinance.data.repositories.CategoryRepository
import spirit.realm.faefinance.data.repositories.PeriodicTransactionRepository
import spirit.realm.faefinance.ui.utility.AccountUtil
import spirit.realm.faefinance.ui.utility.CategoryUtil
import spirit.realm.faefinance.ui.utility.CurrencyUtil
import spirit.realm.faefinance.ui.utility.IAppResourceProvider
import spirit.realm.faefinance.ui.utility.TransactionIntervalUtil
import spirit.realm.faefinance.ui.utility.TransactionTypeUtil

data class PeriodicFormState(
    var typeChoice: Choice = Choice(title = "", value = ""),
    var title: String = "",
    var amount: String = "0",
    val senderAccountChoice: Choice = Choice(title = "", value = ""),
    val recipientAccountChoice: Choice = Choice(title = "", value = ""),
    val currencyChoice: Choice = Choice(title = "", value = ""),
    val categoryChoice: Choice = Choice(title = "", value = ""),
    var nextTransaction: Date = Date(),
    var intervalChoice: Choice = Choice(title = "", value = ""),
    var intervalLength: String = "0",

    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleteVisible: Boolean = false
)

class PeriodicFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: IAppResourceProvider,
    private val periodicTransactionRepository: PeriodicTransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    private val _state = MutableStateFlow(PeriodicFormState())
    val state: StateFlow<PeriodicFormState> = _state.asStateFlow()

    val transactionTypeChoices = TransactionTypeUtil.getChoices(resourceProvider)
    val intervalChoices = TransactionIntervalUtil.getChoices(resourceProvider)
    val currencyChoices = CurrencyUtil.currencyChoices

    private val _accountChoices = MutableStateFlow<List<Choice>>(emptyList())
    val accountChoices: StateFlow<List<Choice>> = _accountChoices.asStateFlow()

    private val _categoryChoices = MutableStateFlow<List<Choice>>(emptyList())
    val categoryChoices: StateFlow<List<Choice>> = _categoryChoices.asStateFlow()

    init {
        viewModelScope.launch {
            AccountUtil.getAccountChoices(accountRepository).collect {
                _accountChoices.value = it
            }
        }

        viewModelScope.launch {
            CategoryUtil.getCategoryChoices(categoryRepository).collect {
                _categoryChoices.value = it
            }
        }

        viewModelScope.launch {
            if (_itemId != 0L) {
                periodicTransactionRepository.getExpandedById(_itemId).first().let { expanded ->
                    _state.value = PeriodicFormState(
                        typeChoice = transactionTypeChoices.first { it.value == expanded.periodicTransaction.type.toString() },
                        title = expanded.periodicTransaction.title,
                        amount = expanded.periodicTransaction.amount.toString(),
                        senderAccountChoice = _accountChoices.value.first { it.value == expanded.senderAccount.id.toString() },
                        recipientAccountChoice = _accountChoices.value.first { it.value == expanded.recipientAccount.id.toString() },
                        currencyChoice = currencyChoices.first { it.value == expanded.periodicTransaction.currency },
                        categoryChoice = _categoryChoices.value.first { it.value == expanded.category.id.toString() },
                        nextTransaction = expanded.periodicTransaction.nextTransaction,
                        intervalChoice = intervalChoices.first { it.value == expanded.periodicTransaction.interval.name },
                        intervalLength = expanded.periodicTransaction.intervalLength.toString(),
                        isDeleteVisible = true
                    )
                }
            }
        }
    }

    // --- State Updates ---
    fun updateTypeChoice(choice: Choice) = _state.update { it.copy(typeChoice = choice) }
    fun updateTitle(title: String) = _state.update { it.copy(title = title) }
    fun updateAmount(amount: String) = _state.update { it.copy(amount = amount) }
    fun updateSenderAccount(choice: Choice) = _state.update { it.copy(senderAccountChoice = choice) }
    fun updateRecipientAccount(choice: Choice) = _state.update { it.copy(recipientAccountChoice = choice) }
    fun updateCurrencyChoice(choice: Choice) = _state.update { it.copy(currencyChoice = choice) }
    fun updateCategoryChoice(choice: Choice) = _state.update { it.copy(categoryChoice = choice) }
    fun updateNextTransaction(date: Date) = _state.update { it.copy(nextTransaction = date) }
    fun updateIntervalChoice(choice: Choice) = _state.update { it.copy(intervalChoice = choice) }
    fun updateIntervalLength(length: String) = _state.update { it.copy(intervalLength = length) }

    // --- Submit ---
    fun validateAndSubmit(navigateBack: () -> Unit) {
        val s = _state.value

        val error = when {
            s.typeChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_transaction_type_message)
            s.amount.toDoubleOrNull() == null -> resourceProvider.getString(R.string.invalid_amount_message)
            s.senderAccountChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_sender_account_message)
            s.typeChoice.value == ETransactionType.Transfer.toString() &&
                    s.recipientAccountChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_recipient_account_message)
            s.currencyChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_currency_message)
            s.categoryChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_category_message)
            s.intervalChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_interval_message)
            s.intervalLength.toIntOrNull() == null -> resourceProvider.getString(R.string.invalid_interval_length_message)
            else -> null
        }

        if (error != null) {
            _state.update { it.copy(errorMessage = error, showErrorDialog = true) }
            return
        }

        val model = PeriodicTransaction(
            id = _itemId,
            type = ETransactionType.valueOf(s.typeChoice.value),
            title = s.title,
            amount = s.amount.toDouble(),
            senderAccount = s.senderAccountChoice.value.toLong(),
            recipientAccount = s.recipientAccountChoice.value.toLongOrNull() ?: 0L,
            currency = s.currencyChoice.value,
            category = s.categoryChoice.value.toLong(),
            nextTransaction = s.nextTransaction,
            interval = ETransactionInterval.valueOf(s.intervalChoice.value),
            intervalLength = s.intervalLength.toInt()
        )

        navigateBack()
        viewModelScope.launch {
            if (_itemId == 0L) {
                periodicTransactionRepository.insert(model)
            } else {
                periodicTransactionRepository.update(model)
            }
        }
    }

    // --- Dialog Control ---
    fun dismissErrorDialog() = _state.update { it.copy(showErrorDialog = false) }
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    fun deleteItem(navigateBack: () -> Unit) {
        navigateBack()
        viewModelScope.launch {
            if (_itemId != 0L)
            {
                periodicTransactionRepository.deleteById(_itemId)
            }
        }
    }
}

