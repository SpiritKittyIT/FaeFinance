package spirit.realm.faefinance.ui.screens

import androidx.compose.runtime.Composable
import spirit.realm.faefinance.ui.navigation.NavigationDestination

object BudgetFormDestination : NavigationDestination {
    override val route = "budget_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

@Composable
fun BudgetFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
) {

}
