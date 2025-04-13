package spirit.realm.faefinance.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainScreen(val route: String, val icon: ImageVector) {
    object Transactions : MainScreen("transactions", Icons.Default.Payments)
    object Budgets : MainScreen("budgets", Icons.Default.Savings)
    object Periodic : MainScreen("periodic", Icons.Default.EventRepeat)
    object Charts : MainScreen("charts", Icons.Default.BarChart)
}

sealed class Screen(val route: String) {
    object Transactions : Screen(MainScreen.Transactions.route)
    object Budgets : Screen(MainScreen.Budgets.route)
    object Periodic : Screen(MainScreen.Periodic.route)
    object Charts : Screen(MainScreen.Charts.route)

    object TransactionForm : Screen("transaction_form")
    object BudgetForm : Screen("budget_form")
    object PeriodicForm : Screen("periodic_form")
}
