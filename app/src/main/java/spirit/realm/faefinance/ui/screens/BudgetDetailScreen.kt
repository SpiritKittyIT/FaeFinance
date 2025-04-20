package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import spirit.realm.faefinance.ui.navigation.NavigationDestination
import spirit.realm.faefinance.ui.viewmodels.AppViewModelProvider
import spirit.realm.faefinance.ui.viewmodels.BudgetDetailViewModel

object BudgetDetailDestination : NavigationDestination {
    override val route = "budget_detail"
    const val ID_ARG = "id"
    val routeWithArgs = "${route}/{$ID_ARG}"
}

@Composable
fun BudgetDetailScreen(
    viewModel: BudgetDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LazyColumn {
        item {
            Column (
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(8.dp)
            ) {

            }
        }
    }
}
