package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.TransactionsViewModel
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

/**
 * Defines the destination route for the Transactions screen.
 */
object TransactionsDestination : NavigationDestination {
    override val route = "transactions"
}

/**
 * Composable function that displays a list of transactions grouped by month and year.
 *
 * The screen shows a list of transactions, grouped by month and year. For each transaction,
 * relevant details such as title, amount, sender and recipient accounts are displayed.
 * It also handles navigating to the transaction form when an item is long-pressed.
 *
 * @param navigateToTransactionForm A lambda function that navigates to the transaction form screen when a transaction is selected.
 * @param viewModel The view model that provides the necessary data and handles business logic for the transactions screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(
    navigateToTransactionForm: (Long) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Collecting the state from the view model
    val state by viewModel.state.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        itemsIndexed(state.groupedTransactions) { index, group ->
            // Display the grouped date (month and year)
            val monthName = Month.of(group.groupDate.month).getDisplayName(TextStyle.FULL, Locale.getDefault())
            val year = group.groupDate.year

            Text("$monthName $year")

            // Card to display transaction details for each group
            Card {
                group.accounts.forEachIndexed { index, transaction ->
                    val amountText = String.format(Locale.getDefault(), "%.2f", transaction.transaction.amountConverted)
                    // Divider between transactions
                    if (index != 0) {
                        HorizontalDivider()
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current,
                                onClick = { /* handle single click, if necessary */ },
                                onLongClick = {
                                    // Navigate to the transaction form on long click
                                    navigateToTransactionForm(transaction.transaction.id)
                                }
                            )
                    ) {
                        // Display the transaction category symbol with the sender account color
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(transaction.senderAccount.color)
                                .padding(4.dp)
                        ) {
                            Text(transaction.category.symbol)
                        }

                        // Column to display transaction details
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                // Display transaction title and amount
                                Text(
                                    transaction.transaction.title,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                // Display the amount with negative sign for expenses
                                Text(
                                    if (transaction.transaction.type == ETransactionType.Expense)
                                        "-${amountText}"
                                    else amountText,
                                    color = if (transaction.transaction.type == ETransactionType.Expense)
                                        MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Display sender and recipient account information if applicable
                            if (transaction.recipientAccount != null) {
                                Text(
                                    if (transaction.transaction.type == ETransactionType.Expense)
                                        "${transaction.senderAccount.title} -> ${transaction.recipientAccount.title}"
                                    else "${transaction.recipientAccount.title} -> ${transaction.senderAccount.title}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Add space at the bottom if this is the last item in the list
            if (index == state.groupedTransactions.size - 1) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )
            }
        }
    }
}
