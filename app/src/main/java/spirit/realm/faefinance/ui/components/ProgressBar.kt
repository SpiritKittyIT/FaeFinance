package spirit.realm.faefinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    amountSpent: Double,
    amountMax: Double,
    modifier: Modifier = Modifier
) {
    val ratio = if (amountMax > 0) (amountSpent / amountMax).coerceIn(0.0, 1.0) else 0.0

    Box(
        modifier = modifier
            .height(24.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(ratio.toFloat())
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        )

        Text(
            text = "${"%.0f".format(amountSpent)} / ${"%.0f".format(amountMax)}",
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}
