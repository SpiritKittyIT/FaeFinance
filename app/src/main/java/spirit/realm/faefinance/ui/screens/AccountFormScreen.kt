package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import spirit.realm.faefinance.DatabaseApplication
import spirit.realm.faefinance.data.classes.Account
import spirit.realm.faefinance.ui.components.AutocompleteDropdown
import spirit.realm.faefinance.ui.components.Choice
import java.util.Currency
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.components.ColorHexField

@Composable
fun AccountFormScreen(navController: NavController, app: DatabaseApplication, formAccount: Account, setFormSubmit: (() -> Unit) -> Unit) {
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Track form state
    var title by remember { mutableStateOf(TextFieldValue(formAccount.title)) }
    var currency by remember { mutableStateOf(
        if (formAccount.currency == "") {
            Choice(
                title = "",
                value = ""
            )
        }
        else {
            val currency = Currency.getInstance(formAccount.currency)
            Choice(
                title = currency.displayName,
                value = currency.currencyCode,
                trailing = {Text(currency.symbol)}
            )
        }
    ) }
    var currencyName by remember { mutableStateOf(currency.title) }
    var balance by remember { mutableStateOf(formAccount.balance.toString()) }
    var color by remember { mutableStateOf(formAccount.color) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val currencyChoices = Currency.getAvailableCurrencies()
        .filter { currency -> currency.displayName.contains(currencyName) }
        .sortedBy { currency -> currency.displayName }
        .map { currency -> Choice(currency.displayName, currency.currencyCode, trailing = {Text(currency.symbol)} ) }

    fun validateForm(): Boolean {
        errorMessage = ""

        // Validate balance
        if (balance.toDoubleOrNull() == null) {
            errorMessage = context.getString(R.string.valid_number_for_balance)
        }

        // Validate color (basic check - you might want more specific validation)
        if (color == Color.Unspecified) {
            errorMessage = "${errorMessage}${context.getString(R.string.select_a_valid_color)}"
        }

        return errorMessage == ""
    }

    // Passing the form submit function to the TopBar (through setFormSubmit)
    setFormSubmit {
        if (validateForm()) {
            scope.launch {
                // Handle form submit
                val account = formAccount.copy(
                    title = title.text,
                    currency = currency.value,
                    balance = balance.toDoubleOrNull() ?: 0.0,
                    color = color
                )

                if (formAccount.id == 0L) {
                    // Insert new account if id is 0
                    app.container.accountRepository.insert(account)
                } else {
                    // Update existing account
                    app.container.accountRepository.update(account)
                }

                // Navigate back after submitting
                navController.popBackStack()
            }
        }
        else {
            showErrorDialog = true
        }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
            ),
            singleLine = true,
        )
        AutocompleteDropdown(
            "Currency",
            currencyChoices,
            currency
        ) { newChoice ->
            currency = newChoice
        }
        OutlinedTextField(
            value = balance,
            onValueChange = { balance = it },
            label = { Text(stringResource(R.string.balance)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
        )
        ColorHexField(
            label = stringResource(R.string.color),
            color = color,
            onColorChange = { newColor -> color = newColor },
            modifier = Modifier.fillMaxWidth()
        )
        // Delete Button, only visible if it's an existing account (id != 0)
        if (formAccount.id != 0L) {
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.delete_account))
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(stringResource(R.string.invalid_input)) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.confirm_deletion)) },
            text = { Text(stringResource(R.string.are_you_sure_delete)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            app.container.accountRepository.delete(formAccount)
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
