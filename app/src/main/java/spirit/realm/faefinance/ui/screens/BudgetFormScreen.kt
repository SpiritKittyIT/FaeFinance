package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.components.AutocompleteDropdown
import spirit.realm.faefinance.ui.components.CheckboxWithLabel
import spirit.realm.faefinance.ui.components.DateField
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.BudgetFormViewModel

object BudgetFormDestination : NavigationDestination {
    override val route = "budget_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

@Composable
fun BudgetFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
    viewModel: BudgetFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()
    val categoryList by viewModel.categoryList.collectAsState()

    LaunchedEffect(Unit) {
        setFormSubmit {
            viewModel.validateAndSubmit(navigateBack)
        }
    }

    LazyColumn {
        item {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.amount,
                    onValueChange = viewModel::updateAmount,
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Button(
                    onClick = viewModel::showCategoryDialog,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.select_categories))
                }

                DateField(
                    label = stringResource(R.string.start_date),
                    dateText = state.startDate,
                    onDateTextChange = viewModel::updateStartDate,
                    modifier = Modifier.fillMaxWidth()
                )

                AutocompleteDropdown(
                    label = stringResource(R.string.currency),
                    choices = viewModel.currencyChoices,
                    selected = state.currencyChoice,
                    onSelect = viewModel::updateCurrencyChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                AutocompleteDropdown(
                    label = stringResource(R.string.interval),
                    choices = viewModel.intervalChoices,
                    selected = state.intervalChoice,
                    onSelect = viewModel::updateIntervalChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.intervalLength,
                    onValueChange = viewModel::updateIntervalLength,
                    label = { Text(stringResource(R.string.interval_length)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
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
        }
    }

    // Category selection dialog
    if (state.showCategoryDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissCategoryDialog,
            title = { Text(stringResource(R.string.screen_categories)) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    itemsIndexed(categoryList) { index, category ->
                        val selected = state.categories.any { it.id == category.id }

                        if (index != 0) {
                            HorizontalDivider()
                        }
                        CheckboxWithLabel(
                            checked = selected,
                            label = category.title,
                            onCheckedChange = { checked ->
                                val updatedList = if (checked) {
                                    state.categories + category
                                } else {
                                    state.categories.filterNot { it.id == category.id }
                                }
                                viewModel.updateCategoryChoices(updatedList)
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissCategoryDialog) {
                    Text(stringResource(R.string.confirm))
                }
            }
        )
    }


    // Error Dialog
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
