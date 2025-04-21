package spirit.realm.faefinance.ui.utility

import spirit.realm.faefinance.ui.components.Choice
import java.util.Currency

/**
 * Utility object for working with currencies in the app.
 *
 * This object provides a list of all available currencies, sorted by their display names.
 * Each currency is mapped into a [Choice] object, which contains the currency's display name,
 * currency code, and symbol for use in UI components such as dropdowns.
 */
object CurrencyUtil {

    /**
     * A sorted list of currency choices representing all available currencies.
     *
     * This list is derived from the available currencies in the system, sorted alphabetically
     * by their display names. Each currency is represented as a [Choice] object containing the
     * currency's display name, its currency code, and its symbol, to be used in UI components
     * like dropdowns for currency selection.
     */
    val currencyChoices: List<Choice> = Currency.getAvailableCurrencies()
        .sortedBy { it.displayName }  // Sort currencies alphabetically by display name
        .map { currency ->
            Choice(
                title = currency.displayName,    // The name of the currency
                value = currency.currencyCode,   // The currency code (e.g., USD, EUR)
                trailing = currency.symbol       // The symbol of the currency (e.g., $, €)
            )
        }
}
