package spirit.realm.faefinance.ui.utility

import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.ui.components.Choice

/**
 * Utility object for handling transaction intervals.
 *
 * This utility provides methods to generate a list of available transaction interval choices
 * and their corresponding titles in a localized form.
 */
object TransactionIntervalUtil {

    /**
     * Generates a list of transaction interval choices.
     *
     * The list contains choices for different transaction intervals such as Days, Weeks, and Months.
     * The titles for these choices are localized using the provided resource provider.
     *
     * @param resourceProvider The [IAppResourceProvider] used to fetch localized strings.
     * @return A list of [Choice] objects representing different transaction intervals.
     */
    fun getChoices(resourceProvider: IAppResourceProvider): List<Choice> {
        return ETransactionInterval.entries.map { interval ->
            Choice(
                title = getTitle(resourceProvider, interval),  // Localize the interval title
                value = interval.name  // Use the interval's enum name as the value
            )
        }
    }

    /**
     * Retrieves the localized title for a given transaction interval.
     *
     * @param resourceProvider The [IAppResourceProvider] used to fetch localized strings.
     * @param type The [ETransactionInterval] value for which the title is needed.
     * @return A localized string representing the title for the transaction interval.
     */
    private fun getTitle(resourceProvider: IAppResourceProvider, type: ETransactionInterval): String {
        return when (type) {
            ETransactionInterval.Days -> resourceProvider.getString(R.string.days_title)  // Localized title for Days
            ETransactionInterval.Weeks -> resourceProvider.getString(R.string.weeks_title)  // Localized title for Weeks
            ETransactionInterval.Months -> resourceProvider.getString(R.string.months_title)  // Localized title for Months
        }
    }
}
