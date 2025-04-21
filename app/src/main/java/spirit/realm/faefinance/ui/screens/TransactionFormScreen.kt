package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.components.AutocompleteDropdown
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.TransactionFormViewModel
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.ui.components.DateField

/**
 * Destination for the Transaction Form screen.
 * Supports both creating and editing transactions.
 */
object TransactionFormDestination : NavigationDestination {
    override val route = "transaction_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

/**
 * Composable function to display the transaction form.
 *
 * @param navigateBack Callback to navigate back after successful form submission.
 * @param setFormSubmit Callback to set form submission logic.
 * @param viewModel ViewModel to handle form state and logic.
 */
@Composable
fun TransactionFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
    viewModel: TransactionFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Collecting the state and available choices from the ViewModel
    val state by viewModel.state.collectAsState()
    val accountChoices by viewModel.accountChoices.collectAsState()
    val categoryChoices by viewModel.categoryChoices.collectAsState()

    // Trigger form submission action when the form is ready
    LaunchedEffect(Unit) {
        setFormSubmit {
            viewModel.validateAndSubmit(navigateBack)
        }
    }

    // Layout for the transaction form using LazyColumn for efficient rendering
    LazyColumn {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Dropdown for selecting transaction type (Income, Expense, Transfer)
                AutocompleteDropdown(
                    label = stringResource(R.string.transaction_type),
                    choices = viewModel.transactionTypeChoices,
                    selected = state.typeChoice,
                    onSelect = viewModel::updateTransactionTypeChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Outlined text field for the title of the transaction
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                )

                // Outlined text field for the amount of the transaction
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = viewModel::updateAmount,
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )

                // Dropdown for selecting the sender account
                AutocompleteDropdown(
                    label = stringResource(R.string.sender_account),
                    choices = accountChoices,
                    selected = state.senderAccountChoice,
                    onSelect = viewModel::updateSenderAccountChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // If the transaction is a transfer, allow selecting the recipient account
                if (state.typeChoice.value == ETransactionType.Transfer.toString()) {
                    AutocompleteDropdown(
                        label = stringResource(R.string.recipient_account),
                        choices = accountChoices,
                        selected = state.recipientAccountChoice,
                        onSelect = viewModel::updateRecipientAccountChoice,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Dropdown for selecting the currency of the transaction
                AutocompleteDropdown(
                    label = stringResource(R.string.currency),
                    choices = viewModel.currencyChoices,
                    selected = state.currencyChoice,
                    onSelect = viewModel::updateCurrencyChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown for selecting the category of the transaction
                AutocompleteDropdown(
                    label = stringResource(R.string.category),
                    choices = categoryChoices,
                    selected = state.categoryChoice,
                    onSelect = viewModel::updateCategoryChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Date picker for selecting the transaction timestamp
                DateField(
                    label = stringResource(R.string.timestamp),
                    dateText = state.timestamp,
                    onDateTextChange = viewModel::updateTimestamp,
                    modifier = Modifier.fillMaxWidth()
                )

                // Delete button shown if the transaction has a delete option
                if (state.isDeleteVisible) {
                    Button(
                        onClick = viewModel::triggerDeleteDialog,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        }
    }

    // Error dialog for invalid input
    if (state.showErrorDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissErrorDialog,
            title = { Text(stringResource(R.string.invalid_input)) },
            text = { Text(state.errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = viewModel::dismissErrorDialog) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    // Delete confirmation dialog
    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title = { Text(stringResource(R.string.confirm_deletion)) },
            text = { Text(stringResource(R.string.are_you_sure_delete)) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteItem(navigateBack) }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteDialog) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
