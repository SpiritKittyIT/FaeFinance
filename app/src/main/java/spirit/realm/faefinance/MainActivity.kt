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

/**
 * The main entry point of the application.
 *
 * This activity is responsible for setting up the app's theme and navigation system.
 * It also manages the system's dark mode and user-specific theme preferences.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is created.
     *
     * This method sets up the UI, including enabling edge-to-edge display, reading
     * the dark theme preference from settings or the system, and initializing
     * the navigation.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge display (allowing content to extend to the edges of the screen)
        enableEdgeToEdge()

        // Set the content of the activity using Jetpack Compose
        setContent {
            val context = LocalContext.current
            val settings = SettingsDataStore(context)
            val systemIsDark = isSystemInDarkTheme()
            val darkTheme by settings.isDarkTheme.collectAsState(initial = systemIsDark)

            FaeFinanceTheme(darkTheme = darkTheme) {
                AppNavigation()
            }
        }
    }
}
