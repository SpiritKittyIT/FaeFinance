package spirit.realm.faefinance.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import spirit.realm.faefinance.ui.navigation.NavigationDestination

object PeriodicDestination : NavigationDestination {
    override val route = "periodic"
}

@Composable
fun PeriodicScreen() {
    Text(
        "PeriodicScreen"
    )
}
