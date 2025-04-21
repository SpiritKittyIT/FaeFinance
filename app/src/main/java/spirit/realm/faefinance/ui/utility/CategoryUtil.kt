package spirit.realm.faefinance.ui.utility

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import spirit.realm.faefinance.data.repositories.CategoryRepository
import spirit.realm.faefinance.ui.components.Choice

/**
 * Utility class to fetch and transform category data into a list of choices.
 *
 * This object provides a helper function to fetch categories from the repository and map them
 * into a list of [Choice] objects, which are used for selection in UI components like dropdowns.
 */
object CategoryUtil {

    /**
     * Retrieves a list of category choices from the [CategoryRepository].
     *
     * This function fetches all categories from the repository, then transforms them into
     * a list of [Choice] objects. Each [Choice] consists of the category's title, ID (as string),
     * and symbol to be displayed in the UI.
     *
     * @param categoryRepository The repository that provides access to categories.
     * @return A flow emitting a list of [Choice] objects representing the available categories.
     */
    fun getCategoryChoices(categoryRepository: CategoryRepository): Flow<List<Choice>> {
        return categoryRepository.getAll().map { categories ->
            categories.map { category ->
                Choice(
                    title = category.title,
                    value = category.id.toString(),
                    trailing = category.symbol
                )
            }
        }
    }
}
