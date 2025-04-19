package spirit.realm.faefinance.ui.screens

import androidx.compose.runtime.Composable
import spirit.realm.faefinance.ui.navigation.NavigationDestination

object PeriodicFormDestination : NavigationDestination {
    override val route = "periodic_form"
    const val ID_ARG = "id"
    val routeWithArgs = "$route/{$ID_ARG}"
}

@Composable
fun PeriodicFormScreen(
    navigateBack: () -> Unit,
    setFormSubmit: (() -> Unit) -> Unit,
) {

}
