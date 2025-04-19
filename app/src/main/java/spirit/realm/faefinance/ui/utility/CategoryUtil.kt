package spirit.realm.faefinance.ui.utility

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import spirit.realm.faefinance.data.repositories.CategoryRepository
import spirit.realm.faefinance.ui.components.Choice

object CategoryUtil {
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