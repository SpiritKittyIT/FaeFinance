package spirit.realm.faefinance.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.Account
import androidx.navigation.NavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import spirit.realm.faefinance.DatabaseApplication
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.ui.navigation.Screen

@Composable
fun AccountSelector(
    account: Account,
    activeAccountId: Long,
    onAccountSelected: (Long) -> Unit,
    launchAccountForm: (Account) -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(account.color)
            .padding(6.dp)
    ) {
        Icon(
            Icons.Default.DragHandle,
            contentDescription = stringResource(R.string.menu),
            modifier = Modifier
                .alpha(if (account.id == 0L) { 0f } else { 1f })
        )
        Text(
            account.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (account.id != 0L) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
                modifier = Modifier.clickable {
                    launchAccountForm(account)
                }
            )
        }
        Icon(
            if (activeAccountId == account.id) {
                Icons.Default.RadioButtonChecked
            }
            else {
                Icons.Default.RadioButtonUnchecked
            },
            contentDescription = stringResource(R.string.select),
            modifier = Modifier.clickable {
                onAccountSelected(account.id)
            }
        )
    }
}

@Composable
fun DraggableAccountSelector(
    index: Int,
    account: Account,
    activeAccountId: Long,
    onAccountSelected: (Long) -> Unit,
    launchAccountForm: (Account) -> Unit,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }

    AccountSelector(
        account = account,
        activeAccountId = activeAccountId,
        onAccountSelected = onAccountSelected,
        launchAccountForm = launchAccountForm,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .alpha(if (isDragging) 0.5f else 1f)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val direction = if (dragAmount.y < 0) -1 else 1
                        onMove(index, index + direction)
                    }
                )
            }
    )
}

@Composable
fun useStateISDarkTheme(settings: SettingsDataStore): Pair<Boolean, (Boolean) -> Unit> {
    val scope = rememberCoroutineScope()
    var value by remember { mutableStateOf<Boolean>(false) }

    LaunchedEffect(Unit) {
        value = settings.isDarkTheme.first()
    }

    val setValue: (Boolean) -> Unit = { newValue ->
        scope.launch {
            settings.setDarkTheme(newValue)
            value = newValue
        }
    }

    return Pair(value, setValue)
}

@Composable
fun useStateActiveAccountId(settings: SettingsDataStore): Pair<Long, (Long) -> Unit> {
    val scope = rememberCoroutineScope()
    var value by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        value = settings.activeAccountId.first()
    }

    val setValue: (Long) -> Unit = { newValue ->
        scope.launch {
            settings.setActiveAccountId(newValue)
            value = newValue
        }
    }

    return Pair(value, setValue)
}

@Composable
fun DrawerContent(
    settings: SettingsDataStore,
    app: DatabaseApplication,
    navController: NavController,
    drawerState: DrawerState,
    setFormAccount: (Account) -> Unit
) {
    val (activeAccountId, setActiveAccountId) = useStateActiveAccountId(settings)
    val (isDarkTheme, setIsDarkTheme) = useStateISDarkTheme(settings)

    val allAccount = Account(
        title = stringResource(R.string.default_account_title),
        color = MaterialTheme.colorScheme.primaryContainer
    )

    val scope = rememberCoroutineScope()
    var accounts by remember { mutableStateOf<List<Account>>(emptyList()) }

    // Load accounts once
    LaunchedEffect(Unit) {
        app.container.accountRepository.getAll().collect { fetched ->
            accounts = fetched.sortedBy { it.sortOrder }
        }
    }

    var draggedIndex by remember { mutableIntStateOf(-1) }

    val onMove = { from: Int, to: Int ->
        if (from != to && to in accounts.indices) {
            accounts = accounts.toMutableList().apply {
                add(to, removeAt(from))
            }
        }
    }

    val onDragEnd = {
        scope.launch {
            accounts.forEachIndexed { i, acc ->
                if (acc.sortOrder != i.toLong()) {
                    app.container.accountRepository.update(acc.copy(sortOrder = i.toLong()))
                }
            }
        }
        draggedIndex = -1
    }

    val launchAccountForm: (Account) -> Unit = { formAccount ->
        setFormAccount(formAccount)
        scope.launch {
            drawerState.close()
        }
        navController.navigate(Screen.AccountForm.route)
    }

    ModalDrawerSheet {
        Column {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Row (
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
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = {
                            setIsDarkTheme(it)
                        },
                        thumbContent = if (isDarkTheme) {
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
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    stringResource(R.string.by_creator),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column (
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AccountSelector(allAccount, activeAccountId, setActiveAccountId, launchAccountForm)
                accounts.forEachIndexed { index, account ->
                    DraggableAccountSelector(
                        index = index,
                        account = account,
                        activeAccountId = activeAccountId,
                        onAccountSelected = setActiveAccountId,
                        launchAccountForm = launchAccountForm,
                        onMove = onMove,
                        onDragEnd = onDragEnd,
                        modifier = Modifier
                            .zIndex(if (index == draggedIndex) 1f else 0f)
                    )
                }
                HorizontalDivider()
                Column (
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Button(
                        onClick = {
                            launchAccountForm(Account())
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.add_account))
                    }
                    Button(
                        onClick = {
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
