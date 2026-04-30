package com.sinced.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinced.data.entity.Category
import com.sinced.data.repository.CategoryRepository
import com.sinced.ui.theme.CategoryPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class CategoryManageState(
    val categories: List<Category> = emptyList()
)

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryManageState())
    val state: StateFlow<CategoryManageState> = _state.asStateFlow()

    init {
        repository.observeAll()
            .onEach { _state.value = CategoryManageState(it) }
            .launchIn(viewModelScope)
    }

    fun add(name: String, color: Int? = nextDefaultColor()) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val sortOrder = _state.value.categories.size
        viewModelScope.launch {
            repository.add(name = trimmed, color = color, sortOrder = sortOrder)
        }
    }

    fun rename(category: Category, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty() || trimmed == category.name) return
        viewModelScope.launch {
            repository.update(category.copy(name = trimmed))
        }
    }

    fun setColor(category: Category, color: Int?) {
        viewModelScope.launch {
            repository.update(category.copy(color = color))
        }
    }

    fun moveUp(category: Category) {
        val list = _state.value.categories.toMutableList()
        val idx = list.indexOfFirst { it.id == category.id }
        if (idx <= 0) return
        val above = list[idx - 1]
        list[idx - 1] = category
        list[idx] = above
        viewModelScope.launch { repository.reorder(list) }
    }

    fun moveDown(category: Category) {
        val list = _state.value.categories.toMutableList()
        val idx = list.indexOfFirst { it.id == category.id }
        if (idx < 0 || idx >= list.lastIndex) return
        val below = list[idx + 1]
        list[idx + 1] = category
        list[idx] = below
        viewModelScope.launch { repository.reorder(list) }
    }

    fun delete(category: Category) {
        viewModelScope.launch {
            repository.delete(category)
        }
    }

    suspend fun countItemsInCategory(category: Category): Int =
        repository.countItems(category.id)

    private fun nextDefaultColor(): Int {
        val used = _state.value.categories.mapNotNull { it.color }.toSet()
        return CategoryPalette.presets.firstOrNull { it !in used } ?: CategoryPalette.presets.first()
    }
}
