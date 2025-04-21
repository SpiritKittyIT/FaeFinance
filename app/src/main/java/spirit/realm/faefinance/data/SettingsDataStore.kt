package spirit.realm.faefinance.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import spirit.realm.faefinance.data.classes.ETransactionInterval

// Initialize the DataStore for app settings.
val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

/**
 * Manages app settings using DataStore for persistent storage.
 * This class allows getting and setting various app preferences.
 */
class SettingsDataStore(context: Context) {

    private val dataStore = context.settingsDataStore

    companion object {
        // Keys to store and retrieve settings from DataStore.
        val ACTIVE_ACCOUNT_ID = longPreferencesKey("default_account_id")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val CHARTS_INTERVAL = stringPreferencesKey("charts_interval")
        val CHARTS_INTERVAL_LEN = intPreferencesKey("charts_interval_len")
        val CHARTS_TYPE = stringPreferencesKey("charts_type")
    }

    /**
     * Retrieves the ID of the active account.
     * Defaults to 0L if not set.
     */
    val activeAccountId: Flow<Long> = dataStore.data.map { prefs ->
        prefs[ACTIVE_ACCOUNT_ID] ?: 0L
    }

    /**
     * Sets the ID of the active account.
     *
     * @param id The ID to set as the active account.
     */
    suspend fun setActiveAccountId(id: Long) {
        dataStore.edit { prefs ->
            prefs[ACTIVE_ACCOUNT_ID] = id
        }
    }

    /**
     * Retrieves the dark theme setting.
     * Defaults to false if not set.
     */
    val isDarkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_DARK_THEME] == true
    }

    /**
     * Sets the dark theme setting.
     *
     * @param enabled Whether dark theme should be enabled or not.
     */
    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_DARK_THEME] = enabled
        }
    }

    /**
     * Retrieves the chart interval setting.
     * Defaults to "Months" if not set.
     */
    val chartsInterval: Flow<String> = dataStore.data.map { prefs ->
        prefs[CHARTS_INTERVAL] ?: ETransactionInterval.Months.name
    }

    /**
     * Sets the chart interval setting.
     *
     * @param interval The interval to set (e.g., "Months", "Days").
     */
    suspend fun setChartsInterval(interval: String) {
        dataStore.edit { prefs ->
            prefs[CHARTS_INTERVAL] = interval
        }
    }

    /**
     * Retrieves the length of the interval.
     * Defaults to 6 if not set.
     */
    val chartsIntervalLen: Flow<Int> = dataStore.data.map { prefs ->
        prefs[CHARTS_INTERVAL_LEN] ?: 6
    }

    /**
     * Sets the length of the chart interval.
     *
     * @param len The number of intervals to set (e.g., 6, 12).
     */
    suspend fun setChartsIntervalLen(len: Int) {
        dataStore.edit { prefs ->
            prefs[CHARTS_INTERVAL_LEN] = len
        }
    }

    /**
     * Retrieves the type of transactions displayed.
     * Defaults to an empty string if not set.
     */
    val chartsType: Flow<String> = dataStore.data.map { prefs ->
        prefs[CHARTS_TYPE] ?: ""
    }

    /**
     * Sets the type of transactions to be displayed.
     *
     * @param type The chart type to set.
     */
    suspend fun setChartsType(type: String) {
        dataStore.edit { prefs ->
            prefs[CHARTS_TYPE] = type
        }
    }
}
