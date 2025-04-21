package spirit.realm.faefinance.ui.utility

import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.ui.components.Choice

/**
 * Utility object for handling transaction types.
 *
 * This utility provides methods to generate a list of available transaction type choices
 * and their corresponding localized titles.
 */
object TransactionTypeUtil {

    /**
     * Generates a list of transaction type choices.
     *
     * The list contains choices for different transaction types such as Expense, Income, and Transfer.
     * The titles for these choices are localized using the provided resource provider.
     *
     * @param resourceProvider The [IAppResourceProvider] used to fetch localized strings.
     * @return A list of [Choice] objects representing different transaction types.
     */
    fun getChoices(resourceProvider: IAppResourceProvider): List<Choice> {
        return ETransactionType.entries.map { type ->
            Choice(
                title = getTitle(resourceProvider, type),  // Localize the transaction type title
                value = type.name  // Use the transaction type's enum name as the value
            )
        }
    }

    /**
     * Retrieves the localized title for a given transaction type.
     *
     * @param resourceProvider The [IAppResourceProvider] used to fetch localized strings.
     * @param type The [ETransactionType] value for which the title is needed.
     * @return A localized string representing the title for the transaction type.
     */
    private fun getTitle(resourceProvider: IAppResourceProvider, type: ETransactionType): String {
        return when (type) {
            ETransactionType.Expense -> resourceProvider.getString(R.string.expense_title)  // Localized title for Expense
            ETransactionType.Income -> resourceProvider.getString(R.string.income_title)  // Localized title for Income
            ETransactionType.Transfer -> resourceProvider.getString(R.string.transfer_title)  // Localized title for Transfer
        }
    }
}
