package com.sinced.data.repository

import com.sinced.data.dao.CategoryDao
import com.sinced.data.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {

    fun observeAll(): Flow<List<Category>> = dao.observeAll()

    suspend fun getAll(): List<Category> = dao.getAll()

    suspend fun add(name: String, color: Int?, sortOrder: Int): Long {
        val now = System.currentTimeMillis()
        return dao.insert(
            Category(name = name, color = color, sortOrder = sortOrder, createdAt = now)
        )
    }

    suspend fun update(category: Category) = dao.update(category)

    suspend fun reorder(ordered: List<Category>) {
        val updated = ordered.mapIndexed { index, c -> c.copy(sortOrder = index) }
        dao.updateAll(updated)
    }

    suspend fun delete(category: Category) = dao.delete(category)

    suspend fun countItems(categoryId: Long): Int = dao.countItemsInCategory(categoryId)
}
