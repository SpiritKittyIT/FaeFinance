package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.components.ProgressBar
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.utility.DateFormatterUtil
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.BudgetsViewModel
import java.util.Currency

/**
 * Defines the navigation destination for the budgets list screen.
 */
object BudgetsDestination : NavigationDestination {
    override val route = "budgets"
}

/**
 * Displays a list of all user budgets in a scrollable column.
 * Each item includes the title, currency, category icons, dates, and a progress bar.
 * Includes edit and detail navigation actions per budget item.
 *
 * @param navigateToBudgetForm Callback to navigate to the budget form (edit/create).
 * @param navigateToBudgetDetail Callback to navigate to the budget detail screen.
 * @param viewModel Backing ViewModel for state management.
 */
@Composable
fun BudgetsScreen(
    navigateToBudgetForm: (Long) -> Unit,
    navigateToBudgetDetail: (Long) -> Unit,
    viewModel: BudgetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        itemsIndexed(state.budgets) { _, expanded ->
            Card {
                Column {
                    // Header Section: Title, currency, and action icons
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

                            // Navigate to budget detail
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = stringResource(R.string.edit),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.clickable {
                                    navigateToBudgetDetail(expanded.budget.id)
                                }
                            )

                            // Navigate to budget form for editing
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.clickable {
                                    navigateToBudgetForm(expanded.budget.id)
                                }
                            )
                        }

                        // Categories associated with the budget
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            expanded.categories.forEach { category ->
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

                    // Footer Section: Start/end date and progress bar
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
