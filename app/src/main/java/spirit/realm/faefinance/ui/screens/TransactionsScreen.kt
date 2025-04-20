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

object TransactionsDestination : NavigationDestination {
    override val route = "transactions"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(
    navigateToTransactionForm: (Long) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        itemsIndexed(state.groupedTransactions) { index, group ->
            // Display the date
            val monthName = Month.of(group.groupDate.month).getDisplayName(TextStyle.FULL, Locale.getDefault())
            val year = group.groupDate.year

            Text("$monthName $year")

            Card {
                group.accounts.forEachIndexed { index, transaction ->
                    val amountText = String.format(Locale.getDefault() ,"%.2f", transaction.transaction.amountConverted)
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
                                onClick = { },
                                onLongClick = {
                                    navigateToTransactionForm(transaction.transaction.id)
                                }
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(transaction.senderAccount.color)
                                .padding(4.dp)
                        ) {
                            Text(transaction.category.symbol)
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Row (
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    transaction.transaction.title,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    if (transaction.transaction.type == ETransactionType.Expense)
                                    "-${amountText}"
                                    else amountText,
                                    color = if (transaction.transaction.type == ETransactionType.Expense)
                                        MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
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
            if (index == state.groupedTransactions.size - 1) {
                Box (
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )
            }
        }
    }
}
