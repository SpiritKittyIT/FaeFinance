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
import spirit.realm.faefinance.ui.utility.DateFormatterUtil
import spirit.realm.faefinance.ui.utility.IAppResourceProvider
import spirit.realm.faefinance.ui.utility.TransactionIntervalUtil
import spirit.realm.faefinance.ui.utility.TransactionTypeUtil
import java.util.Currency

/**
 * Data class representing the state of the periodic transaction form.
 * It holds the form's state including the selected transaction type, amount, accounts, category,
 * and various other fields related to periodic transactions.
 */
data class PeriodicFormState(
    var typeChoice: Choice = Choice(title = "", value = ""),
    var title: String = "",
    var amount: String = "0",
    val senderAccountChoice: Choice = Choice(title = "", value = ""),
    val recipientAccountChoice: Choice = Choice(title = "", value = ""),
    val currencyChoice: Choice = Choice(title = "", value = ""),
    val categoryChoice: Choice = Choice(title = "", value = ""),
    var nextTransaction: String = DateFormatterUtil.format(Date()),
    var intervalChoice: Choice = Choice(title = "", value = ""),
    var intervalLength: String = "0",

    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleteVisible: Boolean = false
)

/**
 * ViewModel responsible for managing the periodic transaction form's state and handling form submissions.
 * It includes logic to fetch and update data, validate inputs, and submit a new or updated periodic transaction.
 */
class PeriodicFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: IAppResourceProvider,
    private val periodicTransactionRepository: PeriodicTransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // The ID of the periodic transaction being edited (if any)
    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    // Mutable state flow to hold the current state of the periodic transaction form
    private val _state = MutableStateFlow(PeriodicFormState())
    val state: StateFlow<PeriodicFormState> = _state.asStateFlow()

    val transactionTypeChoices = TransactionTypeUtil.getChoices(resourceProvider)
    val intervalChoices = TransactionIntervalUtil.getChoices(resourceProvider)
    val currencyChoices = CurrencyUtil.currencyChoices

    // Mutable state flows for available account and category choices
    private val _accountChoices = MutableStateFlow<List<Choice>>(emptyList())
    val accountChoices: StateFlow<List<Choice>> = _accountChoices.asStateFlow()

    private val _categoryChoices = MutableStateFlow<List<Choice>>(emptyList())
    val categoryChoices: StateFlow<List<Choice>> = _categoryChoices.asStateFlow()

    init {
        // Initialize account choices from the account repository
        viewModelScope.launch {
            AccountUtil.getAccountChoices(accountRepository).collect {
                _accountChoices.value = it
            }
        }

        // Initialize category choices from the category repository
        viewModelScope.launch {
            CategoryUtil.getCategoryChoices(categoryRepository).collect {
                _categoryChoices.value = it
            }
        }

        // If editing an existing periodic transaction, load its details and set the form state
        viewModelScope.launch {
            if (_itemId != 0L) {
                periodicTransactionRepository.getExpandedById(_itemId).first().let { expanded ->
                    _state.value = PeriodicFormState(
                        typeChoice = transactionTypeChoices.first { it.value == expanded.periodicTransaction.type.toString() },
                        title = expanded.periodicTransaction.title,
                        amount = expanded.periodicTransaction.amount.toString(),
                        senderAccountChoice = _accountChoices.value.first { it.value == expanded.senderAccount.id.toString() },
                        recipientAccountChoice = expanded.recipientAccount?.let {
                            Choice(
                                title = it.title,
                                value = it.id.toString(),
                                trailing = Currency.getInstance(it.currency).symbol
                            )
                        } ?: Choice(title = "", value = ""),
                        currencyChoice = currencyChoices.first { it.value == expanded.periodicTransaction.currency },
                        categoryChoice = _categoryChoices.value.first { it.value == expanded.category.id.toString() },
                        nextTransaction = DateFormatterUtil.format(expanded.periodicTransaction.nextTransaction),
                        intervalChoice = intervalChoices.first { it.value == expanded.periodicTransaction.interval.name },
                        intervalLength = expanded.periodicTransaction.intervalLength.toString(),
                        isDeleteVisible = true
                    )
                }
            }
        }
    }

    // --- State Update Methods ---

    /**
     * Updates the transaction type choice in the form state.
     * @param choice The selected transaction type.
     */
    fun updateTypeChoice(choice: Choice) = _state.update { it.copy(typeChoice = choice) }

    /**
     * Updates the title of the periodic transaction in the form state.
     * @param title The title of the periodic transaction.
     */
    fun updateTitle(title: String) = _state.update { it.copy(title = title) }

    /**
     * Updates the amount for the periodic transaction in the form state.
     * @param amount The amount of the transaction.
     */
    fun updateAmount(amount: String) = _state.update { it.copy(amount = amount) }

    /**
     * Updates the sender account choice in the form state.
     * This will also update the currency choice based on the selected account's currency.
     * @param newChoice The selected sender account.
     */
    fun updateSenderAccount(newChoice: Choice) {
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

    /**
     * Updates the recipient account choice in the form state.
     * @param choice The selected recipient account.
     */
    fun updateRecipientAccount(choice: Choice) = _state.update { it.copy(recipientAccountChoice = choice) }

    /**
     * Updates the currency choice in the form state.
     * @param choice The selected currency.
     */
    fun updateCurrencyChoice(choice: Choice) = _state.update { it.copy(currencyChoice = choice) }

    /**
     * Updates the category choice in the form state.
     * @param choice The selected category.
     */
    fun updateCategoryChoice(choice: Choice) = _state.update { it.copy(categoryChoice = choice) }

    /**
     * Updates the next transaction date in the form state.
     * @param dateText The date of the next transaction.
     */
    fun updateNextTransaction(dateText: String) = _state.update { it.copy(nextTransaction = dateText) }

    /**
     * Updates the interval choice in the form state.
     * @param choice The selected interval.
     */
    fun updateIntervalChoice(choice: Choice) = _state.update { it.copy(intervalChoice = choice) }

    /**
     * Updates the interval length in the form state.
     * @param length The length of the interval (in numeric form).
     */
    fun updateIntervalLength(length: String) = _state.update { it.copy(intervalLength = length) }

    // --- Submit Method ---

    /**
     * Validates the form and submits the periodic transaction.
     * If there are any validation errors, they will be shown to the user.
     * @param navigateBack The callback to navigate back after submission.
     */
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
            DateFormatterUtil.tryParse(s.nextTransaction) == null -> resourceProvider.getString(R.string.invalid_next_transaction_message)
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
            recipientAccount = s.recipientAccountChoice.value.toLongOrNull(),
            currency = s.currencyChoice.value,
            category = s.categoryChoice.value.toLong(),
            nextTransaction = DateFormatterUtil.tryParse(s.nextTransaction)!!,
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
            periodicTransactionRepository.processAllUnprocessed()
        }
    }

    // --- Dialog Control Methods ---

    /**
     * Dismisses the error dialog.
     */
    fun dismissErrorDialog() = _state.update { it.copy(showErrorDialog = false) }

    /**
     * Triggers the delete dialog.
     */
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }

    /**
     * Dismisses the delete dialog.
     */
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    /**
     * Deletes the current periodic transaction and navigates back.
     * @param navigateBack The callback to navigate back after deletion.
     */
    fun deleteItem(navigateBack: () -> Unit) {
        navigateBack()
        viewModelScope.launch {
            if (_itemId != 0L) {
                periodicTransactionRepository.deleteById(_itemId)
            }
        }
    }
}
