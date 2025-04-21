package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.components.AutocompleteDropdown
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.ChartsViewModel
import spirit.realm.faefinance.R
import kotlin.math.roundToInt

/**
 * Navigation destination for the Charts screen.
 */
object ChartsDestination : NavigationDestination {
    override val route = "charts"
}

/**
 * Screen displaying transaction charts with various filters and options.
 *
 * @param viewModel ViewModel for managing the charts' state and business logic.
 */
@Composable
fun ChartsScreen(
    viewModel: ChartsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Observe state changes from the ViewModel
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Filter and interval selection card
                Card {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        // Interval selection dropdown
                        AutocompleteDropdown(
                            label = stringResource(R.string.interval),
                            choices = viewModel.intervalChoices,
                            selected = state.intervalChoice,
                            onSelect = viewModel::updateIntervalChoice,
                            modifier = Modifier.weight(1f)
                        )

                        // Interval length input field
                        OutlinedTextField(
                            value = state.intervalLength,
                            onValueChange = viewModel::updateIntervalLength,
                            label = { Text(stringResource(R.string.interval_length)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                        )
                    }
                }

                // Type selection card (Expense / Income / All)
                Card {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        // List of possible transaction types (Expense, Income, or All)
                        val types = listOf(
                            "" to stringResource(R.string.all),
                            "Expense" to stringResource(R.string.expense_title),
                            "Income" to stringResource(R.string.income_title)
                        )

                        // Display button or text for each type
                        types.forEach { (value, label) ->
                            val isSelected = state.type == value
                            if (isSelected) {
                                FilledTonalButton(
                                    onClick = { viewModel.updateType(value) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label)
                                }
                            } else {
                                TextButton(
                                    onClick = { viewModel.updateType(value) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }
                }

                // Display grouped categories with their transaction counts and percentage
                state.groupedByCategory.map { (title, count) ->
                    val percentage = (100.0 * count / state.totalTransactionCount).roundToInt()
                    Card {
                        Column {
                            // Title Row for each category
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(6.dp)
                            ) {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }

                            // Count and percentage Row for each category
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp)
                            ) {
                                Text("$count ${stringResource(R.string.transactions)}")
                                Text("$percentage%")
                            }
                        }
                    }
                }
            }
        }
    }
}
