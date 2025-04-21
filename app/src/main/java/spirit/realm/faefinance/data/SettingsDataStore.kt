package spirit.realm.faefinance.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.ETransactionInterval

val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

class SettingsDataStore(context: Context) {

    private val dataStore = context.settingsDataStore

    companion object {
        val ACTIVE_ACCOUNT_ID = longPreferencesKey("default_account_id")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val CHARTS_INTERVAL = stringPreferencesKey("charts_interval")
        val CHARTS_INTERVAL_LEN = intPreferencesKey("charts_interval_len")
        val CHARTS_TYPE = stringPreferencesKey("charts_type")
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

    val chartsInterval: Flow<String> = dataStore.data.map { prefs ->
        prefs[CHARTS_INTERVAL] ?: ETransactionInterval.Months.name
    }

    suspend fun setChartsInterval(interval: String) {
        dataStore.edit { prefs ->
            prefs[CHARTS_INTERVAL] = interval
        }
    }

    val chartsIntervalLen: Flow<Int> = dataStore.data.map { prefs ->
        prefs[CHARTS_INTERVAL_LEN] ?: 6
    }

    suspend fun setChartsIntervalLen(len: Int) {
        dataStore.edit { prefs ->
            prefs[CHARTS_INTERVAL_LEN] = len
        }
    }

    val chartsType: Flow<String> = dataStore.data.map { prefs ->
        prefs[CHARTS_TYPE] ?: ""
    }

    suspend fun setChartsType(type: String) {
        dataStore.edit { prefs ->
            prefs[CHARTS_TYPE] = type
        }
    }
}