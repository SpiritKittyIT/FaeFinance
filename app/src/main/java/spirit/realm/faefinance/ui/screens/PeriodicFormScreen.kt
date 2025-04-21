package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.ui.components.AutocompleteDropdown
import spirit.realm.faefinance.ui.components.DateField
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.PeriodicFormViewModel

/**
 * Navigation destination for the PeriodicForm screen.
 */
object PeriodicFormDestination : NavigationDestination {
    override val route = "periodic_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

/**
 * Screen displaying a form for creating/editing periodic transactions.
 *
 * @param navigateBack Callback function for navigating back to the previous screen.
 * @param setFormSubmit Callback function for setting the form submission behavior.
 * @param viewModel ViewModel for managing the periodic form's state and business logic.
 */
@Composable
fun PeriodicFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
    viewModel: PeriodicFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Collect the form state and choices from the ViewModel
    val state by viewModel.state.collectAsState()
    val accountChoices by viewModel.accountChoices.collectAsState()
    val categoryChoices by viewModel.categoryChoices.collectAsState()

    // Set the form submission action when the screen is first composed
    LaunchedEffect(Unit) {
        setFormSubmit {
            viewModel.validateAndSubmit(navigateBack)
        }
    }

    // Layout for the periodic form
    LazyColumn {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Transaction type dropdown
                AutocompleteDropdown(
                    label = stringResource(R.string.transaction_type),
                    choices = viewModel.transactionTypeChoices,
                    selected = state.typeChoice,
                    onSelect = viewModel::updateTypeChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Title input field
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true
                )

                // Amount input field
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = viewModel::updateAmount,
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                // Sender account dropdown
                AutocompleteDropdown(
                    label = stringResource(R.string.sender_account),
                    choices = accountChoices,
                    selected = state.senderAccountChoice,
                    onSelect = viewModel::updateSenderAccount,
                    modifier = Modifier.fillMaxWidth()
                )

                // If the transaction type is Transfer, show recipient account dropdown
                if (state.typeChoice.value == ETransactionType.Transfer.toString()) {
                    AutocompleteDropdown(
                        label = stringResource(R.string.recipient_account),
                        choices = accountChoices,
                        selected = state.recipientAccountChoice,
                        onSelect = viewModel::updateRecipientAccount,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Currency dropdown
                AutocompleteDropdown(
                    label = stringResource(R.string.currency),
                    choices = viewModel.currencyChoices,
                    selected = state.currencyChoice,
                    onSelect = viewModel::updateCurrencyChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category dropdown
                AutocompleteDropdown(
                    label = stringResource(R.string.category),
                    choices = categoryChoices,
                    selected = state.categoryChoice,
                    onSelect = viewModel::updateCategoryChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Date picker for next transaction
                DateField(
                    label = stringResource(R.string.next_transaction),
                    dateText = state.nextTransaction,
                    onDateTextChange = viewModel::updateNextTransaction,
                    modifier = Modifier.fillMaxWidth()
                )

                // Interval dropdown
                AutocompleteDropdown(
                    label = stringResource(R.string.interval),
                    choices = viewModel.intervalChoices,
                    selected = state.intervalChoice,
                    onSelect = viewModel::updateIntervalChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Interval length input field
                OutlinedTextField(
                    value = state.intervalLength,
                    onValueChange = viewModel::updateIntervalLength,
                    label = { Text(stringResource(R.string.interval_length)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Delete button visibility
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

    // Error Dialog - Displays error message if input is invalid
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

    // Delete Confirmation Dialog
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
