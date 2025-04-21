package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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

object ChartsDestination : NavigationDestination {
    override val route = "charts"
}

@Composable
fun ChartsScreen(
    viewModel: ChartsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LazyColumn {
        item {
            Column (
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Card {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        AutocompleteDropdown(
                            label = stringResource(R.string.interval),
                            choices = viewModel.intervalChoices,
                            selected = state.intervalChoice,
                            onSelect = viewModel::updateIntervalChoice,
                            modifier = Modifier.weight(1f)
                        )
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
                Card {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        val types = listOf(
                            "" to stringResource(R.string.all),
                            "Expense" to stringResource(R.string.expense_title),
                            "Income" to stringResource(R.string.income_title)
                        )

                        types.forEach { (value, label) ->
                            val isSelected = state.type == value
                            if (isSelected) {
                                androidx.compose.material3.FilledTonalButton(
                                    onClick = { viewModel.updateType(value) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label)
                                }
                            } else {
                                androidx.compose.material3.TextButton(
                                    onClick = { viewModel.updateType(value) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    }
                }
                state.groupedByCategory.map { (title, count) ->
                    val percentage = (100.0 * count / state.totalTransactionCount).roundToInt()
                    Card {
                        Column {
                            Row (
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
