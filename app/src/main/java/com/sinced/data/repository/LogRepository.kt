package com.sinced.data.repository

import com.sinced.data.dao.LogEntryDao
import com.sinced.data.entity.LogEntry
import kotlinx.coroutines.flow.Flow

class LogRepository(private val dao: LogEntryDao) {

    fun observeByItem(itemId: Long): Flow<List<LogEntry>> = dao.observeByItem(itemId)

    suspend fun add(itemId: Long, performedAt: Long, note: String?): Long {
        val now = System.currentTimeMillis()
        return dao.insert(
            LogEntry(
                itemId = itemId,
                performedAt = performedAt,
                note = note?.takeIf { it.isNotBlank() },
                createdAt = now
            )
        )
    }

    suspend fun update(id: Long, performedAt: Long, note: String?) {
        val existing = dao.getById(id) ?: return
        dao.update(
            existing.copy(
                performedAt = performedAt,
                note = note?.takeIf { it.isNotBlank() }
            )
        )
    }

    suspend fun delete(entry: LogEntry) = dao.delete(entry)
}
