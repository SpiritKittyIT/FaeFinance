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

/**
 * Data class representing a bottom app bar item.
 *
 * @param route The route for the navigation destination.
 * @param icon The icon to display for this item.
 * @param label The label to display for this item.
 */
class BottomAppBarItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
/**
 * Composable function that displays a bottom app bar with navigation items.
 *
 * @param currentRoute The current active route to determine which item is selected.
 * @param navController The navigation controller to manage navigation actions.
 */
fun BottomAppBar(
    currentRoute: String?,
    navController: NavController
) {
    // Define the list of items in the bottom navigation bar.
    val items = listOf(
        BottomAppBarItem(TransactionsDestination.route, Icons.Default.Payments, stringResource(R.string.nav_transactions)),
        BottomAppBarItem(BudgetsDestination.route, Icons.Default.Savings, stringResource(R.string.nav_budgets)),
        BottomAppBarItem(PeriodicDestination.route, Icons.Default.EventRepeat, stringResource(R.string.nav_periodic)),
        BottomAppBarItem(ChartsDestination.route, Icons.Default.BarChart, stringResource(R.string.nav_charts)),
    )

    // Create a navigation bar with items
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },  // Display the icon for the item
                label = { Text(item.label) },  // Display the label for the item
                selected = currentRoute == item.route,  // Check if the item is selected based on the current route
                onClick = {
                    // Navigate to the selected item route and set up navigation options
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {  // Ensure we pop back to the start
                            saveState = true
                        }
                        launchSingleTop = true  // Ensure only one instance of the screen is at the top
                        restoreState = true  // Restore the state of the screen when navigating back
                    }
                }
            )
        }
    }
}
