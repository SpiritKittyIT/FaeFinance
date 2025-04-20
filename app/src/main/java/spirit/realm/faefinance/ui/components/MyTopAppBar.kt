package spirit.realm.faefinance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.screens.AccountFormDestination
import spirit.realm.faefinance.ui.screens.BudgetFormDestination
import spirit.realm.faefinance.ui.screens.CategoriesDestination
import spirit.realm.faefinance.ui.screens.CategoryFormDestination
import spirit.realm.faefinance.ui.screens.PeriodicFormDestination
import spirit.realm.faefinance.ui.screens.TransactionFormDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    isMainScreen: Boolean,
    isForm: Boolean,
    currentRoute: String?,
    accountTitle: String?,
    accountBalance: String,
    navController: NavController,
    drawerState: DrawerState,
    submitFunction: () -> Unit
) {
    val scope = rememberCoroutineScope()

    if (isMainScreen) {
        TopAppBar(
            title = {
                Text(
                    accountTitle ?: stringResource(R.string.default_account_title),
                    maxLines = 1,
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = stringResource(R.string.menu)
                    )
                }
            },
            actions = {
                Text(
                    accountBalance,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
        )
    }
    else {
        TopAppBar(
            title = {
                Text(
                    when (currentRoute) {
                        AccountFormDestination.routeWithArgs -> stringResource(R.string.form_account)
                        TransactionFormDestination.routeWithArgs -> stringResource(R.string.form_transaction)
                        BudgetFormDestination.routeWithArgs -> stringResource(R.string.form_budget)
                        PeriodicFormDestination.routeWithArgs -> stringResource(R.string.form_periodic)
                        CategoryFormDestination.routeWithArgs -> stringResource(R.string.form_category)
                        CategoriesDestination.route -> stringResource(R.string.screen_categories)
                        else -> ""
                    },
                    maxLines = 1,
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            actions = {
                if (isForm) {
                    TextButton(
                        onClick = {
                            submitFunction()
                        }
                    ) {
                        Text(
                            stringResource(R.string.confirm),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
        )
    }
}
