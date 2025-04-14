package spirit.realm.faefinance.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

class SettingsDataStore(context: Context) {

    private val dataStore = context.settingsDataStore

    companion object {
        val ACTIVE_ACCOUNT_ID = longPreferencesKey("default_account_id")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    val activeAccountId: Flow<Long> = dataStore.data.map { prefs ->
        prefs[ACTIVE_ACCOUNT_ID] ?: 0L
    }

    suspend fun setActiveAccountId(id: Long) {
        dataStore.edit { prefs ->
            prefs[ACTIVE_ACCOUNT_ID] = id
        }
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_DARK_THEME] == true
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_DARK_THEME] = enabled
        }
    }
}