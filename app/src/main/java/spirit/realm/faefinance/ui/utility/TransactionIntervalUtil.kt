package spirit.realm.faefinance.ui.utility

import spirit.realm.faefinance.R
import spirit.realm.faefinance.data.classes.ETransactionInterval
import spirit.realm.faefinance.ui.components.Choice

object TransactionIntervalUtil {

    fun getChoices(resourceProvider: IAppResourceProvider): List<Choice> {
        return ETransactionInterval.entries.map { interval ->
            Choice(
                title = getTitle(resourceProvider, interval),
                value = interval.name
            )
        }
    }

    private fun getTitle(resourceProvider: IAppResourceProvider, type: ETransactionInterval): String {
        return when (type) {
            ETransactionInterval.Days -> resourceProvider.getString(R.string.days_title)
            ETransactionInterval.Weeks -> resourceProvider.getString(R.string.weeks_title)
            ETransactionInterval.Months -> resourceProvider.getString(R.string.months_title)
        }
    }
}