package spirit.realm.faefinance.ui.components

import Converters
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ColorHexField(
    label: String,
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val converter = Converters()
    var hexText by remember(color) { mutableStateOf(converter.colorToString(color)) }
    var isError by remember { mutableStateOf(false) }
    val validColorRegex = remember { Regex("^#[0-9a-fA-F]{6}$") }

    fun updateColor(newHex: String) {
        hexText = newHex
        if (validColorRegex.matches(newHex)) {
            isError = false
            onColorChange(converter.stringToColor(newHex))
        } else {
            isError = newHex.isNotEmpty()
            onColorChange(Color.Unspecified)
        }
    }

    OutlinedTextField(
        value = hexText,
        onValueChange = { updateColor(it) },
        modifier = modifier,
        label = { Text(label) },
        isError = isError,
        supportingText = {
            if (isError) {
                Text("Invalid hex color")
            }
        },
        trailingIcon = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (!isError) color else MaterialTheme.colorScheme.errorContainer,
                        RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
        ),
        singleLine = true,
    )
}
