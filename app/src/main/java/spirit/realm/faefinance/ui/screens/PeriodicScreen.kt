package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.utility.DateFormatterUtil
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.PeriodicTransactionsViewModel
import java.util.Currency
import java.util.Locale

/**
 * Navigation destination for the Periodic screen.
 */
object PeriodicDestination : NavigationDestination {
    override val route = "periodic"
}

/**
 * Screen displaying a list of periodic transactions with the ability to edit them.
 *
 * @param navigateToPeriodicForm Callback function to navigate to the form for editing a periodic transaction.
 * @param viewModel ViewModel for managing the periodic transactions' state and business logic.
 */
@Composable
fun PeriodicScreen(
    navigateToPeriodicForm: (Long) -> Unit,
    viewModel: PeriodicTransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Collect the list of periodic transactions from the ViewModel
    val state by viewModel.state.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        // Iterate through the list of periodic transactions and display each item
        itemsIndexed(state.periodicTransactions) { index, expanded ->
            // Format the amount text for displaying
            val amountText = String.format(Locale.getDefault(), "%.2f", expanded.periodicTransaction.amount)

            // Determine the type of the transaction (Income, Expense, or Transfer)
            val typeText = when (expanded.periodicTransaction.type) {
                ETransactionType.Expense -> stringResource(R.string.expense_title)
                ETransactionType.Income -> stringResource(R.string.income_title)
                ETransactionType.Transfer -> stringResource(R.string.transfer_title)
            }

            // Determine the interval for the transaction (Days, Weeks, or Months)
            val intervalText = when (expanded.periodicTransaction.interval) {
                ETransactionInterval.Days -> stringResource(R.string.days_title)
                ETransactionInterval.Weeks -> stringResource(R.string.weeks_title)
                ETransactionInterval.Months -> stringResource(R.string.months_title)
            }

            // Card for the transaction item
            Card {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(6.dp)
                ) {
                    // Category symbol (e.g., icon representing the category)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(4.dp)
                    ) {
                        Text(
                            expanded.category.symbol,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Title of the periodic transaction
                    Text(
                        expanded.periodicTransaction.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )

                    // Edit icon for navigating to the form to edit the transaction
                    Icon(
                        Icons.Default.Edit,
                        stringResource(R.string.edit),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .clickable(onClick = {
                                navigateToPeriodicForm(expanded.periodicTransaction.id)
                            })
                    )
                }

                // Additional transaction details
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    // Sender account details
                    Text("${stringResource(R.string.sender_account)}: ${expanded.senderAccount.title}")

                    // Recipient account details (only shown for transfers)
                    if (expanded.periodicTransaction.type == ETransactionType.Transfer) {
                        Text("${stringResource(R.string.recipient_account)}: ${expanded.recipientAccount?.title}")
                    }

                    // Amount and transaction type
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "${stringResource(R.string.amount)}: $amountText ${Currency.getInstance(expanded.periodicTransaction.currency).symbol}"
                        )
                        Text(typeText)
                    }

                    // Interval length and unit (e.g., every 2 months)
                    if (expanded.periodicTransaction.intervalLength > 0) {
                        Text(
                            "${stringResource(R.string.interval)}: ${expanded.periodicTransaction.intervalLength} $intervalText"
                        )
                    }

                    // Next transaction date
                    Text(
                        "${stringResource(R.string.next_transaction)}: ${DateFormatterUtil.format(expanded.periodicTransaction.nextTransaction)}"
                    )
                }
            }

            // Spacer at the bottom of the list to avoid UI clipping
            if (index == state.periodicTransactions.size - 1) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )
            }
        }
    }
}
