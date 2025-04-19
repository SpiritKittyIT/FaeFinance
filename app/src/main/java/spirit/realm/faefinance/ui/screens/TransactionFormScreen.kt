package spirit.realm.faefinance.ui.screens

import androidx.compose.runtime.Composable
import spirit.realm.faefinance.ui.navigation.NavigationDestination

object TransactionFormDestination : NavigationDestination {
    override val route = "transactions_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

@Composable
fun TransactionFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
) {

}
