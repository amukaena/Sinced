package com.sinced.data.seed

import com.sinced.data.dao.CategoryDao
import com.sinced.data.entity.Category
import com.sinced.ui.theme.CategoryPalette

object DefaultCategories {

    suspend fun seedIfEmpty(dao: CategoryDao) {
        if (dao.count() > 0) return
        val now = System.currentTimeMillis()
        val seeds = listOf(
            Category(name = "청소", color = CategoryPalette.CYAN, sortOrder = 0, createdAt = now),
            Category(name = "구독", color = CategoryPalette.PURPLE, sortOrder = 1, createdAt = now),
            Category(name = "교체/소모품", color = CategoryPalette.AMBER, sortOrder = 2, createdAt = now),
            Category(name = "건강", color = CategoryPalette.GREEN, sortOrder = 3, createdAt = now)
        )
        dao.insertAll(seeds)
    }
}
