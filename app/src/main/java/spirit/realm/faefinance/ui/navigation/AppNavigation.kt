package spirit.realm.faefinance.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.components.BottomAppBar
import spirit.realm.faefinance.ui.components.DrawerContent
import spirit.realm.faefinance.ui.components.MyTopAppBar
import spirit.realm.faefinance.ui.screens.*
import spirit.realm.faefinance.ui.viewmodels.AppNavigationViewModel
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider

/**
 * Main composable responsible for setting up app-wide navigation.
 * It includes drawer, top app bar, bottom navigation bar, FAB, and routing between screens.
 *
 * @param viewModel AppNavigationViewModel responsible for handling navigation state.
 * @param navController NavController used to manage back stack and screen transitions.
 */
@Composable
fun AppNavigation(
    viewModel: AppNavigationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()

    // Tracks the current screen route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // State collected from ViewModel
    val state by viewModel.state.collectAsState()

    // Controls the drawer open/close state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // UI logic to toggle visibility of elements based on current screen
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

    // Trigger one-time startup actions
    LaunchedEffect(Unit) {
        viewModel.onLaunch()
    }

    // Scaffold layout with navigation drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navigateToAccountForm = {
                    scope.launch { drawerState.close() }
                    navController.navigate("${AccountFormDestination.route}/$it")
                },
                navigateToCategories = {
                    scope.launch { drawerState.close() }
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
            // Handles actual navigation between different screens
            NavHost(
                navController = navController,
                startDestination = TransactionsDestination.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(TransactionsDestination.route) { TransactionsScreen(
                    navigateToTransactionForm = {
                        navController.navigate("${TransactionFormDestination.route}/$it")
                    }
                ) }
                composable(BudgetsDestination.route) { BudgetsScreen(
                    navigateToBudgetForm = {
                        navController.navigate("${BudgetFormDestination.route}/$it")
                    },
                    navigateToBudgetDetail = {
                        navController.navigate("${BudgetDetailDestination.route}/$it")
                    },
                ) }
                composable(PeriodicDestination.route) { PeriodicScreen(
                    navigateToPeriodicForm = {
                        navController.navigate("${PeriodicFormDestination.route}/$it")
                    }
                ) }
                composable(ChartsDestination.route) { ChartsScreen() }
                composable(CategoriesDestination.route) { CategoriesScreen(
                    navigateToCategoryForm = {
                        navController.navigate("${CategoryFormDestination.route}/$it")
                    }
                ) }

                // Detail screen
                composable(
                    BudgetDetailDestination.routeWithArgs,
                    arguments = listOf(navArgument(BudgetDetailDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    BudgetDetailScreen()
                }

                // Form screens
                composable(
                    AccountFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(AccountFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    AccountFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction
                    )
                }
                composable(
                    TransactionFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(TransactionFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    TransactionFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction
                    )
                }
                composable(
                    BudgetFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(BudgetFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    BudgetFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction
                    )
                }
                composable(
                    PeriodicFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(PeriodicFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    PeriodicFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction
                    )
                }
                composable(
                    CategoryFormDestination.routeWithArgs,
                    arguments = listOf(navArgument(CategoryFormDestination.ID_ARG) {
                        type = NavType.LongType
                    })
                ) {
                    CategoryFormScreen(
                        navigateBack = { navController.popBackStack() },
                        setFormSubmit = viewModel::setFormSubmitAction
                    )
                }
            }
        }
    }
}
