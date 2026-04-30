package com.sinced.ui.itemedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinced.data.entity.Category
import com.sinced.data.repository.CategoryRepository
import com.sinced.data.repository.ItemRepository
import com.sinced.domain.util.DateUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItemEditUiState(
    val isNew: Boolean = true,
    val name: String = "",
    val categoryId: Long? = null,
    val cycleEnabled: Boolean = false,
    val cycleDays: String = "",
    val memo: String = "",
    val firstLogEnabled: Boolean = false,
    val firstLogAtMillis: Long = DateUtil.todayMillis(),
    val categories: List<Category> = emptyList(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    val nameError: String? = null,
    val cycleError: String? = null
)

class ItemEditViewModel(
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val itemId: Long?
) : ViewModel() {

    private val _state = MutableStateFlow(ItemEditUiState(isNew = itemId == null))
    val state: StateFlow<ItemEditUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val cats = categoryRepository.getAll()
            _state.value = _state.value.copy(categories = cats)

            if (itemId != null) {
                val existing = itemRepository.getById(itemId)
                if (existing != null) {
                    _state.value = _state.value.copy(
                        isNew = false,
                        name = existing.name,
                        categoryId = existing.categoryId,
                        cycleEnabled = existing.cycleDays != null,
                        cycleDays = existing.cycleDays?.toString().orEmpty(),
                        memo = existing.memo.orEmpty()
                    )
                }
            }
        }
    }

    fun setName(value: String) {
        _state.value = _state.value.copy(name = value, nameError = null)
    }

    fun setCategory(id: Long?) {
        _state.value = _state.value.copy(categoryId = id)
    }

    fun setCycleEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(cycleEnabled = enabled, cycleError = null)
    }

    fun setCycleDays(value: String) {
        _state.value = _state.value.copy(cycleDays = value.filter { it.isDigit() }, cycleError = null)
    }

    fun setMemo(value: String) {
        _state.value = _state.value.copy(memo = value)
    }

    fun setFirstLogEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(firstLogEnabled = enabled)
    }

    fun setFirstLogDate(millis: Long) {
        _state.value = _state.value.copy(firstLogAtMillis = millis)
    }

    fun save() {
        val current = _state.value
        if (current.saving) return

        var nameError: String? = null
        var cycleError: String? = null

        val trimmedName = current.name.trim()
        if (trimmedName.isEmpty()) nameError = "이름을 입력해주세요"

        val parsedCycle: Int? = if (current.cycleEnabled) {
            val n = current.cycleDays.toIntOrNull()
            if (n == null || n <= 0) {
                cycleError = "주기는 1 이상의 정수여야 합니다"
                null
            } else n
        } else null

        if (nameError != null || cycleError != null) {
            _state.value = current.copy(nameError = nameError, cycleError = cycleError)
            return
        }

        _state.value = current.copy(saving = true, nameError = null, cycleError = null)

        viewModelScope.launch {
            if (itemId == null) {
                itemRepository.create(
                    name = trimmedName,
                    categoryId = current.categoryId,
                    cycleDays = parsedCycle,
                    memo = current.memo,
                    firstLogAt = if (current.firstLogEnabled) current.firstLogAtMillis else null
                )
            } else {
                itemRepository.update(
                    id = itemId,
                    name = trimmedName,
                    categoryId = current.categoryId,
                    cycleDays = parsedCycle,
                    memo = current.memo
                )
            }
            _state.value = _state.value.copy(saving = false, saved = true)
        }
    }
}
