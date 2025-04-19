package spirit.realm.faefinance.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.TransactionsViewModel
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

object TransactionsDestination : NavigationDestination {
    override val route = "transactions"
}

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LazyColumn {
        itemsIndexed(state.groupedTransactions) { index, group ->
            // Display the date
            val monthName = Month.of(group.groupDate.month).getDisplayName(TextStyle.FULL, Locale.getDefault())
            val year = group.groupDate.year

            Text("$monthName $year")

            Card {
                group.accounts.forEach { transaction ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(transaction.senderAccount.color)
                        ) {
                            Text(transaction.category.symbol)
                        }
                        Text(
                            transaction.transaction.title,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            transaction.transaction.amountConverted.toString(),
                            color = if (transaction.transaction.amountConverted < 0)
                                MaterialTheme.colorScheme.onErrorContainer
                                else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
