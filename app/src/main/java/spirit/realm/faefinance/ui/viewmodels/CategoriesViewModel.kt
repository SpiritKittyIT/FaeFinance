package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.classes.Category
import spirit.realm.faefinance.data.repositories.CategoryRepository

/**
 * Data class representing the state of the Categories screen.
 * Holds the list of categories to be displayed.
 */
data class CategoriesState(
    val categories: List<Category> = emptyList()
)

/**
 * ViewModel class responsible for managing the state of the Categories screen.
 * It interacts with the CategoryRepository to fetch the list of categories and updates the state.
 */
class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Mutable state flow to hold the list of categories
    private val _state = MutableStateFlow(CategoriesState())
    val state: StateFlow<CategoriesState> = _state.asStateFlow()

    init {
        // Launch a coroutine to fetch all categories from the repository
        viewModelScope.launch {
            categoryRepository.getAll()
                .collect { list ->
                    _state.update { it.copy(categories = list) }
                }
        }
    }
}
