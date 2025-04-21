package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.components.AutocompleteDropdown
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.components.ColorHexField
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AccountFormViewModel
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider

/**
 * Navigation destination for the account form screen.
 * This screen allows creating or editing an account.
 */
object AccountFormDestination : NavigationDestination {
    override val route = "account_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

/**
 * Composable screen for creating or editing an account.
 *
 * @param navigateBack Function to call to navigate to the previous screen.
 * @param setFormSubmit Function that allows parent navigation controller to hook into form submission.
 * @param viewModel ViewModel providing form state and business logic.
 */
@Composable
fun AccountFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
    viewModel: AccountFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    // Register form submit callback when screen is shown
    LaunchedEffect(Unit) {
        setFormSubmit {
            viewModel.validateAndSubmit(navigateBack)
        }
    }

    // Main form layout using LazyColumn for potential scrolling
    LazyColumn {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Account title field
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    singleLine = true
                )

                // Currency dropdown field
                AutocompleteDropdown(
                    label = stringResource(R.string.currency),
                    choices = viewModel.currencyChoices,
                    selected = state.currencyChoice,
                    onSelect = viewModel::updateCurrencyChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Balance input field
                OutlinedTextField(
                    value = state.balance,
                    onValueChange = viewModel::updateBalance,
                    label = { Text(stringResource(R.string.balance)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Color picker field
                ColorHexField(
                    label = stringResource(R.string.color),
                    color = state.color,
                    onColorChange = viewModel::updateColor,
                    modifier = Modifier.fillMaxWidth()
                )

                // Delete button (conditionally visible)
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

    // Displays an error dialog if input is invalid
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
