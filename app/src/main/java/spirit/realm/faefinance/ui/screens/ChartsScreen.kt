package spirit.realm.faefinance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import spirit.realm.faefinance.ui.navigation.NavigationDestination

object ChartsDestination : NavigationDestination {
    override val route = "charts"
}

@Composable
fun ChartsScreen() {
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
