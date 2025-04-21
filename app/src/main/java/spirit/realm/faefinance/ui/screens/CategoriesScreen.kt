package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.R
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.CategoriesViewModel

/**
 * Navigation destination for the Categories screen.
 */
object CategoriesDestination : NavigationDestination {
    override val route = "categories"
}

/**
 * Displays a list of budget categories.
 * Each category includes its symbol, title, and an edit icon.
 *
 * @param navigateToCategoryForm Callback to navigate to the form for editing a category.
 * @param viewModel Backing ViewModel for state management.
 */
@Composable
fun CategoriesScreen(
    navigateToCategoryForm: (Long) -> Unit,
    viewModel: CategoriesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        itemsIndexed(state.categories) { index, category ->
            Card {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    // Symbol circle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = category.symbol,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Category title
                    Text(
                        text = category.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    // Edit icon
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        modifier = Modifier.clickable {
                            navigateToCategoryForm(category.id)
                        }
                    )
                }
            }

            // Extra bottom padding for the last item
            if (index == state.categories.lastIndex) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                )
            }
        }
    }
}
