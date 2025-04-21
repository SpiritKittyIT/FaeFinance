package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import spirit.realm.faefinance.data.classes.Transaction
import spirit.realm.faefinance.data.repositories.AccountRepository
import spirit.realm.faefinance.data.repositories.CategoryRepository
import spirit.realm.faefinance.ui.utility.AccountUtil
import spirit.realm.faefinance.ui.utility.CategoryUtil
import spirit.realm.faefinance.ui.utility.CurrencyUtil
import spirit.realm.faefinance.ui.components.Choice
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.data.repositories.TransactionRepository
import spirit.realm.faefinance.ui.utility.DateFormatterUtil
import spirit.realm.faefinance.ui.utility.IAppResourceProvider
import spirit.realm.faefinance.ui.utility.TransactionTypeUtil
import java.util.Currency
import java.util.Date

/**
 * Data class representing the state of the transaction form.
 * This contains all the fields that the user can fill out when creating or updating a transaction.
 */
data class TransactionFormState(
    var typeChoice: Choice = Choice(title = "", value = ""),
    var title: String = "",
    var amount: String = "0",
    val senderAccountChoice: Choice = Choice(title = "", value = ""),
    val recipientAccountChoice: Choice = Choice(title = "", value = ""),
    val currencyChoice: Choice = Choice(title = "", value = ""),
    val categoryChoice: Choice = Choice(title = "", value = ""),
    var timestamp: String = DateFormatterUtil.format(Date()),

    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleteVisible: Boolean = false
)

/**
 * ViewModel for managing the state and logic of a transaction form.
 * It interacts with repositories to fetch account, category, and transaction data,
 * as well as to submit or delete transactions.
 */
class TransactionFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: IAppResourceProvider,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Item ID for the transaction (used for editing an existing transaction)
    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    // State flow to hold and observe the state of the transaction form
    private val _state = MutableStateFlow(TransactionFormState())
    val state: StateFlow<TransactionFormState> = _state.asStateFlow()

    // Choices for currency and transaction types
    val currencyChoices = CurrencyUtil.currencyChoices

    val transactionTypeChoices = TransactionTypeUtil
        .getChoices(resourceProvider)
        .filter {
            // Exclude 'Transfer' type if editing an existing transaction
            if (_itemId != 0L) it.value != ETransactionType.Transfer.toString() else true
        }

    // State flows for account and category choices (populated from the repositories)
    private val _accountChoices = MutableStateFlow<List<Choice>>(emptyList())
    val accountChoices: StateFlow<List<Choice>> = _accountChoices.asStateFlow()

    private val _categoryChoices = MutableStateFlow<List<Choice>>(emptyList())
    val categoryChoices: StateFlow<List<Choice>> = _categoryChoices.asStateFlow()

    // Initialize the ViewModel by fetching account and category choices,
    // and if editing an existing transaction, load its details.
    init {
        viewModelScope.launch {
            // Fetch account choices from the account repository
            AccountUtil.getAccountChoices(accountRepository).collect {
                _accountChoices.value = it
            }
        }

        viewModelScope.launch {
            // Fetch category choices from the category repository
            CategoryUtil.getCategoryChoices(categoryRepository).collect {
                _categoryChoices.value = it
            }
        }

        viewModelScope.launch {
            // If editing, load the transaction details and update the state
            if (_itemId != 0L) {
                transactionRepository.getExpandedById(_itemId).first().let { transactionExpanded ->
                    _state.value = TransactionFormState(
                        typeChoice = transactionTypeChoices.first { it.value == transactionExpanded.transaction.type.toString() },
                        title = transactionExpanded.transaction.title,
                        amount = transactionExpanded.transaction.amount.toString(),
                        currencyChoice = Choice(
                            title = Currency.getInstance(transactionExpanded.transaction.currency).displayName,
                            value = transactionExpanded.transaction.currency,
                            trailing = Currency.getInstance(transactionExpanded.transaction.currency).symbol
                        ),
                        senderAccountChoice = Choice(
                            title = transactionExpanded.senderAccount.title,
                            value = transactionExpanded.senderAccount.id.toString(),
                            trailing = Currency.getInstance(transactionExpanded.senderAccount.currency).symbol
                        ),
                        recipientAccountChoice = if (transactionExpanded.recipientAccount == null)
                            Choice(title = "", value = "")
                        else Choice(
                            title = transactionExpanded.recipientAccount.title,
                            value = transactionExpanded.recipientAccount.id.toString(),
                            trailing = Currency.getInstance(transactionExpanded.recipientAccount.currency).symbol
                        ),
                        categoryChoice = Choice(
                            title = transactionExpanded.category.title,
                            value = transactionExpanded.category.id.toString(),
                            trailing = transactionExpanded.category.symbol,
                        ),
                        timestamp = DateFormatterUtil.format(transactionExpanded.transaction.timestamp),
                        isDeleteVisible = true
                    )
                }
            }
        }
    }

    // --- State Update Functions ---
    fun updateTransactionTypeChoice(newChoice: Choice) = _state.update { state -> state.copy(typeChoice = newChoice) }
    fun updateTitle(newTitle: String) = _state.update { state -> state.copy(title = newTitle) }
    fun updateAmount(newAmount: String) = _state.update { state -> state.copy(amount = newAmount) }

    // Updates the sender account choice and conditionally updates the currency choice based on the selected account
    fun updateSenderAccountChoice(newChoice: Choice) {
        viewModelScope.launch {
            _state.update { currentState ->
                val shouldUpdateCurrency = currentState.currencyChoice.value.isBlank()
                val newCurrencyChoice = if (shouldUpdateCurrency) {
                    val account = accountRepository.getById(newChoice.value.toLongOrNull() ?: 0L).first()
                    val currency = Currency.getInstance(account.currency)
                    Choice(
                        title = currency.displayName,
                        value = currency.currencyCode,
                        trailing = currency.symbol
                    )
                } else {
                    currentState.currencyChoice
                }

                currentState.copy(
                    senderAccountChoice = newChoice,
                    currencyChoice = newCurrencyChoice
                )
            }
        }
    }

    fun updateRecipientAccountChoice(newChoice: Choice) = _state.update { state -> state.copy(recipientAccountChoice = newChoice) }
    fun updateCurrencyChoice(newChoice: Choice) = _state.update { state -> state.copy(currencyChoice = newChoice) }
    fun updateCategoryChoice(newChoice: Choice) = _state.update { state -> state.copy(categoryChoice = newChoice) }
    fun updateTimestamp(newTimestamp: String) = _state.update { state -> state.copy(timestamp = newTimestamp) }

    // --- Submit Function ---
    fun validateAndSubmit(navigateBack: () -> Unit) {
        val s = _state.value
        val error = when {
            s.typeChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_transaction_type_message)
            s.amount.toDoubleOrNull() == null -> resourceProvider.getString(R.string.invalid_amount_message)
            s.senderAccountChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_sender_account_message)
            s.typeChoice.value == ETransactionType.Transfer.toString()
                    && s.recipientAccountChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_recipient_account_message)
            s.currencyChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_currency_message)
            s.categoryChoice.value.isBlank() -> resourceProvider.getString(R.string.invalid_category_message)
            DateFormatterUtil.tryParse(s.timestamp) == null -> resourceProvider.getString(R.string.invalid_timestamp_message)
            else -> null
        }

        if (error != null) {
            _state.update { it.copy(errorMessage = error, showErrorDialog = true) }
            return
        }

        // Prepare the transaction object for submission or update
        val updatedTransaction = Transaction(
            id = _itemId,
            type = ETransactionType.valueOf(s.typeChoice.value),
            title = s.title,
            amount = s.amount.toDoubleOrNull() ?: 0.0,
            amountConverted = 0.0,
            senderAccount = s.senderAccountChoice.value.toLongOrNull() ?: 0L,
            recipientAccount = s.recipientAccountChoice.value.toLongOrNull(),
            currency = s.currencyChoice.value,
            category = s.categoryChoice.value.toLongOrNull() ?: 0L,
            timestamp = DateFormatterUtil.tryParse(s.timestamp) ?: Date()
        )

        // Navigate back after submission
        navigateBack()

        // Submit the transaction
        viewModelScope.launch {
            if (_itemId == 0L) {
                transactionRepository.process(updatedTransaction)
            } else {
                transactionRepository.update(updatedTransaction)
            }
        }
    }

    // --- Dialog Control ---
    fun dismissErrorDialog() = _state.update { it.copy(showErrorDialog = false) }
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    // Deletes the transaction if editing an existing one
    fun deleteItem(navigateBack: () -> Unit) {
        navigateBack()
        viewModelScope.launch {
            if (_itemId != 0L) {
                transactionRepository.deleteById(_itemId)
            }
        }
    }
}
