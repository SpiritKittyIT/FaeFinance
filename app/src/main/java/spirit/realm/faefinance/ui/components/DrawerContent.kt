package spirit.realm.faefinance.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DrawerContent() {
    ModalDrawerSheet {
        Text("Drawer Content Here", style = MaterialTheme.typography.bodyLarge)
    }
}
