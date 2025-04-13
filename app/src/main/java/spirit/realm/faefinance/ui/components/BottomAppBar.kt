package spirit.realm.faefinance.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.navigation.MainScreen

@Composable
fun BottomAppBar(
    currentRoute: String?,
    navController: NavController
) {
    val mainScreens = listOf(
        MainScreen.Transactions,
        MainScreen.Budgets,
        MainScreen.Periodic,
        MainScreen.Charts
    )

    NavigationBar {
        mainScreens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = {
                    Text(
                        text = when (screen) {
                            MainScreen.Transactions -> stringResource(R.string.nav_transactions)
                            MainScreen.Budgets -> stringResource(R.string.nav_budgets)
                            MainScreen.Periodic -> stringResource(R.string.nav_periodic)
                            MainScreen.Charts -> stringResource(R.string.nav_charts)
                        }
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
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
