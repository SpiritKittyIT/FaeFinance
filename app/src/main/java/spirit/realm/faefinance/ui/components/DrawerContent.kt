package spirit.realm.faefinance.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.ui.viewmodels.AppNavigationViewModel

/**
 * DrawerContent displays the contents of the navigation drawer.
 *
 * It shows the app branding, a theme toggle, the account selector (with drag and drop),
 * and navigation buttons to manage accounts and categories.
 *
 * @param navigateToAccountForm Function to navigate to the account form screen.
 * @param navigateToCategories Function to navigate to the category management screen.
 * @param navigationViewModel ViewModel holding app navigation state and actions.
 */
@Composable
fun DrawerContent(
    navigateToAccountForm: (Long) -> Unit,
    navigateToCategories: () -> Unit,
    navigationViewModel: AppNavigationViewModel
) {
    // Observes current app navigation state from the ViewModel
    val state by navigationViewModel.state.collectAsState()

    // Local copy of account list to support drag-and-drop reordering
    var localAccounts by remember(state.accounts) {
        mutableStateOf(if (state.accounts.isNotEmpty()) state.accounts else emptyList())
    }

    // Tracks the currently dragged item's index
    var draggedIndex by remember { mutableIntStateOf(-1) }

    // Handles reordering of accounts within the list
    val onMove = { from: Int, to: Int ->
        if (from != to && to in localAccounts.indices) {
            localAccounts = localAccounts.toMutableList().apply {
                add(to, removeAt(from))
            }
        }
    }

    // Called when drag operation ends; updates the ViewModel
    val onDragEnd = {
        navigationViewModel.updateAccountOrder(localAccounts)
        draggedIndex = -1
    }

    // A pseudo-account representing the "All accounts" option
    val allAccount = Account(
        title = stringResource(R.string.default_account_title),
        color = MaterialTheme.colorScheme.primaryContainer
    )

    // UI: Modal drawer layout containing all content
    ModalDrawerSheet {
        LazyColumn {
            item {
                // Branding and theme switch section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(1000.dp))
                        )
                        // Theme toggle switch (light/dark)
                        Switch(
                            checked = state.isDarkTheme,
                            onCheckedChange = navigationViewModel::setDarkTheme,
                            thumbContent = if (state.isDarkTheme) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            }
                        )
                    }
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        stringResource(R.string.by_creator),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Account selection section
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Selector for "All Accounts"
                    AccountSelector(
                        account = allAccount,
                        activeAccountId = state.activeAccountId,
                        onAccountSelected = navigationViewModel::updateActiveAccount,
                        navigateToAccountForm = navigateToAccountForm,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        // Draggable selectors for each user-defined account
                        localAccounts.mapIndexed { index, account ->
                            DraggableAccountSelector(
                                index = index,
                                account = account,
                                activeAccountId = state.activeAccountId,
                                onAccountSelected = navigationViewModel::updateActiveAccount,
                                navigateToAccountForm = navigateToAccountForm,
                                onMove = onMove,
                                onDragEnd = onDragEnd,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    HorizontalDivider()
                    // Navigation buttons for managing accounts and categories
                    Column(
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Button(
                            onClick = {
                                navigateToAccountForm(0L)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.add_account))
                        }
                        Button(
                            onClick = {
                                navigateToCategories()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.manage_categories))
                        }
                    }
                }
            }
        }
    }
}
