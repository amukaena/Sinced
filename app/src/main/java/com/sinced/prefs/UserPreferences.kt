package com.sinced.prefs

import android.content.Context
import com.sinced.domain.filter.CycleFilter
import com.sinced.domain.filter.SortOption

class UserPreferences(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        "sinced_prefs",
        Context.MODE_PRIVATE
    )

    var cycleFilter: CycleFilter
        get() = runCatching {
            CycleFilter.valueOf(prefs.getString(KEY_CYCLE, CycleFilter.ALL.name) ?: CycleFilter.ALL.name)
        }.getOrDefault(CycleFilter.ALL)
        set(value) {
            prefs.edit().putString(KEY_CYCLE, value.name).apply()
        }

    var sortOption: SortOption
        get() = runCatching {
            SortOption.valueOf(prefs.getString(KEY_SORT, SortOption.UPCOMING.name) ?: SortOption.UPCOMING.name)
        }.getOrDefault(SortOption.UPCOMING)
        set(value) {
            prefs.edit().putString(KEY_SORT, value.name).apply()
        }

    var categoryFilter: Set<String>
        get() = prefs.getStringSet(KEY_CATEGORIES, emptySet()) ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_CATEGORIES, value).apply()
        }

    companion object {
        private const val KEY_CYCLE = "cycle_filter"
        private const val KEY_SORT = "sort_option"
        private const val KEY_CATEGORIES = "category_filter"

        const val UNCATEGORIZED_KEY = "__uncategorized__"

        fun encodeCategoryIds(ids: Set<Long?>): Set<String> = ids.map {
            it?.toString() ?: UNCATEGORIZED_KEY
        }.toSet()

        fun decodeCategoryIds(raw: Set<String>): Set<Long?> = raw.map {
            if (it == UNCATEGORIZED_KEY) null else it.toLongOrNull()
        }.filter { it != null || raw.contains(UNCATEGORIZED_KEY) }.toSet()
    }
}
