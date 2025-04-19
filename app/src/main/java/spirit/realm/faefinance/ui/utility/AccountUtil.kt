package spirit.realm.faefinance.ui.utility

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import spirit.realm.faefinance.data.repositories.AccountRepository
import spirit.realm.faefinance.ui.components.Choice
import java.util.Currency

object AccountUtil {
    fun getAccountChoices(accountRepository: AccountRepository): Flow<List<Choice>> {
        return accountRepository.getAll().map { accounts ->
            accounts.map { account ->
                Choice(
                    title = account.title,
                    value = account.id.toString(),
                    trailing = Currency.getInstance(account.currency).symbol
                )
            }
        }
    }
}