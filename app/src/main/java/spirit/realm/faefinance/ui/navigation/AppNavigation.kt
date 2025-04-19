package spirit.realm.faefinance.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import spirit.realm.faefinance.ui.screens.BudgetFormScreen
import spirit.realm.faefinance.ui.screens.BudgetsScreen
import spirit.realm.faefinance.ui.screens.ChartsScreen
import spirit.realm.faefinance.ui.screens.PeriodicFormScreen
import spirit.realm.faefinance.ui.screens.PeriodicScreen
import spirit.realm.faefinance.ui.screens.TransactionFormScreen
import spirit.realm.faefinance.ui.screens.TransactionsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.components.BottomAppBar
import spirit.realm.faefinance.ui.components.DrawerContent
import spirit.realm.faefinance.ui.components.MyTopAppBar
import spirit.realm.faefinance.ui.screens.AccountFormDestination
import spirit.realm.faefinance.ui.screens.AccountFormScreen
import spirit.realm.faefinance.ui.screens.BudgetFormDestination
import spirit.realm.faefinance.ui.screens.BudgetsDestination
import spirit.realm.faefinance.ui.screens.CategoriesDestination
import spirit.realm.faefinance.ui.screens.CategoriesScreen
import spirit.realm.faefinance.ui.screens.CategoryFormDestination
import spirit.realm.faefinance.ui.screens.CategoryFormScreen
import spirit.realm.faefinance.ui.screens.ChartsDestination
import spirit.realm.faefinance.ui.screens.PeriodicDestination
import spirit.realm.faefinance.ui.screens.PeriodicFormDestination
import spirit.realm.faefinance.ui.screens.TransactionFormDestination
import spirit.realm.faefinance.ui.screens.TransactionsDestination
import spirit.realm.faefinance.ui.viewmodels.AppNavigationViewModel
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider

@Composable
fun AppNavigation(
    viewModel: AppNavigationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val state by viewModel.state.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val showBottomAppBar = currentRoute in listOf(
        TransactionsDestination.route,
        BudgetsDestination.route,
        PeriodicDestination.route,
        ChartsDestination.route
    )

    val isForm = currentRoute in listOf(
        AccountFormDestination.routeWithArgs,
        BudgetFormDestination.routeWithArgs,
        CategoryFormDestination.routeWithArgs,
        PeriodicFormDestination.routeWithArgs,
        TransactionFormDestination.routeWithArgs
    )

    val showFAB = currentRoute in listOf(
        TransactionsDestination.route,
        BudgetsDestination.route,
        PeriodicDestination.route,
        CategoriesDestination.route
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navigateToAccountForm = {
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate("${AccountFormDestination.route}/$it")
                },
                navigateToCategories = {
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate(CategoriesDestination.route)
                },
                navigationViewModel = viewModel
            )
        }
    ) {
        Scaffold(
            topBar = {
                MyTopAppBar(
                    isMainScreen = showBottomAppBar,
                    isForm = isForm,
                    currentRoute = currentRoute,
                    accountTitle = state.accountTitle,
                    accountBalance = state.accountBalance,
                    navController = navController,
                    drawerState = drawerState,
                    submitFunction = state.formSubmit
                )
            },
            floatingActionButton = {
                if (showFAB) {
                    FloatingActionButton(
                        onClick = {
                            when (currentRoute) {
                                TransactionsDestination.route -> navController.navigate("${TransactionFormDestination.route}/0")
                                BudgetsDestination.route -> navController.navigate("${BudgetFormDestination.route}/0")
                                PeriodicDestination.route -> navController.navigate("${PeriodicFormDestination.route}/0")
                                CategoriesDestination.route -> navController.navigate("${CategoryFormDestination.route}/0")
                                else -> {}
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, stringResource(R.string.add))
                    }
                }
            },
            bottomBar = {
                if (showBottomAppBar) {
                    BottomAppBar(
                        currentRoute = currentRoute,
                        navController = navController
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = TransactionsDestination.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(TransactionsDestination.route) { TransactionsScreen() }
                composable(BudgetsDestination.route) { BudgetsScreen() }
                composable(PeriodicDestination.route) { PeriodicScreen() }
                composable(ChartsDestination.route) { ChartsScreen() }
                composable(CategoriesDestination.route) { CategoriesScreen(
                    navigateToCategoryForm = {
                        navController.navigate("${CategoryFormDestination.route}/$it")
                    }
                ) }

                // Form screens
                composable(
                    AccountFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(AccountFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    AccountFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction)
                }
                composable(
                    TransactionFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(TransactionFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    TransactionFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction)
                }
                composable(
                    BudgetFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(BudgetFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    BudgetFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction)
                }
                composable(
                    PeriodicFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(PeriodicFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    PeriodicFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction)
                }
                composable(
                    CategoryFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(CategoryFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    CategoryFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction)
                }
            }
        }
    }
}
