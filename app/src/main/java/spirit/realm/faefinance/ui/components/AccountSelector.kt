package spirit.realm.faefinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
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
    var accumulatedDragY by remember { mutableFloatStateOf(0f) }
    val dragThreshold = 34.dp // feel good estimate
    val dragThresholdPx = with(LocalDensity.current) { dragThreshold.toPx() }

    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    AccountSelector(
        account = account,
        activeAccountId = activeAccountId,
        onAccountSelected = onAccountSelected,
        launchAccountForm = launchAccountForm,
        modifier = modifier
            .zIndex(if (isDragging) 1f else 0f)
            .alpha(if (isDragging) 0.5f else 1f)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        accumulatedDragY = 0f
                        dragOffsetY = 0f
                    },
                    onDragEnd = {
                        isDragging = false
                        dragOffsetY = 0f
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetY += dragAmount.y
                        accumulatedDragY += dragAmount.y

                        if (accumulatedDragY > dragThresholdPx) {
                            onMove(index, index + 1)
                            accumulatedDragY = 0f
                        } else if (accumulatedDragY < -dragThresholdPx) {
                            onMove(index, index - 1)
                            accumulatedDragY = 0f
                        }
                    }
                )
            }
            .offset { IntOffset(0, dragOffsetY.roundToInt()) }
    )
}
