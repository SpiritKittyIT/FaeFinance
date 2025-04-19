package spirit.realm.faefinance.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import spirit.realm.faefinance.ui.navigation.NavigationDestination

object BudgetsDestination : NavigationDestination {
    override val route = "budgets"
}

@Composable
fun BudgetsScreen() {
    Text(
        "BudgetsScreen"
    )
}
