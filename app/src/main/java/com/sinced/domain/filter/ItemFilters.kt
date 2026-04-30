package com.sinced.domain.filter

import com.sinced.domain.model.ItemView

enum class CycleFilter { ALL, WITH_CYCLE, WITHOUT_CYCLE }

enum class SortOption(val label: String) {
    UPCOMING("임박순"),
    OLDEST("오래된 순"),
    NAME("이름"),
    MANUAL("직접 정렬")
}

data class FilterState(
    val cycle: CycleFilter = CycleFilter.ALL,
    val categoryIds: Set<Long?> = emptySet(),
    val sort: SortOption = SortOption.UPCOMING
)

object ItemFilters {

    fun apply(items: List<ItemView>, state: FilterState): List<ItemView> {
        val filtered = items.filter { itemMatches(it, state) }
        return sort(filtered, state.sort)
    }

    private fun itemMatches(view: ItemView, state: FilterState): Boolean {
        val cycleOk = when (state.cycle) {
            CycleFilter.ALL -> true
            CycleFilter.WITH_CYCLE -> view.cycleDays != null
            CycleFilter.WITHOUT_CYCLE -> view.cycleDays == null
        }
        if (!cycleOk) return false

        if (state.categoryIds.isEmpty()) return true
        return state.categoryIds.contains(view.categoryId)
    }

    private fun sort(items: List<ItemView>, sort: SortOption): List<ItemView> = when (sort) {
        SortOption.UPCOMING -> items.sortedWith(
            compareBy<ItemView> { it.daysUntilDue == null }
                .thenBy { it.daysUntilDue ?: Long.MAX_VALUE }
                .thenBy { it.name }
        )
        SortOption.OLDEST -> items.sortedWith(
            compareByDescending<ItemView> { it.lastPerformedAt == null }
                .thenByDescending { it.daysSince ?: -1L }
                .thenBy { it.name }
        )
        SortOption.NAME -> items.sortedBy { it.name }
        SortOption.MANUAL -> items.sortedWith(
            compareBy<ItemView> { it.sortOrder }.thenBy { it.id }
        )
    }
}
