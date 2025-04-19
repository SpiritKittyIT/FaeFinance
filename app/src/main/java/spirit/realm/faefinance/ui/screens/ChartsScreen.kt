package spirit.realm.faefinance.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import spirit.realm.faefinance.ui.navigation.NavigationDestination

object ChartsDestination : NavigationDestination {
    override val route = "charts"
}

@Composable
fun ChartsScreen() {
    Text(
        "ChartsScreen"
    )
}
