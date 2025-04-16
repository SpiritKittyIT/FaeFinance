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

class Choice(
    val title: String,
    val value: String,
    val leading: String = "",
    val trailing: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteDropdown(
    label: String,
    choices: List<Choice>,
    selected: Choice,
    onSelect: (Choice) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf(selected.title) }

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
