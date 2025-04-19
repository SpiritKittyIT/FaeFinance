package spirit.realm.faefinance.ui.utility

import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.ETransactionType
import spirit.realm.faefinance.ui.components.Choice

object TransactionTypeUtil {

    fun getChoices(resourceProvider: IAppResourceProvider): List<Choice> {
        return ETransactionType.entries.map { type ->
            Choice(
                title = getTitle(resourceProvider, type),
                value = type.name
            )
        }
    }

    private fun getTitle(resourceProvider: IAppResourceProvider, type: ETransactionType): String {
        return when (type) {
            ETransactionType.Expense -> resourceProvider.getString(R.string.expense_title)
            ETransactionType.Income -> resourceProvider.getString(R.string.income_title)
            ETransactionType.Transfer -> resourceProvider.getString(R.string.transfer_title)
        }
    }
}
