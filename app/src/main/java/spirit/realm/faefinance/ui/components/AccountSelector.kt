package spirit.realm.faefinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.Account
import kotlin.math.roundToInt

/**
 * Composable that displays an individual account selector with the ability to
 * edit, select, and drag an account.
 *
 * @param account The account data to display in the selector.
 * @param activeAccountId The ID of the currently selected account.
 * @param onAccountSelected A callback that is invoked when an account is selected.
 * @param navigateToAccountForm A callback to navigate to the account editing form.
 * @param modifier Modifier to customize the composable.
 */
@Composable
fun AccountSelector(
    account: Account,
    activeAccountId: Long,
    onAccountSelected: (Long) -> Unit,
    navigateToAccountForm: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(6.dp), // Space between elements in the row
        verticalAlignment = Alignment.CenterVertically, // Align items vertically
        modifier = modifier
            .clip(MaterialTheme.shapes.medium) // Applies a medium shape to the container
            .background(account.color) // Sets the background color for the account item
            .padding(6.dp) // Adds padding around the content
    ) {
        // Drag handle icon, only visible when the account is not the default (id != 0)
        Icon(
            Icons.Default.DragHandle,
            contentDescription = stringResource(R.string.menu),
            modifier = Modifier
                .alpha(if (account.id == 0L) { 0f } else { 1f }) // Hides the icon for default account
        )

        // Display account title
        Text(
            account.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f), // Makes the text take up available space
            maxLines = 1, // Ensures the title doesn't overflow
            overflow = TextOverflow.Ellipsis // Ellipsis for overflowed text
        )

        // Edit icon visible only for non-default accounts
        if (account.id != 0L) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
                modifier = Modifier.clickable {
                    navigateToAccountForm(account.id) // Navigate to account form on click
                }
            )
        }

        // Radio button to indicate if the account is selected
        Icon(
            if (activeAccountId == account.id) {
                Icons.Default.RadioButtonChecked // Checked if the account is selected
            }
            else {
                Icons.Default.RadioButtonUnchecked // Unchecked otherwise
            },
            contentDescription = stringResource(R.string.select),
            modifier = Modifier.clickable {
                onAccountSelected(account.id) // Handle selection on click
            }
        )
    }
}

/**
 * Composable that displays an account selector with the ability to drag and reorder accounts.
 *
 * @param index The index of the account in the list.
 * @param account The account data to display in the selector.
 * @param activeAccountId The ID of the currently selected account.
 * @param onAccountSelected A callback that is invoked when an account is selected.
 * @param navigateToAccountForm A callback to navigate to the account editing form.
 * @param onMove A callback to handle the movement of accounts when dragging.
 * @param onDragEnd A callback to handle the end of a drag action.
 * @param modifier Modifier to customize the composable.
 */
@Composable
fun DraggableAccountSelector(
    index: Int,
    account: Account,
    activeAccountId: Long,
    onAccountSelected: (Long) -> Unit,
    navigateToAccountForm: (Long) -> Unit,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State to manage dragging status and the accumulated drag distance
    var isDragging by remember { mutableStateOf(false) }
    var accumulatedDragY by remember { mutableFloatStateOf(0f) }
    val dragThreshold = 34.dp // Drag threshold in dp to trigger movement
    val dragThresholdPx = with(LocalDensity.current) { dragThreshold.toPx() } // Convert to pixels

    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    // Call the AccountSelector Composable for displaying the account item
    AccountSelector(
        account = account,
        activeAccountId = activeAccountId,
        onAccountSelected = onAccountSelected,
        navigateToAccountForm = navigateToAccountForm,
        modifier = modifier
            .zIndex(if (isDragging) 1f else 0f) // Bring the item to the front when dragging
            .alpha(if (isDragging) 0.5f else 1f) // Make it semi-transparent when dragging
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true // Set dragging flag to true when drag starts
                        accumulatedDragY = 0f
                        dragOffsetY = 0f
                    },
                    onDragEnd = {
                        isDragging = false // Set dragging flag to false when drag ends
                        dragOffsetY = 0f
                        onDragEnd() // Invoke the drag end callback
                    },
                    onDragCancel = {
                        isDragging = false // Reset on drag cancel
                        dragOffsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume() // Consume the gesture to prevent default handling
                        dragOffsetY += dragAmount.y
                        accumulatedDragY += dragAmount.y

                        // Check if the drag exceeds the threshold to trigger a move
                        if (accumulatedDragY > dragThresholdPx) {
                            onMove(index, index + 1) // Move item down
                            accumulatedDragY = 0f
                        } else if (accumulatedDragY < -dragThresholdPx) {
                            onMove(index, index - 1) // Move item up
                            accumulatedDragY = 0f
                        }
                    }
                )
            }
            .offset { IntOffset(0, dragOffsetY.roundToInt()) } // Move the item based on drag offset
    )
}
