package spirit.realm.faefinance.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.BudgetFormViewModel

object BudgetFormDestination : NavigationDestination {
    override val route = "budget_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

@Composable
fun BudgetFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
    viewModel: BudgetFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

}
