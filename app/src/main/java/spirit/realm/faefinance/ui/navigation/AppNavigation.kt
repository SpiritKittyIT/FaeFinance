package spirit.realm.faefinance.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.ui.screens.BudgetFormScreen
import spirit.realm.faefinance.ui.screens.BudgetsScreen
import spirit.realm.faefinance.ui.screens.ChartsScreen
import spirit.realm.faefinance.ui.screens.PeriodicFormScreen
import spirit.realm.faefinance.ui.screens.PeriodicScreen
import spirit.realm.faefinance.ui.screens.TransactionFormScreen
import spirit.realm.faefinance.ui.screens.TransactionsScreen
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.first
import spirit.realm.faefinance.DatabaseApplication
import spirit.realm.faefinance.ui.components.BottomAppBar
import spirit.realm.faefinance.ui.components.DrawerContent
import spirit.realm.faefinance.ui.components.TopAppBar
import java.util.Currency

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val context = LocalContext.current
    val settings = SettingsDataStore(context)

    val app = context.applicationContext as DatabaseApplication
    val container = app.container

    // State to hold the account info
    var accountTitle: String? by remember { mutableStateOf(null) }
    var accountBalance: String by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Use LaunchedEffect to launch a coroutine to get the account title
    LaunchedEffect(settings.activeAccountId) {
        val activeAccountId = settings.activeAccountId.first()
        if (activeAccountId == 0L) {
            accountTitle = null
            accountBalance = ""
        }
        else {
            val account = container.accountRepository.getById(activeAccountId).first()
            accountTitle = account.title
            val symbol = Currency.getInstance(account.currency).symbol
            accountBalance = "${"%.2f".format(account.balance)} $symbol"
        }
    }


    val showBottomBar = currentRoute in listOf(
        Screen.Transactions.route,
        Screen.Budgets.route,
        Screen.Periodic.route,
        Screen.Charts.route
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent()
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    showBottomBar = showBottomBar,
                    currentRoute = currentRoute,
                    accountTitle = accountTitle,
                    accountBalance = accountBalance,
                    navController = navController,
                    drawerState = drawerState,
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    BottomAppBar(
                        currentRoute = currentRoute,
                        navController = navController
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MainScreen.Transactions.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Screen.Transactions.route) { TransactionsScreen() }
                composable(Screen.Budgets.route) { BudgetsScreen() }
                composable(Screen.Periodic.route) { PeriodicScreen() }
                composable(Screen.Charts.route) { ChartsScreen() }

                // Form screens
                composable(Screen.TransactionForm.route) { TransactionFormScreen() }
                composable(Screen.BudgetForm.route) { BudgetFormScreen() }
                composable(Screen.PeriodicForm.route) { PeriodicFormScreen() }
            }
        }
    }
}
