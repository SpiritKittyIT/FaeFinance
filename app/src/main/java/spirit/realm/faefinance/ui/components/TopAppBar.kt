package spirit.realm.faefinance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    showBottomBar: Boolean,
    currentRoute: String?,
    accountTitle: String?,
    accountBalance: String,
    navController: NavController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()

    if (showBottomBar) {
        CenterAlignedTopAppBar(
            title = {
                Text(accountTitle ?: stringResource(R.string.default_account_title))
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
            }
        )
    }
    else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    when (currentRoute) {
                        Screen.TransactionForm.route -> stringResource(R.string.form_transaction)
                        Screen.BudgetForm.route -> stringResource(R.string.form_budget)
                        Screen.PeriodicForm.route -> stringResource(R.string.form_periodic)
                        else -> ""
                    }
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )
    }
}
