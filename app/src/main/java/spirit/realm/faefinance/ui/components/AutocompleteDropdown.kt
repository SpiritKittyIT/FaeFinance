package spirit.realm.faefinance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * Data class representing a single choice in the autocomplete dropdown.
 *
 * @param title The display title of the choice.
 * @param value The underlying value of the choice.
 * @param leading Optional leading icon text.
 * @param trailing Optional trailing icon text.
 */
class Choice(
    val title: String,
    val value: String,
    val leading: String = "",
    val trailing: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * A composable function that displays an autocomplete dropdown menu.
 *
 * @param label The label to display for the dropdown field.
 * @param choices A list of available choices to display in the dropdown.
 * @param selected The currently selected choice.
 * @param onSelect A callback function to handle when a new choice is selected.
 * @param modifier A [Modifier] to customize the UI appearance of the dropdown.
 */
fun AutocompleteDropdown(
    label: String,
    choices: List<Choice>,
    selected: Choice,
    onSelect: (Choice) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember(selected.title) { mutableStateOf(selected.title) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    enabled = true,
                    type = MenuAnchorType.PrimaryEditable,
                ),
            leadingIcon = if (selected.leading != "") { { Text(selected.leading) } } else { null },
            trailingIcon = if (selected.trailing != "") { { Text(selected.trailing) } } else { null },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
            ),
            singleLine = true,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            choices.filter { it.title.contains(query, ignoreCase = true) }
                .forEach { choice ->
                    DropdownMenuItem(
                        text = { Text(choice.title) },
                        onClick = {
                            query = choice.title
                            onSelect(choice)
                            expanded = false
                        },
                        leadingIcon = if (selected.leading != "") { { Text(selected.leading) } } else { null },
                        trailingIcon = if (selected.trailing != "") { { Text(selected.trailing) } } else { null },
                    )
                }
        }
    }
}
