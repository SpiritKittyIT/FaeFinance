package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.components.ProgressBar
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.utility.DateFormatterUtil
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.BudgetDetailViewModel
import java.util.Currency

/**
 * Navigation destination for the budget detail screen.
 * This shows details of one or more budget entries, including title, categories, and progress.
 */
object BudgetDetailDestination : NavigationDestination {
    override val route = "budget_detail"
    const val ID_ARG = "id"
    val routeWithArgs = "${route}/{$ID_ARG}"
}

/**
 * Composable screen that displays a list of budgets with their categories, amounts, and dates.
 *
 * @param viewModel ViewModel for accessing budget detail state.
 */
@Composable
fun BudgetDetailScreen(
    viewModel: BudgetDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    // LazyColumn to display all budget entries
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        itemsIndexed(state.budgets) { index, expanded ->
            Card {
                Column {
                    // Header section with title and currency
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                "${expanded.budget.title} ${Currency.getInstance(expanded.budget.currency).symbol}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Category tags (displayed as symbol chips)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            expanded.categories.map { category ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        category.symbol,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Budget progress row with start/end dates and progress bar
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(6.dp)
                    ) {
                        Text(DateFormatterUtil.format(expanded.budget.startDate))
                        ProgressBar(
                            amountSpent = expanded.budget.amountSpent,
                            amountMax = expanded.budget.amount,
                            modifier = Modifier.weight(1f)
                        )
                        Text(DateFormatterUtil.format(expanded.budget.endDate))
                    }
                }
            }
        }
    }
}
