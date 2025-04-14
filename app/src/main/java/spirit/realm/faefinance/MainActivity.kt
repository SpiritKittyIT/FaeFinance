package spirit.realm.faefinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import spirit.realm.faefinance.data.SettingsDataStore
import spirit.realm.faefinance.ui.navigation.AppNavigation
import spirit.realm.faefinance.ui.theme.FaeFinanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val settings = SettingsDataStore(context)
            val systemIsDark = isSystemInDarkTheme()
            val darkTheme by settings.isDarkTheme.collectAsState(initial = systemIsDark)

            FaeFinanceTheme (darkTheme = darkTheme) {
                AppNavigation()
            }
        }
    }
}
