package spirit.realm.faefinance.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import spirit.realm.faefinance.data.repositories.CategoryRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import spirit.realm.faefinance.data.classes.Category

/**
 * Data class representing the state of the Category form screen.
 * Contains properties related to the category being created or edited.
 */
data class CategoryFormState(
    var title: String = "",
    var symbol: String = "",
    val errorMessage: String? = null,
    val showDeleteDialog: Boolean = false,
    val isDeleteVisible: Boolean = false
)

/**
 * ViewModel class responsible for managing the state and logic of the Category form screen.
 * It allows for creating, editing, and deleting categories.
 */
class CategoryFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Retrieves the category ID passed via the SavedStateHandle
    private val _itemId: Long = savedStateHandle["id"] ?: 0L

    // Mutable state flow to hold the form state (title, symbol, etc.)
    private val _state = MutableStateFlow(CategoryFormState())
    val state: StateFlow<CategoryFormState> = _state.asStateFlow()

    init {
        // Initialize the form state with existing category data if an ID is passed
        viewModelScope.launch {
            if (_itemId != 0L) {
                categoryRepository.getById(_itemId).first().let { category ->
                    _state.value = CategoryFormState(
                        title = category.title,
                        symbol = category.symbol,
                        isDeleteVisible = true
                    )
                }
            }
        }
    }

    // --- State Updates ---
    /**
     * Updates the title of the category.
     * @param newTitle The new title to be set.
     */
    fun updateTitle(newTitle: String) = _state.update { state -> state.copy(title = newTitle) }

    /**
     * Updates the symbol of the category.
     * @param newSymbol The new symbol to be set.
     */
    fun updateSymbol(newSymbol: String) = _state.update { state -> state.copy(symbol = newSymbol) }

    // --- Submit ---
    /**
     * Validates the form data and submits the category (either creating or updating).
     * @param navigateBack Callback function to navigate back after submission.
     */
    fun validateAndSubmit(navigateBack: () -> Unit) {
        val s = _state.value

        // Create or update category instance
        val updatedCategory = Category(
            id = _itemId,
            title = s.title,
            symbol = s.symbol
        )

        navigateBack() // Trigger navigation back after the operation
        viewModelScope.launch {
            // If it's a new category (ID = 0), insert it, otherwise update the existing one
            if (_itemId == 0L) {
                categoryRepository.insert(updatedCategory)
            } else {
                categoryRepository.update(updatedCategory)
            }
        }
    }

    // --- Dialog Control ---
    /**
     * Triggers the display of the delete confirmation dialog.
     */
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }

    /**
     * Dismisses the delete confirmation dialog.
     */
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    /**
     * Deletes the category and navigates back.
     * @param navigateBack Callback function to navigate back after deletion.
     */
    fun deleteItem(navigateBack: () -> Unit) {
        navigateBack() // Trigger navigation back after deletion
        viewModelScope.launch {
            // Delete the category by its ID if it exists
            if (_itemId != 0L) {
                categoryRepository.deleteById(_itemId)
            }
        }
    }
}
