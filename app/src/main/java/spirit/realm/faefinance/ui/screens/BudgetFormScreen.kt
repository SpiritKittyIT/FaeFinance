package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

/**
 * Defines the navigation route for the budget form screen.
 * Used for both creating and editing budgets.
 */
object BudgetFormDestination : NavigationDestination {
    override val route = "budget_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

/**
 * Budget form screen that allows users to create or update budget details.
 * Includes inputs for title, amount, categories, date, interval settings, etc.
 *
 * @param navigateBack Callback to navigate back on form submission or deletion.
 * @param setFormSubmit Used to hook form validation and submission to the parent screen.
 * @param viewModel Backing ViewModel handling state and logic for the form.
 */
@Composable
fun BudgetFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
    viewModel: BudgetFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()
    val categoryList by viewModel.categoryList.collectAsState()

    // Registers form submission logic with the parent (e.g., FAB or top bar)
    LaunchedEffect(Unit) {
        setFormSubmit {
            viewModel.validateAndSubmit(navigateBack)
        }
    }

    // Form content layout
    LazyColumn {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Title input
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Amount input
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = viewModel::updateAmount,
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                // Category selection
                Button(
                    onClick = viewModel::showCategoryDialog,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.select_categories))
                }

                // Start date input
                DateField(
                    label = stringResource(R.string.start_date),
                    dateText = state.startDate,
                    onDateTextChange = viewModel::updateStartDate,
                    modifier = Modifier.fillMaxWidth()
                )

                // Currency selector
                AutocompleteDropdown(
                    label = stringResource(R.string.currency),
                    choices = viewModel.currencyChoices,
                    selected = state.currencyChoice,
                    onSelect = viewModel::updateCurrencyChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Interval selector
                AutocompleteDropdown(
                    label = stringResource(R.string.interval),
                    choices = viewModel.intervalChoices,
                    selected = state.intervalChoice,
                    onSelect = viewModel::updateIntervalChoice,
                    modifier = Modifier.fillMaxWidth()
                )

                // Interval length input
                OutlinedTextField(
                    value = state.intervalLength,
                    onValueChange = viewModel::updateIntervalLength,
                    label = { Text(stringResource(R.string.interval_length)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )

                // Optional delete button (visible only when editing existing item)
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

    // Category selection dialog with checkboxes
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

    // Error dialog for invalid inputs or failed validation
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
