package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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

object AccountFormDestination : NavigationDestination {
    override val route = "account_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

@Composable
fun AccountFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
    viewModel: AccountFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSubmitSuccessful) {
        if (state.isSubmitSuccessful) {
            navigateBack()
        }
    }

    LaunchedEffect(Unit) {
        setFormSubmit {
            viewModel.validateAndSubmit()
        }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::updateTitle,
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
            ),
            singleLine = true,
        )
        AutocompleteDropdown(
            label = stringResource(R.string.currency),
            choices = viewModel.currencyChoices,
            selected = state.currencyChoice,
            onSelect = viewModel::updateCurrencyChoice,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.balance,
            onValueChange = viewModel::updateBalance,
            label = { Text(stringResource(R.string.balance)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
        )
        ColorHexField(
            label = stringResource(R.string.color),
            color = state.color,
            onColorChange = viewModel::updateColor,
            modifier = Modifier.fillMaxWidth()
        )
        if (state.isDeleteVisible) {
            Button(
                onClick = viewModel::triggerDeleteDialog,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    }

    if (state.showErrorDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissErrorDialog,
            title = { Text(stringResource(R.string.invalid_input)) },
            text = { Text(state.errorMessage ?: "") },
            confirmButton = {
                TextButton(
                    onClick = viewModel::dismissErrorDialog
                ) {
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
                TextButton(
                    onClick = viewModel::deleteAccount
                ) {
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
