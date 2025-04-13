package spirit.realm.faefinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import spirit.realm.faefinance.ui.navigation.AppNavigation
import spirit.realm.faefinance.ui.theme.FaeFinanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaeFinanceTheme {
                AppNavigation()
            }
        }
    }
}
