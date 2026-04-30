package com.sinced.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinced.data.entity.Category
import com.sinced.data.repository.CategoryRepository
import com.sinced.data.repository.ItemRepository
import com.sinced.data.repository.LogRepository
import com.sinced.domain.filter.CycleFilter
import com.sinced.domain.filter.FilterState
import com.sinced.domain.filter.ItemFilters
import com.sinced.domain.filter.SortOption
import com.sinced.domain.model.ItemView
import com.sinced.domain.util.DateUtil
import com.sinced.domain.util.ItemDeriver
import com.sinced.prefs.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class MainUiState(
    val items: List<ItemView> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filter: FilterState = FilterState(),
    val isLoading: Boolean = true
)

class MainViewModel(
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val logRepository: LogRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _filter = MutableStateFlow(loadInitialFilter())
    private val _state = MutableStateFlow(MainUiState(filter = _filter.value))
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    init {
        combine(
            itemRepository.observeItemsWithLastLog(),
            categoryRepository.observeAll(),
            _filter
        ) { rows, categories, filter ->
            val today = DateUtil.todayMillis()
            val derived = ItemDeriver.deriveAll(rows, today)
            val visible = ItemFilters.apply(derived, filter)
            MainUiState(
                items = visible,
                categories = categories,
                filter = filter,
                isLoading = false
            )
        }.onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    private fun loadInitialFilter(): FilterState {
        val rawCats = userPreferences.categoryFilter
        return FilterState(
            cycle = userPreferences.cycleFilter,
            sort = userPreferences.sortOption,
            categoryIds = UserPreferences.decodeCategoryIds(rawCats)
        )
    }

    fun setCycleFilter(cycle: CycleFilter) {
        userPreferences.cycleFilter = cycle
        _filter.value = _filter.value.copy(cycle = cycle)
    }

    fun setSort(sort: SortOption) {
        userPreferences.sortOption = sort
        _filter.value = _filter.value.copy(sort = sort)
    }

    fun toggleCategory(categoryId: Long?) {
        val current = _filter.value.categoryIds
        val next = if (current.contains(categoryId)) current - categoryId else current + categoryId
        userPreferences.categoryFilter = UserPreferences.encodeCategoryIds(next)
        _filter.value = _filter.value.copy(categoryIds = next)
    }

    fun clearCategoryFilter() {
        userPreferences.categoryFilter = emptySet()
        _filter.value = _filter.value.copy(categoryIds = emptySet())
    }

    fun quickLog(itemId: Long) {
        viewModelScope.launch {
            logRepository.add(itemId, DateUtil.todayMillis(), null)
        }
    }

    fun archive(itemId: Long) {
        viewModelScope.launch {
            itemRepository.archive(itemId)
        }
    }

    fun unarchive(itemId: Long) {
        viewModelScope.launch {
            itemRepository.unarchive(itemId)
        }
    }
}
