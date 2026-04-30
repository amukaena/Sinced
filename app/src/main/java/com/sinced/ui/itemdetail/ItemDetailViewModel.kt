package com.sinced.ui.itemdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinced.data.entity.Category
import com.sinced.data.entity.Item
import com.sinced.data.entity.LogEntry
import com.sinced.data.repository.CategoryRepository
import com.sinced.data.repository.ItemRepository
import com.sinced.data.repository.LogRepository
import com.sinced.data.relation.ItemWithLastLog
import com.sinced.domain.model.ItemView
import com.sinced.domain.util.DateUtil
import com.sinced.domain.util.ItemDeriver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ItemDetailUiState(
    val item: Item? = null,
    val view: ItemView? = null,
    val category: Category? = null,
    val logs: List<LogEntry> = emptyList(),
    val deleted: Boolean = false
)

class ItemDetailViewModel(
    private val itemRepository: ItemRepository,
    private val logRepository: LogRepository,
    private val categoryRepository: CategoryRepository,
    private val itemId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(ItemDetailUiState())
    val state: StateFlow<ItemDetailUiState> = _state.asStateFlow()

    init {
        combine(
            itemRepository.observeById(itemId),
            logRepository.observeByItem(itemId),
            categoryRepository.observeAll()
        ) { item, logs, categories ->
            if (item == null) {
                ItemDetailUiState()
            } else {
                val today = DateUtil.todayMillis()
                val row = ItemWithLastLog(
                    id = item.id,
                    name = item.name,
                    categoryId = item.categoryId,
                    cycleDays = item.cycleDays,
                    memo = item.memo,
                    sortOrder = item.sortOrder,
                    createdAt = item.createdAt,
                    archivedAt = item.archivedAt,
                    lastPerformedAt = logs.maxOfOrNull { it.performedAt }
                )
                ItemDetailUiState(
                    item = item,
                    view = ItemDeriver.derive(row, today),
                    category = categories.firstOrNull { it.id == item.categoryId },
                    logs = logs
                )
            }
        }.onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun addLog(performedAt: Long, note: String?) {
        viewModelScope.launch {
            logRepository.add(itemId, performedAt, note)
        }
    }

    fun updateLog(id: Long, performedAt: Long, note: String?) {
        viewModelScope.launch {
            logRepository.update(id, performedAt, note)
        }
    }

    fun deleteLog(entry: LogEntry) {
        viewModelScope.launch {
            logRepository.delete(entry)
        }
    }

    fun archive() {
        viewModelScope.launch {
            itemRepository.archive(itemId)
            _state.value = _state.value.copy(deleted = true)
        }
    }

    fun deletePermanently() {
        viewModelScope.launch {
            itemRepository.delete(itemId)
            _state.value = _state.value.copy(deleted = true)
        }
    }
}
