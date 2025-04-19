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

data class CategoryFormState(
    var title: String = "",
    var symbol: String = "",
    val errorMessage: String? = null,
    val isSubmitSuccessful: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleteVisible: Boolean = false
)

class CategoryFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoryId: Long = savedStateHandle["id"] ?: 0L

    private val _state = MutableStateFlow(CategoryFormState())
    val state: StateFlow<CategoryFormState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (_categoryId != 0L) {
                categoryRepository.getById(_categoryId).collect { category ->
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
    fun updateTitle(newTitle: String) = _state.update { state -> state.copy(title = newTitle) }
    fun updateSymbol(newSymbol: String) = _state.update { state -> state.copy(symbol = newSymbol) }

    // --- Submit ---
    fun validateAndSubmit() {
        val s = _state.value

        val updatedCategory = Category(
            id = _categoryId,
            title = s.title,
            symbol = s.symbol
        )

        viewModelScope.launch {
            if (_categoryId == 0L) {
                categoryRepository.insert(updatedCategory)
            } else {
                categoryRepository.update(updatedCategory)
            }
            _state.update { it.copy(isSubmitSuccessful = true) }
        }
    }

    // --- Dialog Control ---
    fun triggerDeleteDialog() = _state.update { it.copy(showDeleteDialog = true) }
    fun dismissDeleteDialog() = _state.update { it.copy(showDeleteDialog = false) }

    fun deleteCategory() {
        viewModelScope.launch {
            categoryRepository.getById(_categoryId).first().let {
                categoryRepository.delete(it)
                _state.update { it.copy(isSubmitSuccessful = true) }
            }
        }
    }
}
