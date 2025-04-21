package spirit.realm.faefinance.ui.utility

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import spirit.realm.faefinance.data.repositories.AccountRepository
import spirit.realm.faefinance.ui.components.Choice
import java.util.Currency

/**
 * Utility object for working with accounts, particularly to provide choices for account selection.
 *
 * This object provides a function to retrieve a list of account choices from the `AccountRepository`,
 * which can be used in UI components such as dropdowns or selection menus.
 */
object AccountUtil {

    /**
     * Retrieves a flow of account choices, formatted for UI display.
     *
     * This function fetches all accounts from the given repository and transforms them into a list of
     * `Choice` objects, which consist of:
     * - `title`: The title of the account (e.g., account name).
     * - `value`: The account's ID as a string, used for identifying the account in UI interactions.
     * - `trailing`: The currency symbol associated with the account.
     *
     * @param accountRepository The repository that provides access to account data.
     * @return A flow that emits a list of `Choice` objects representing available accounts.
     */
    fun getAccountChoices(accountRepository: AccountRepository): Flow<List<Choice>> {
        return accountRepository.getAll().map { accounts ->
            // Transforming the list of accounts into a list of `Choice` objects
            accounts.map { account ->
                Choice(
                    title = account.title, // Account name
                    value = account.id.toString(), // Account ID as a string
                    trailing = Currency.getInstance(account.currency).symbol // Account's currency symbol
                )
            }
        }
    }
}
