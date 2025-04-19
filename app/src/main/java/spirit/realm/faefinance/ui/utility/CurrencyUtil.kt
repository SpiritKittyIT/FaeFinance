package spirit.realm.faefinance.ui.utility

import spirit.realm.faefinance.ui.components.Choice
import java.util.Currency

object CurrencyUtil {
    val currencyChoices: List<Choice> = Currency.getAvailableCurrencies()
        .sortedBy { it.displayName }
        .map { currency ->
            Choice(
                title = currency.displayName,
                value = currency.currencyCode,
                trailing = currency.symbol
            )
        }
}
