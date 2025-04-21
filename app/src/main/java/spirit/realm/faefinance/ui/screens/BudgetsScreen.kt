package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

object BudgetsDestination : NavigationDestination {
    override val route = "budgets"
}

@Composable
fun BudgetsScreen(
    navigateToBudgetForm: (Long) -> Unit,
    navigateToBudgetDetail: (Long) -> Unit,
    viewModel: BudgetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        itemsIndexed(state.budgets) { index, expanded ->
            Card {
                Column {
                    Column (
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(6.dp)
                        ) {
                            Text(
                                "${expanded.budget.title} ${Currency.getInstance(expanded.budget.currency).symbol}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.CalendarMonth,
                                stringResource(R.string.edit),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .clickable( onClick = {
                                        navigateToBudgetDetail(expanded.budget.id)
                                    } )
                            )
                            Icon(
                                Icons.Default.Edit,
                                stringResource(R.string.edit),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .clickable( onClick = {
                                        navigateToBudgetForm(expanded.budget.id)
                                    } )
                            )
                        }
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(6.dp)
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
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(6.dp)
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
