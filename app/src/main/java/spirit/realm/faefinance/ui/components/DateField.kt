package spirit.realm.faefinance.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.utility.DateFormatterUtil
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate: Date? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (selectedDate == null) null
        else selectedDate.time
                + TimeZone.getTimeZone(ZoneId.systemDefault()).dstSavings
                + TimeZone.getTimeZone(ZoneId.systemDefault()).rawOffset
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    dateText: String,
    onDateTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val parsedDate = DateFormatterUtil.tryParse(dateText)

    val isError = dateText.isBlank() || parsedDate == null || !isValidDate(dateText)

    OutlinedTextField(
        value = dateText,
        onValueChange = {
            onDateTextChange(it)
        },
        label = { Text(label) },
        isError = isError,
        modifier = modifier,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.EditCalendar,
                    contentDescription = stringResource(R.string.pick_date)
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )

    if (showDatePicker) {
        DatePickerModal(
            selectedDate = if (!isError) parsedDate else Date(),
            onDateSelected = {
                it?.let { millis ->
                    val selected = Date(millis)
                    onDateTextChange(DateFormatterUtil.format(selected))
                }
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun isValidDate(dateStr: String): Boolean {
    val date = DateFormatterUtil.tryParse(dateStr)

    if (date == null) return false

    val reformatted = DateFormatterUtil.format(date)
    if (!reformatted.equals(dateStr, ignoreCase = true)) return false

    if (!date.after(Date(0))) return false

    val calendar = Calendar.getInstance().apply { time = date }
    return calendar.get(Calendar.YEAR) < 2100
}
