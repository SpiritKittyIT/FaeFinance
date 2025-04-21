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

/**
 * Composable that displays a DatePicker in a modal dialog.
 *
 * @param selectedDate The currently selected date, if any.
 * @param onDateSelected Callback function to be invoked when a date is selected.
 * @param onDismiss Callback function to be invoked when the modal is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate: Date? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    // Initialize the date picker state with the selected date (if any)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (selectedDate == null) null
        else selectedDate.time
                + TimeZone.getTimeZone(ZoneId.systemDefault()).dstSavings
                + TimeZone.getTimeZone(ZoneId.systemDefault()).rawOffset
    )

    // Display the DatePickerDialog with confirm and dismiss buttons
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // When the confirm button is clicked, pass the selected date to the callback
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            // When the cancel button is clicked, simply dismiss the dialog
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        // Display the DatePicker inside the dialog
        DatePicker(state = datePickerState)
    }
}

/**
 * Composable that displays a text field for date input and launches a DatePicker when clicked.
 *
 * @param label The label for the date input field.
 * @param dateText The currently displayed date in the text field.
 * @param onDateTextChange Callback function to be invoked when the date text changes.
 * @param modifier A modifier to be applied to the OutlinedTextField.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    dateText: String,
    onDateTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State to control the visibility of the date picker
    var showDatePicker by remember { mutableStateOf(false) }

    // Attempt to parse the date text into a Date object
    val parsedDate = DateFormatterUtil.tryParse(dateText)

    // Determine if the input date is invalid
    val isError = dateText.isBlank() || parsedDate == null || !isValidDate(dateText)

    // Display the OutlinedTextField with date input
    OutlinedTextField(
        value = dateText,
        onValueChange = {
            onDateTextChange(it) // Update the date text when the value changes
        },
        label = { Text(label) },
        isError = isError, // Display error state if the date is invalid
        modifier = modifier,
        trailingIcon = {
            // Icon button that triggers the date picker when clicked
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.EditCalendar,
                    contentDescription = stringResource(R.string.pick_date)
                )
            }
        },
        singleLine = true, // Ensure the input field is a single line
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Set keyboard type to number
    )

    // Show the DatePickerModal if the date picker is visible
    if (showDatePicker) {
        DatePickerModal(
            selectedDate = if (!isError) parsedDate else Date(),
            onDateSelected = {
                it?.let { millis -> // If a date is selected, update the date text
                    val selected = Date(millis)
                    onDateTextChange(DateFormatterUtil.format(selected))
                }
            },
            onDismiss = { showDatePicker = false } // Hide the date picker on dismiss
        )
    }
}

/**
 * Helper function to validate if the given date string is a valid date.
 *
 * @param dateStr The date string to validate.
 * @return True if the date is valid, false otherwise.
 */
@Composable
fun isValidDate(dateStr: String): Boolean {
    val date = DateFormatterUtil.tryParse(dateStr)

    if (date == null) return false // Return false if the date cannot be parsed

    // Reformat the date and compare it with the original string to ensure consistency
    val reformatted = DateFormatterUtil.format(date)
    if (!reformatted.equals(dateStr, ignoreCase = true)) return false

    // Ensure the date is after the epoch (January 1, 1970)
    if (!date.after(Date(0))) return false

    // Ensure the year is less than 2100
    val calendar = Calendar.getInstance().apply { time = date }
    return calendar.get(Calendar.YEAR) < 2100
}
