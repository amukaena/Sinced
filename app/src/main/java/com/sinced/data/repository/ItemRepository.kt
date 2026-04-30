package com.sinced.data.repository

import com.sinced.data.dao.ItemDao
import com.sinced.data.dao.LogEntryDao
import com.sinced.data.entity.Item
import com.sinced.data.entity.LogEntry
import com.sinced.data.relation.ItemWithLastLog
import kotlinx.coroutines.flow.Flow

class ItemRepository(
    private val itemDao: ItemDao,
    private val logEntryDao: LogEntryDao
) {

    fun observeItemsWithLastLog(): Flow<List<ItemWithLastLog>> =
        itemDao.observeItemsWithLastLog()

    fun observeById(id: Long): Flow<Item?> = itemDao.observeById(id)

    suspend fun getById(id: Long): Item? = itemDao.getById(id)

    suspend fun create(
        name: String,
        categoryId: Long?,
        cycleDays: Int?,
        memo: String?,
        firstLogAt: Long? = null,
        firstLogNote: String? = null
    ): Long {
        val now = System.currentTimeMillis()
        val itemId = itemDao.insert(
            Item(
                name = name.trim(),
                categoryId = categoryId,
                cycleDays = cycleDays,
                memo = memo?.takeIf { it.isNotBlank() },
                createdAt = now
            )
        )
        if (firstLogAt != null) {
            logEntryDao.insert(
                LogEntry(
                    itemId = itemId,
                    performedAt = firstLogAt,
                    note = firstLogNote?.takeIf { it.isNotBlank() },
                    createdAt = now
                )
            )
        }
        return itemId
    }

    suspend fun update(
        id: Long,
        name: String,
        categoryId: Long?,
        cycleDays: Int?,
        memo: String?
    ) {
        val existing = itemDao.getById(id) ?: return
        itemDao.update(
            existing.copy(
                name = name.trim(),
                categoryId = categoryId,
                cycleDays = cycleDays,
                memo = memo?.takeIf { it.isNotBlank() }
            )
        )
    }

    suspend fun archive(id: Long) {
        itemDao.setArchived(id, System.currentTimeMillis())
    }

    suspend fun unarchive(id: Long) {
        itemDao.setArchived(id, null)
    }

    suspend fun delete(id: Long) {
        val existing = itemDao.getById(id) ?: return
        itemDao.delete(existing)
    }
}
