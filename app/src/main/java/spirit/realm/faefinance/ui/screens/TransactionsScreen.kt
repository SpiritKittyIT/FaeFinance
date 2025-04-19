package spirit.realm.faefinance.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.TransactionsViewModel

object TransactionsDestination : NavigationDestination {
    override val route = "transactions"
}

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Text(
        "TransactionsScreen"
    )
}
