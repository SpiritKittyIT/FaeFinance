package spirit.realm.faefinance.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.BudgetsViewModel

object BudgetsDestination : NavigationDestination {
    override val route = "budgets"
}

@Composable
fun BudgetsScreen(
    viewModel: BudgetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Text(
        "BudgetsScreen"
    )
}
