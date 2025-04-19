package spirit.realm.faefinance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.screens.BudgetsDestination
import spirit.realm.faefinance.ui.screens.ChartsDestination
import spirit.realm.faefinance.ui.screens.PeriodicDestination
import spirit.realm.faefinance.ui.screens.TransactionsDestination

class BottomAppBarItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomAppBar(
    currentRoute: String?,
    navController: NavController
) {
    val items = listOf(
        BottomAppBarItem(TransactionsDestination.route, Icons.Default.Payments, stringResource(R.string.nav_transactions)),
        BottomAppBarItem(BudgetsDestination.route, Icons.Default.Savings, stringResource(R.string.nav_budgets)),
        BottomAppBarItem(PeriodicDestination.route, Icons.Default.EventRepeat, stringResource(R.string.nav_periodic)),
        BottomAppBarItem(ChartsDestination.route, Icons.Default.BarChart, stringResource(R.string.nav_charts)),
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = {
                    Text(item.label)
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
