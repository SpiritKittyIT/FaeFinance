package spirit.realm.faefinance.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.PeriodicTransactionsViewModel

object PeriodicDestination : NavigationDestination {
    override val route = "periodic"
}

@Composable
fun PeriodicScreen(
    viewModel: PeriodicTransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Text(
        "PeriodicScreen"
    )
}
